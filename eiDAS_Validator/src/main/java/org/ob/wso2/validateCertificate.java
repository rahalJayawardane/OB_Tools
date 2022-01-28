package org.ob.wso2;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.util.io.pem.PemReader;
import org.ob.wso2.common.CertificateContent;
import org.ob.wso2.common.PSD2QCStatement;
import org.ob.wso2.common.PSD2QCType;
import org.ob.wso2.common.PSPRole;
import org.ob.wso2.common.PSPRoles;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

public class validateCertificate {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please give the PEM file location");
            System.exit(0);
        }
        String fileName = args[0];
        InputStream inputStream = new FileInputStream(fileName);

        if (inputStream != null) {
            BufferedInputStream bufferInput = new BufferedInputStream(inputStream);
            ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();
            for (int result = bufferInput.read(); result != -1; result = bufferInput.read()) {
                bufferOutput.write((byte) result);
            }
            X509Certificate certificate = convertStringToX509Cert(bufferOutput.toString("UTF-8"));
            CertificateContent tppData = extract(certificate);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(tppData));
        } else {
            throw new FileNotFoundException(fileName + " not found in the classpath");
        }

    }

    private static X509Certificate convertStringToX509Cert(String certificate) throws Exception{
        InputStream targetStream = new ByteArrayInputStream(certificate.getBytes());
        return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(targetStream);
    }

    public static CertificateContent extract(X509Certificate cert) throws Exception {

        if (cert == null) {
            throw new Exception("Invalid QCStatement in the certificate.");
        }

        CertificateContent tppCertData = new CertificateContent();

        tppCertData.setNotAfter(cert.getNotAfter());
        tppCertData.setNotBefore(cert.getNotBefore());

        PSD2QCType psd2QcType = PSD2QCStatement.getPsd2QCType(cert);
        PSPRoles pspRoles = psd2QcType.getPspRoles();
        List<PSPRole> rolesArray = pspRoles.getRoles();

        List<String> roles = new ArrayList<>();

        for (PSPRole pspRole : rolesArray) {
            roles.add(pspRole.getPsd2RoleName());
        }
        tppCertData.setPspRoles(roles);

        tppCertData.setNcaName(psd2QcType.getnCAName().getString());
        tppCertData.setNcaId(psd2QcType.getnCAId().getString());

        try {
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();

            tppCertData.setPspAuthorisationNumber(getNameValueFromX500Name(x500name, BCStyle.ORGANIZATION_IDENTIFIER));
            tppCertData.setName(getNameValueFromX500Name(x500name, BCStyle.CN));

        } catch (CertificateEncodingException e) {
            throw new Exception("Invalid QCStatement in the certificate.");

        }
        return tppCertData;

    }

    private static String getNameValueFromX500Name(org.bouncycastle.asn1.x500.X500Name x500Name, ASN1ObjectIdentifier asn1ObjectIdentifier) {

        if (ArrayUtils.contains(x500Name.getAttributeTypes(), asn1ObjectIdentifier)) {
            return IETFUtils.valueToString(x500Name.getRDNs(asn1ObjectIdentifier)[0].getFirst().getValue());
        } else {
            return "";
        }
    }

}
