/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package org.ob.wso2.common;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.qualified.QCStatement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Optional;

/**
 * PSD2QCStatement class
 */
public class PSD2QCStatement {

    private static final ASN1ObjectIdentifier psd2QcStatementOid = new ASN1ObjectIdentifier("0.4.0.19495.2");

    public static PSD2QCType getPsd2QCType(X509Certificate cert) throws Exception {

        byte[] extensionValue = cert.getExtensionValue(Extension.qCStatements.getId());  //1.3.6.1.5.5.7.1.3
        if (extensionValue == null) {
            throw new Exception("X509 V3 Extensions not found in the certificate.");
        }

        QCStatement qcStatement = extractQCStatement(extensionValue);

        ASN1Encodable statementInfo = qcStatement.getStatementInfo();

        return PSD2QCType.getInstance(statementInfo);

    }

    private static QCStatement extractQCStatement(byte[] extensionValue) throws Exception {

        ASN1Sequence qcStatements;
        try {
            try (ASN1InputStream derAsn1InputStream = new ASN1InputStream(new ByteArrayInputStream(extensionValue))) {
                DEROctetString oct = (DEROctetString) (derAsn1InputStream.readObject());
                try (ASN1InputStream asn1InputStream = new ASN1InputStream(oct.getOctets())) {
                    qcStatements = (ASN1Sequence) asn1InputStream.readObject();
                }
            }
        } catch (IOException e) {
            throw new Exception("Invalid QCStatement in the certificate.");
        }

        if (qcStatements.size() <= 0) {
            throw new Exception("QCStatements not found in the certificate.");
        }

        ASN1Encodable object = qcStatements.getObjectAt(0);
        if (object.toASN1Primitive() instanceof ASN1ObjectIdentifier) {
            return getSingleQcStatement(qcStatements);
        }

        return extractPsd2QcStatement(qcStatements)
                .orElseThrow(() -> new Exception("No PSD2 QCStatement found in the certificate."));
    }

    private static QCStatement getSingleQcStatement(ASN1Sequence qcStatements) throws Exception {

        QCStatement qcStatement = QCStatement.getInstance(qcStatements);
        if (!psd2QcStatementOid.getId().equals(qcStatement.getStatementId().getId())) {
            System.out.println("Invalid QC statement type in psd2 certificate. expected [" +
                        psd2QcStatementOid.getId() + "] but found [" + qcStatement.getStatementId().getId() + "]");
            throw new Exception("No PSD2 QCStatement found in the certificate.");
        }

        return qcStatement;
    }

    private static Optional<QCStatement> extractPsd2QcStatement(ASN1Sequence qcStatements) {

        Iterator iterator = qcStatements.iterator();

        while (iterator.hasNext()) {
            QCStatement qcStatement = QCStatement.getInstance(iterator.next());
            if (qcStatement != null && qcStatement.getStatementId().getId().equals(psd2QcStatementOid.getId())) {
                return Optional.of(qcStatement);
            }
        }

        return Optional.empty();
    }
}
