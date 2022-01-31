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

package org.ob.wso2.constants;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Constants required for Application Identity Retriever.
 */
public class IdentityConstants {

    public static final String IDENTITY_ADMIN_SERVICE = "IdentityApplicationManagementService";
    public static final String OAUTH_ADMIN_SERVICE = "OAuthAdminService";
    public static final String JWKS_PROPERTY_NAME = "jwksURI";
    public static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERT = "-----END CERTIFICATE-----";
    public static final String PRODUCTION = "PRODUCTION";
    public static final String SANDBOX = "SANDBOX";
    public static final String TOKEN_ENDPOINT = "/token";
    public static final String AUTHORIZE_ENDPOINT = "/authorize";
    public static final String JWKS_ENDPOINT = "/jwks";
    public static final String USERINFO_ENDPOINT = "/userinfo";
    public static final String REVOKE_ENDPOINT = "/revoke";
    public static final String TOKEN_INTROSPECT_ENDPOINT = "/token/introspect";
    public static final String PAR_ENDPOINT = "/par";
    public static final String REGISTER_ENDPOINT = "/register";
    public static final String REGISTER_CLIENT_ID_ENDPOINT = "/register/{ClientId}";
    public static final String WELL_KNOWN_ENDPOINT = "/.well-known/openid-configuration";
    public static final String CDR_ARRANGEMENNT_ENDPOINT = "/{cdrArrangementId}";
    public static final String DISCOVERY_OUTAGES_ENDPOINT = "/discovery/outages";
    public static final String DISCOVERY_STATUS_ENDPOINT = "/discovery/status";

    public static final String REST_API_CONTEXT = "REST_API_CONTEXT";
    public static final String CLIENT_ASSSERTION = "client_assertion";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String TRANSPORT_HEADERS = "TRANSPORT_HEADERS";
    public static final String AUTHORIZATION = "Authorization";

    public static final String KEYSTORE_LOCATION_CONF_KEY = "Security.KeyStore.Location";
    public static final String KEYSTORE_PASS_CONF_KEY = "Security.KeyStore.Password";

    /**
     * CertificateType enum
     */
    public static enum CertificateType {
        TRANSPORT, SIGNING
    }

    /**
     * EnvironmentType enum
     */
    public static enum EnviromentType {
        SANDBOX, PRODUCTION, DEFAULT
    }

    /**
     * Use values for JWKS key set retrieval.
     *
     * Default values defined by specification
     * <ul>
     *     <li>enc</li>
     *     <li>tls</li>
     * </ul>
     *
     * @see <a href="https://tools.ietf.org/html/rfc7517#section-4.2">RFC7517 Key Use Values</a>
     */
    public static final Map<CertificateType, String[]> USE_TYPE_VALUE_MAP;

    static {
        Map<CertificateType, String[]> useMap = new EnumMap<>(CertificateType.class);

        useMap.put(CertificateType.SIGNING, new String[]{"sig"});
        useMap.put(CertificateType.TRANSPORT, new String[]{"enc", "tls"});

        USE_TYPE_VALUE_MAP = Collections.unmodifiableMap(useMap);
    }



}
