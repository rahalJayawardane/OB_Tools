/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.finance.open.banking.common.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.wso2.finance.open.banking.common.config.uk.UKSpecConfigParser;
import com.wso2.finance.open.banking.common.exception.OpenBankingException;
import com.wso2.finance.open.banking.common.identity.IdentityConstants;
import com.wso2.finance.open.banking.common.identity.ServiceProviderRetiriever;
import com.wso2.finance.open.banking.common.identity.retirevers.ServerIdentityRetriever;
import com.wso2.finance.open.banking.common.identity.retirevers.sp.ServiceProviderRetrieverFactory;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.stub.OAuthAdminServiceIdentityOAuthAdminException;
import org.wso2.carbon.identity.oauth.stub.OAuthAdminServiceStub;
import org.wso2.carbon.identity.oauth.stub.dto.OAuthConsumerAppDTO;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT Utils.
 */
public class JWTUtils {
    /**
     * Enums used in decoding jwt.
     */
    public enum JwtPart {
        header, body
    }

    private static Log log = LogFactory.getLog(JWTUtils.class);
    private static final JWSAlgorithm DEFAULT_ALGORITHM = JWSAlgorithm.PS256;

    /**
     * Decode request JWT.
     *
     * @param jwtToken jwt sent by the tpp
     * @param jwtPart  expected jwt part (header, body)
     * @return json object containing requested jwt part
     */
    public static JSONObject decodeRequestJWT(String jwtToken, JwtPart jwtPart)
            throws OpenBankingException {
        Optional<JSONObject> jsonObject = Optional.empty();

        try {
            JWSObject plainObject = JWSObject.parse(jwtToken);

            if (JwtPart.header.equals(jwtPart)) {
                jsonObject = Optional.ofNullable(plainObject.getHeader().toJSONObject());
            } else if (JwtPart.body.equals(jwtPart)) {
                jsonObject = Optional.ofNullable(plainObject.getPayload().toJSONObject());
            }
        } catch (ParseException e) {
            log.error("Error occurred while parsing the jwt token");
            throw new OpenBankingException("Error occurred while parsing the jwt token", e);
        }
        return jsonObject.orElseThrow(() -> new OpenBankingException(
                "Error occurred while parsing the jwt token"));
    }

