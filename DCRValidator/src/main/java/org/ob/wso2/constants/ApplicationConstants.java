/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package org.ob.wso2.constants;

/**
 * Field names used as application attributes.
 */
public class ApplicationConstants {
    public static final String SANDBOX_ENV = "SANDBOX";
    public static final String PRODUCTION_ENV = "PRODUCTION";
    public static final String SOFTWARE_JWKS_ENDPOINT = "software_jwks_endpoint";
    public static final String JWKS_URI = "jwks_uri";
    public static final String ID_TOKEN_ENCRYPTED_RESPONSE_ALG = "id_token_encrypted_response_alg";
    public static final String ID_TOKEN_ENCRYPTED_RESPONSE_ENC = "id_token_encrypted_response_enc";
    public static final String ID_TOKEN_SIGNED_RESPONSE_ALG = "id_token_signed_response_alg";

    //Application attributes
    public static final String KEY_STATE = "keyState";
    public static final String CONSUMER_KEY = "consumerKey";
    public static final String APP_ID = "appId";
    public static final String CLIENT_ID_ISSUED_AT = "clientIdIssuedAt";
    public static final String CONSUMER_SECRET = "consumerSecret";

    //Regulatory attributes
    public static final String REGULATORY = "regulatory";

    //Keymanager attributes
    public static final String AUTHENTICATION_ADMIN_SERVICE = "AuthenticationAdmin";
    public static final String IDENTITY_APPLICATION_MGT_SERVICE = "IdentityApplicationManagementService";
}
