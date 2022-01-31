/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package org.ob.wso2.util;

import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.ob.wso2.constants.IdentityConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Common Utilities.
 */
public class OBIdentityUtil {
    private static final String OIDC_IDP_ENTITY_ID = "IdPEntityId";
    private static final String OAUTH2_TOKEN_EP_URL = "OAuth2TokenEPUrl";
    private static final String OIDC_ID_TOKEN_ISSUER_ID = "OAuth.OpenIDConnect.IDTokenIssuerID";
    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT = "-----END CERTIFICATE-----";
    private static final String X509 = "X.509";


    /**
     * Return Certificate for give Certificate Content.
     *
     * @param content Certificate Content
     * @return X509Certificate
     * @throws CertificateException
     */
    public static X509Certificate parseCertificate(String content) throws CertificateException {

        // Trim extra spaces
        String decodedContent = content.trim();

        // Remove Certificate Headers
        byte[] decoded = Base64.getDecoder().decode(decodedContent
                        .replaceAll(IdentityConstants.BEGIN_CERT, StringUtils.EMPTY)
                        .replaceAll(IdentityConstants.END_CERT, StringUtils.EMPTY).trim()
                                                   );

        return (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(decoded));
    }
}
