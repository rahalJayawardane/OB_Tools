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
 * Field names used for AU specific validations.
 */
public class UKValidationConstants {

    //scopes
    public static final String SCOPE_ACCOUNT = "accounts";
    public static final String SCOPE_PAYMENT = "payments";
    public static final String SCOPE_FUNDSCONFIRMATION = "fundsconfirmations";
    public static final String SCOPE_OPENID = "openid";

    //roles
    public static final String ROLE_AISP = "AISP";
    public static final String ROLE_PISP = "PISP";
    public static final String ROLE_CBPII = "CBPII";

    //response types
    public static final String CODE = "code";
    public static final String CODE_ID_TOKEN = "code id_token";

    //grant types
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String REFRESH_TOKEN = "refresh_token";

    //environments
    public static final String SOFTWARE_ENVIRONMENT_PROD = "production";
    public static final String SOFTWARE_ENVIRONMENT_SAND = "sandbox";

    //jwks urls
    public static final String OPENBANKING_JWKS_SANDBOX =
            "https://keystore.openbankingtest.org.uk/keystore/openbanking.jwks";
    public static final String OPENBANKING_JWKS_PRODUCTION =
            "https://keystore.openbanking.org.uk/keystore/openbanking.jwks";

    //application types
    public static final String APPLICATION_WEB = "web";
    public static final String APPLICATION_MOBILE = "mobile";

    //signing algorithms
    public static final String SIGNING_ALGO_PS256 = "PS256";
    public static final String SIGNING_ALGO_ES256 = "ES256";

    //token endpoint auth methods
    public static final String PRIVATE_KEY_JWT = "private_key_jwt";
    public static final String CLIENT_SECRET_JWT = "client_secret_jwt";
}