    /**
     * Validate the signed JWT by querying a jwks.
     *
     * @param jwtString signed json web token
     * @param jwksUri   endpoint displaying the key set for the signing certificates
     * @param algorithm the signing algorithm for jwt
     * @return true if signature is valid
     */
    public static boolean validateJWTSignature(String jwtString, String jwksUri, String algorithm,
                                               UKSpecConfigParser parser)
            throws ParseException, BadJOSEException, JOSEException, MalformedURLException {

        int defaultConnectionTimeout = 3000;
        int defaultReadTimeout = 3000;
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWT jwt = JWTParser.parse(jwtString);
        // set the Key Selector for the jwks_uri.
        Map<String, RemoteJWKSet<SecurityContext>> jwkSourceMap = new ConcurrentHashMap<>();
        RemoteJWKSet<SecurityContext> jwkSet = jwkSourceMap.get(jwksUri);
        if (jwkSet == null) {
            int connectionTimeout = Integer.parseInt(parser.getConnectionTimeout());
            int readTimeout = Integer.parseInt(parser.getReadTimeout());
            int sizeLimit = RemoteJWKSet.DEFAULT_HTTP_SIZE_LIMIT;
            if (connectionTimeout == 0 && readTimeout == 0) {
                connectionTimeout = defaultConnectionTimeout;
                readTimeout = defaultReadTimeout;
            }
            DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever(
                    connectionTimeout,
                    readTimeout,
                    sizeLimit);
            jwkSet = new RemoteJWKSet<>(new URL(jwksUri), resourceRetriever);
            jwkSourceMap.put(jwksUri, jwkSet);
        }
        // The expected JWS algorithm of the access tokens (agreed out-of-band).
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.parse(algorithm);
        //Configure the JWT processor with a key selector to feed matching public RSA keys sourced from the JWK set URL.
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, jwkSet);
        jwtProcessor.setJWSKeySelector(keySelector);
        // Process the token, set optional context parameters.
        SimpleSecurityContext securityContext = new SimpleSecurityContext();
        jwtProcessor.process((SignedJWT) jwt, securityContext);
        return true;
    }

    /**
     * Get signed JWT for payload.
     *
     * @param payloadJsonObject payload as a JSON object
     * @param signatureAlgorithm JWT signing algorithm
     * @return signed JWT.
     * @throws OpenBankingException on signing exception.
     */
    public static String generateJWTFromJSON(org.json.JSONObject payloadJsonObject,
                                             JWSAlgorithm signatureAlgorithm, String clientId)
            throws OpenBankingException {

        JWSAlgorithm signAlgorithm = signatureAlgorithm == null ? DEFAULT_ALGORITHM : signatureAlgorithm;
        String payload = payloadJsonObject.toString();
        Optional<String> certAlias = IdentityConstants.PRIMARY_SIGNING_CERT_ALIAS;
        Optional<Key> key = Optional.empty();
        JWSHeader.Builder headerBuilder = new JWSHeader.Builder(signAlgorithm);
        X509Certificate certificate = null;

        try {
            ServiceProviderRetiriever serviceProviderRetrieverInstance =
                    new ServiceProviderRetrieverFactory().getServiceProviderRetriever();

            OAuthAdminServiceStub oAuthAdminServiceStub =
                    serviceProviderRetrieverInstance.getOauthAdminServiceStub();
            if (oAuthAdminServiceStub != null) {
                OAuthConsumerAppDTO oAuthConsumerAppDTO =
                        oAuthAdminServiceStub.getOAuthApplicationData(clientId);
                if (oAuthConsumerAppDTO != null) {
                    String applicationName = oAuthConsumerAppDTO.getApplicationName();
                    if (applicationName.endsWith(IdentityConstants.PRODUCTION)) {
                        key = ServerIdentityRetriever.getCertificateByAlias(certAlias);
                    } else if (applicationName.endsWith(IdentityConstants.SANDBOX)) {
                        certAlias = IdentityConstants.SANDBOX_SIGNING_CERT_ALIAS;
                        key = ServerIdentityRetriever.getCertificateByAlias(certAlias);
                    } else {
                        //Default is returned when the application name does not contains sandbox or production
                        key = ServerIdentityRetriever.getPrimaryCertificate(IdentityConstants.CertificateType.SIGNING);
                    }
                    certificate = (X509Certificate) ServerIdentityRetriever.retrieveCertFromAlias(certAlias.get());

                }
            }
        } catch (OAuthAdminServiceIdentityOAuthAdminException e) {
            log.error("Error while trying to retrieve OauthAdminService.", e);
        } catch (OpenBankingException e) {
            log.error("Error while building the message context of token request.", e);
        } catch (RemoteException e) {
            log.error("Error while retrieving application data from OauthAdminStub. ", e);
        } catch (KeyStoreException e) {
            log.error("Error while retrieving signing certificate", e);
        }

        if (!key.isPresent()) {
            log.warn("Server signing key for alias " + certAlias.get() + " is missing");
            return StringUtils.EMPTY;
        }

        if (certificate != null) {
            try {
                headerBuilder.keyID(OAuth2Util.getKID(OAuth2Util.getThumbPrint(certificate, certAlias.get()),
                        signAlgorithm));
            } catch (IdentityOAuth2Exception e) {
                throw new OpenBankingException("Unable to retrieve thumbprint of the certificate", e);
            }
        }

        JWSObject jwsObject = new JWSObject(headerBuilder.build(), new Payload(payload));
        try {
            jwsObject.sign(new RSASSASigner((PrivateKey) key.get()));
        } catch (JOSEException e) {
            throw new OpenBankingException("Unable to sign JWT with signer", e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Converted JWT from JSON response of :" +
                    payloadJsonObject.get("ConsentId") + "is " + jwsObject.serialize());
        }

        return jwsObject.serialize();
    }
}
