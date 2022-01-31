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
 * Field names used in request validation errors.
 */
public final class ErrorConstants {

    //parameter related constants
    public static final String AUD_NULL = "Mandatory parameter aud is null";
    public static final String EXP_NULL = "Mandatory parameter exp is null";
    public static final String REDIRECT_URIS_EMPTY = "Mandatory parameter redirect_uris is empty";
    public static final String REDIRECT_URIS_NULL = "Mandatory parameter redirect_uris is null";
    public static final String APPLICATION_TYPE_NULL = "Mandatory parameter application_type is null";
    public static final String GRANT_TYPES_EMPTY = "Mandatory parameter grant_types is empty";
    public static final String GRANT_TYPES_NULL = "Mandatory parameter grant_types is null";
    public static final String IAT_NULL = "Mandatory parameter iat is null";
    public static final String ISS_NULL = "Mandatory parameter iss is null";
    public static final String JTI_NULL = "Mandatory parameter jti is null";
    public static final String RESPONSE_TYPES_EMPTY = "Mandatory parameter response_types is empty";
    public static final String RESPONSE_TYPES_NULL = "Mandatory parameter response_types is null";
    public static final String SSA_NULL = "Mandatory parameter software_statement is null";
    public static final String TOKEN_ENDPOINT_AUTH_METHOD_NULL =
            "Mandatory parameter token_endpoint_auth_method is null";
    public static final String TOKEN_ENDPOINT_AUTH_SIGNING_ALG_NULL =
            "Mandatory parameter token_endpoint_auth_signing_alg is null";
    public static final String ID_TOKEN_SIGNED_RESPONSE_ALG_NULL =
            "Mandatory parameter id_token_signed_response_alg is null";
    public static final String ID_TOKEN_ENCRYPTED_RESPONSE_ENC_NULL =
            "Mandatory parameter id_token_encrypted_response_enc is null";
    public static final String ID_TOKEN_ENCRYPTED_RESPONSE_ALG_NULL =
            "Mandatory parameter id_token_encrypted_response_alg is null";
    public static final String ORG_ID_NULL = "Mandatory parameter org_id is null";
    public static final String ORG_NAME_NULL = "Mandatory parameter org_name is null";
    public static final String CLIENT_NAME_NULL = "Mandatory parameter client_name is null";
    public static final String CLIENT_DESCRIPTION_NULL = "Mandatory parameter client_description is null";
    public static final String CLIENT_URI_NULL = "Mandatory parameter client_uri is null";
    public static final String LOGO_URI_NULL = "Mandatory parameter logo_uri is null";
    public static final String JWKS_URI_NULL = "Mandatory parameter jwks_uri is null";
    public static final String REVOCATION_URI_NULL = "Mandatory parameter revocation_uri is null";
    public static final String RECIPIENT_BASE_URI_NULL = "Mandatory parameter recipient_base_uri is null";
    public static final String SOFTWARE_ID_NULL = "Mandatory parameter software_id is null";
    public static final String SOFTWARE_ROLES_NULL = "Mandatory parameter software_roles is null";
    public static final String SCOPE_NULL = "Mandatory parameter scope is null";

    //process related constants
    public static final String ERROR_REVOKING_ACCESS_TOKEN =
            "Error occurred while revoking the access token";
    public static final String ERROR_INITIALIZING_DAO =
            "Error occurred while initializing data access layer services";
    public static final String PAYLOAD_VALIDATION_FAILED =
            "Request payload validation failed with following error: ";
    public static final String SSA_VALIDATION_FAILED = "SSA validation failed.";
}
