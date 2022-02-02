package org.ob.wso2.util;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Utility methods related to X509 certificate operations.
 */
public class CertificateUtil {

    private static final String HASH_ALGO = "SHA-1";
    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT = "-----END CERTIFICATE-----";
    private static final String X509 = "X.509";

    /**
     * check whether the client TLS certificate chains
     * to a certificate located in the SSA 'org_jwks_endpoint' attribute.
     *
     * @param jwksEndpoint    the software_jwks_endpoint sent in the software statement
     * @param x509Certificate the client certificate sent by the tpp with the request
     */
    public static boolean verifyCertChain(String jwksEndpoint, X509Certificate x509Certificate)
            throws IOException {

        boolean isCertValid = false;
        JWKSet keys = getListedKeys(jwksEndpoint);
        Base64URL tlsCertThumbPrint = null;
        if (x509Certificate != null) {
            tlsCertThumbPrint = X509CertUtils.computeSHA256Thumbprint(x509Certificate);
        }
        List<X509Certificate> certificates = null;
        //retrieve the tls certificate list from the jwks endpoint
        if (tlsCertThumbPrint != null) {
            certificates = retrieveCertificates(keys, jwksEndpoint, "TLS");
            //check whether tls cert in request match tls cert in jwks endpoint
            for (X509Certificate certificate : certificates) {
                Base64URL certThumbPrint = X509CertUtils.computeSHA256Thumbprint(certificate);
                if (certThumbPrint != null &&
                        tlsCertThumbPrint.decodeToString().equals(certThumbPrint.decodeToString())) {
                    isCertValid = true;
                    break;
                }
            }
        }
        return isCertValid;
    }

    /**
     * Get key identifier (kid) from X509 certificate.
     */
    public static String getKidFromCertificate(X509Certificate certificate) {

        String x5t = "";
        RSAPublicKey rsaPublicKey = (RSAPublicKey) certificate.getPublicKey();
        JWK jwk = new RSAKey.Builder(rsaPublicKey).keyID(UUID.randomUUID().toString()).build();
        try {
            x5t = jwk.computeThumbprint(HASH_ALGO).toString();
        } catch (JOSEException e) {
            System.out.println("Error while computing thumbprint: " + e);
        }
        return x5t;
    }

    /**
     * Retrieve the json web key set corresponding to a particular endpoint.
     *
     * @param jwksUrl the jwks location endpoint
     */
    private static JWKSet getListedKeys(String jwksUrl) {

        JWKSet jwkSet = new JWKSet();
        try {
            jwkSet = JWKSet.load(new URL(jwksUrl));
        } catch (MalformedURLException e) {
            System.out.println("ERROR: Malformed jwks uri: " + e);
        } catch (ParseException e) {
            System.out.println(String.format("ERROR: A valid jwkSet could not be found in the given jwks url %s", jwksUrl));
        } catch (IOException e) {
            System.out.println("ERROR: Error while loading the jwk set: " + e);
        }
        return jwkSet;
    }

    /**
     * Check whether the certificate used to sign the jwt is revoked.
     *
     * @param kid  the kid for which the jwks should be retrieved
     * @param jwks the json web key set endpoint send in  the software statmenet assertion
     */
    public static boolean checkJWKRevoked(String kid, String jwks) {

        boolean isRevoked = false;
        JWKSet jwkSet = getListedKeys(jwks);
        if (jwkSet != null) {
            for (JWK key : jwkSet.getKeys()) {
                if (key.getKeyID().equals(kid)) {
                    isRevoked = true;
                }
            }
        }
        return isRevoked;
    }

    /**
     * obtain certificate chain from jwks
     *
     * @param jwks jwks endpoint for software product in ssa
     * @param keys   key set obtained from the jwks end point
     */
    public static List<X509Certificate> retrieveCertificates(JWKSet keys, String jwks, String keyUse)
            throws IOException {

        List<X509Certificate> certificates = new ArrayList<>();
        Iterator jwkIterator = keys.getKeys().iterator();
        while (jwkIterator.hasNext()) {
            JWK jwk = (JWK) jwkIterator.next();
            if (keyUse.equalsIgnoreCase(jwk.getKeyUse().toString())) {
                X509Certificate x509Certificate = getCertificateFromJWKS(jwks, jwk.getKeyID());
                if (x509Certificate != null) {
                    certificates.add(x509Certificate);
                }
            }
        }
        return certificates;
    }


    /**
     * Retrieve the certificate from the JWKS endpoint
     *
     * @param jwksEndPoint JWKS endpoint
     * @param keyID        key ID of the certificate
     * @return the certificate that matches with the key ID
     */
    public static X509Certificate getCertificateFromJWKS(String jwksEndPoint, String keyID) {

        JWKSet keys = getListedKeys(jwksEndPoint);
        JWK key = keys.getKeyByKeyId(keyID);
        return key.getParsedX509CertChain().get(0);

    }

    /**
     * method to retrieve the certificate from jwks
     *
     * @param key JWK object related to the kid
     * @return x509certificate object
     */
    public static X509Certificate getCertificateFromJWKS(JWK key) {

        StringBuilder content = new StringBuilder();
        X509Certificate certificate = null;

        // create a url object
        URL url = null;
        URLConnection urlConnection = null;
        try {
            if (key.getX509CertURL() == null) {
                return null;
            }
            url = new URL(key.getX509CertURL().toString());
            urlConnection = url.openConnection();
        } catch (IOException e) {
            System.out.println("Error occurred while trying to retrieve the transport certificate from jwks endpoint: " + e);
            return certificate;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()
                , StandardCharsets.UTF_8))) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            byte[] decoded = Base64.getDecoder().decode(content.toString().replaceAll(BEGIN_CERT, "")
                    .replaceAll(END_CERT, ""));
            certificate = (X509Certificate) CertificateFactory.getInstance(X509)
                    .generateCertificate(new ByteArrayInputStream(decoded));
        } catch (IOException e) {
            System.out.println("Exception occurred while trying to retrieve certificate for  token binding: " + e);
        } catch (java.security.cert.CertificateException e) {
            System.out.println("Error occurred while creating X50nCertificate object: " + e);
        }
        return certificate;
    }
}
