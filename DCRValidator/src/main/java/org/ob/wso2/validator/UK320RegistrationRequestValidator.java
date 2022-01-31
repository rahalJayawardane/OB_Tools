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
package com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.validator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.wso2.finance.open.banking.common.config.CommonConfigParser;
import com.wso2.finance.open.banking.common.config.uk.UKSpecConfigParser;
import com.wso2.finance.open.banking.common.util.JWTUtils;
import com.wso2.finance.open.banking.dynamic.client.registration.common.constants.ErrorConstants;
import com.wso2.finance.open.banking.dynamic.client.registration.common.exception.DynamicClientRegistrationException;
import com.wso2.finance.open.banking.dynamic.client.registration.common.util.CertificateUtil;
import com.wso2.finance.open.banking.dynamic.client.registration.common.util.CommonUtil;
import com.wso2.finance.open.banking.dynamic.client.registration.common.util.DCRUtil;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.constants.UKValidationConstants;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model.UK320ClientRegistrationRequest;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model.UK320RegistrationError;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model.UK320SoftwareStatementBody;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model.UK320SoftwareStatementHeader;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.util.UKErrorResponseUtil;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.util.UKValidationUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * Request validator class for UK DCR v3.2.
 */
public class UK320RegistrationRequestValidator {
    private static final Log log = LogFactory.getLog(UK320RegistrationRequestValidator.class);

    /**
     * Mandatory parameter checks for DCR v3.2.
     *
     * @param registrationRequest - Registration request object
     */
    public static void validateMandatoryParameters(
            UK320ClientRegistrationRequest registrationRequest) throws DynamicClientRegistrationException {

        //null checks
        if (StringUtils.isBlank(registrationRequest.getIss())) {
            throw new DynamicClientRegistrationException(ErrorConstants.ISS_NULL);
        }

        if (registrationRequest.getIat() == null) {
            throw new DynamicClientRegistrationException(ErrorConstants.IAT_NULL);
        }

        if (registrationRequest.getExp() == null) {
            throw new DynamicClientRegistrationException(ErrorConstants.EXP_NULL);
        }

        if (StringUtils.isBlank(registrationRequest.getJti())) {
            throw new DynamicClientRegistrationException(ErrorConstants.JTI_NULL);
        }

        if (StringUtils.isBlank(registrationRequest.getAud())) {
            throw new DynamicClientRegistrationException(ErrorConstants.AUD_NULL);
        }

        if (registrationRequest.getRedirectUris() == null) {
            throw new DynamicClientRegistrationException(ErrorConstants.REDIRECT_URIS_NULL);
        }

        if (registrationRequest.getRedirectUris().isEmpty()) {
            throw new DynamicClientRegistrationException(ErrorConstants.REDIRECT_URIS_EMPTY);
        }

        if (StringUtils.isBlank(registrationRequest.getApplicationType())) {
            throw new DynamicClientRegistrationException(ErrorConstants.APPLICATION_TYPE_NULL);
        }

        if (StringUtils.isBlank(registrationRequest.getTokenEndpointAuthMethod())) {
            throw new DynamicClientRegistrationException(ErrorConstants.TOKEN_ENDPOINT_AUTH_METHOD_NULL);
        } else {
            //signing algorithm should be present if auth_method is private_key_jwt or client_secret_jwt
            String tokenEndpointAuthMethod = registrationRequest.getTokenEndpointAuthMethod();
            if ((UKValidationConstants.PRIVATE_KEY_JWT.equals(tokenEndpointAuthMethod) ||
                    UKValidationConstants.CLIENT_SECRET_JWT.equals(tokenEndpointAuthMethod)) &&
                    StringUtils.isBlank(registrationRequest.getTokenEndpointAuthSigningAlg())) {
                throw new DynamicClientRegistrationException(ErrorConstants.TOKEN_ENDPOINT_AUTH_SIGNING_ALG_NULL);

            }
        }

        if (registrationRequest.getGrantTypes() == null) {
            throw new DynamicClientRegistrationException(ErrorConstants.GRANT_TYPES_NULL);

        } else if (registrationRequest.getGrantTypes().isEmpty()) {
            throw new DynamicClientRegistrationException(ErrorConstants.GRANT_TYPES_EMPTY);
        }

        if (registrationRequest.getResponseTypes() == null) {
            throw new DynamicClientRegistrationException(ErrorConstants.RESPONSE_TYPES_NULL);

        } else if (registrationRequest.getResponseTypes().isEmpty()) {
            throw new DynamicClientRegistrationException(ErrorConstants.RESPONSE_TYPES_EMPTY);

        }

        if (StringUtils.isBlank(registrationRequest.getSoftwareStatementPayload())) {
            throw new DynamicClientRegistrationException(ErrorConstants.SSA_NULL);
        }
    }

    /**
     * Validate registration payload.
     *
     * @param requestJWT               - registration request jwt
     * @param softwareStatementHeader  - SSA header object
     * @param softwareStatementPayload - SSA body object
     * @return - UK320RegistrationError
     */
    public static UK320RegistrationError validateRegistrationPayload(
            String requestJWT, UK320ClientRegistrationRequest registrationRequest,
            UK320SoftwareStatementHeader softwareStatementHeader, UK320SoftwareStatementBody softwareStatementPayload) {

        //check whether SSA jwt is revoked
        if (CertificateUtil.checkJWKRevoked(softwareStatementHeader.getKid(),
                softwareStatementPayload.getSoftwareJwksRevokedEndpoint())) {
            log.error("SSA is revoked");
            return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError.ErrorCodeEnum.INVALID_SOFTWARE_STATEMENT,
                    "Provided SSA is revoked");
        }

        //Get jwks uri
        String jwksURI;
        String softwareEnvironment = DCRUtil.getSoftwareEnvironment(softwareStatementPayload);
        if (UKValidationConstants.SOFTWARE_ENVIRONMENT_PROD.equalsIgnoreCase(softwareEnvironment)) {
            jwksURI = CommonConfigParser.getInstance().getDCRJwksUrlProduction();
        } else {
            jwksURI = CommonConfigParser.getInstance().getDCRJwksUrlSandbox();
        }

        //validate request jwt signature
        try {
            if (!JWTUtils.validateJWTSignature(requestJWT, softwareStatementPayload.getSoftwareJwksEndpoint(),
                    softwareStatementHeader.getAlg(), UKSpecConfigParser.getInstance())) {
                log.error("Request jwt signature does not match with the SSA");
                return UKErrorResponseUtil.getInvalidSignatureError("Failed to validate jwt signature");
            }
        } catch (ParseException e) {
            log.error("Error occurred while parsing the jwt", e);
            return UKErrorResponseUtil.getInvalidSignatureError("Error occurred while parsing the jwt");
        } catch (MalformedURLException e) {
            log.error("The provided jwks uri is malformed", e);
            return UKErrorResponseUtil.getInvalidSignatureError("Provided jwks uri is malformed");
        } catch (BadJOSEException | JOSEException e) {
            log.error("Signature validation failed for the provided JWT", e);
            return UKErrorResponseUtil.getInvalidSignatureError("Failed to validate jwt signature");
        }

        //validate SSA signature
        try {
            if (!JWTUtils.validateJWTSignature(registrationRequest.getSoftwareStatementPayload(), jwksURI,
                    softwareStatementHeader.getAlg(), UKSpecConfigParser.getInstance())) {
                return UKErrorResponseUtil.getInvalidSignatureError("Failed to validate SSA signature");
            }
        } catch (ParseException e) {
            log.error("Error occurred while parsing the jwt", e);
            return UKErrorResponseUtil.getInvalidSSAError("Failed to validate SSA signature");
        } catch (MalformedURLException e) {
            log.error("Provided jwks uri is malformed", e);
            return UKErrorResponseUtil.getInvalidSSAError("Provided jwks uri is malformed");
        } catch (BadJOSEException | JOSEException e) {
            log.error("Error occurred in signature validation process", e);
            return UKErrorResponseUtil.getInvalidSSAError("Failed to validate SSA signature");
        }

        //validate iss against SSA softwareId (required for OBIE SSAs)
        if (!softwareStatementPayload.getSoftwareId().equals(registrationRequest.getIss())) {
            log.error("Validation failure. Iss claim of the registration request should match with the  SSA " +
                    "softwareId.");
            return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                    "ISS claim of the request JWT does not match with the SSA's software ID");
        }

        //validate jti of the request jwt
        if (CommonConfigParser.getInstance().validateDcrRequestJtiRequired()) {
            String requestJti = registrationRequest.getJti();
            if (CommonUtil.isJTIReplayed(requestJti)) {
                log.error(String.format("JTI value %s of the registration request has been replayed", requestJti));
                return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError
                                .ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "JTI value of the registration request has been replayed");
            }
        }

        //validate jti of the SSA
        if (CommonConfigParser.getInstance().validateDcrSsaJtiRequired()) {
            String ssaJti = softwareStatementPayload.getJti();
            if (CommonUtil.isJTIReplayed(ssaJti)) {
                log.error(String.format("JTI value %s of the SSA has been replayed", ssaJti));
                return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError
                                .ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "JTI value of the SSA has been replayed");
            }
        }

        //check whether softwareId in the request matches with SSA if present
        if (!UKValidationUtil.validateSoftareId(registrationRequest, softwareStatementPayload)) {
            log.error("Software Id of the request does not match with the SSA");
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid software_id found in the request");
        }

        //check whether the requested authentication method is valid
        if (!UKValidationUtil.checkAuthMethod(registrationRequest.getTokenEndpointAuthMethod())) {
            log.error(String.format("Invalid token_endpoint_auth_method found in the request: %s",
                    registrationRequest.getTokenEndpointAuthMethod()));
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid token_endpoint_auth_method found in the request");
        }

        //if redirect_uris available in the request object, check whether it is a subset of the redirect_uris in SSA
        if (registrationRequest.getRedirectUris() != null) {
            if (!registrationRequest.getRedirectUris().isEmpty()) {
                if (!CommonUtil.matchRedirectURI(registrationRequest.getRedirectUris(),
                        softwareStatementPayload.getRedirectUris())) {
                    log.error("Provided redirect uris does not match with the SSA");
                    return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError
                                    .ErrorCodeEnum.INVALID_REDIRECT_URI,
                            "Provided redirect uris does not match with the SSA");
                }
            }
        }

        //check whether the redirect URIs are valid in SSA
        if (!CommonUtil.validateRedirectURIs(softwareStatementPayload.getRedirectUris())) {
            log.error("Invalid redirect_uris found in the SSA");
            return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError.ErrorCodeEnum.INVALID_REDIRECT_URI,
                    "Invalid redirect_uris found in the SSA");
        }
        //check whether the redirect URIs and other uris have same hostname
        if (!CommonUtil.validateURIHostNames(softwareStatementPayload.getRedirectUris(),
                softwareStatementPayload.getLogoUri(), softwareStatementPayload.getSoftwareClientUri(),
                softwareStatementPayload.getPolicyUri(), softwareStatementPayload.getTosUri())) {

            log.error("Host names of logo_uri/tos_uri/policy_uri/client_uri does not match with the redirect_uris");
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Host names of logo_uri/tos_uri/policy_uri/client_uri does not match with the redirect_uris");
        }

        //check whether the policy, client,terms of service and logo uri resolve to a valid web page if the validation
        //is set to true
        if (UKSpecConfigParser.getInstance().validateURIRequired()) {
            if (!CommonUtil.checkValidityOfURI(softwareStatementPayload.getLogoUri(),
                    softwareStatementPayload.getSoftwareClientUri(),
                    softwareStatementPayload.getPolicyUri(), softwareStatementPayload.getTosUri())) {
                log.error("Provided logo_uri/client_uri/policy_uri/tos_uri in the request does not resolve " +
                        "to a valid web page");
                return UKErrorResponseUtil.getInvalidClientMetadataError(
                        "Provided logo_uri/client_uri/policy_uri/tos_uri in the request does not resolve " +
                                "to a valid web page");
            }
        }

        //check whether application type is web(optional parameter)
        if (StringUtils.isNotBlank(registrationRequest.getApplicationType())) {
            if (!UKValidationUtil.isValidApplicationType(registrationRequest.getApplicationType())) {
                log.error(String.format("Invalid application type %s found in the request",
                        registrationRequest.getApplicationType()));
                return UKErrorResponseUtil.getInvalidClientMetadataError(
                        "Invalid application type %s found in the request.");
            }
        }

        //check whether grant types are valid
        if (!UKValidationUtil.isValidGrantType(registrationRequest.getGrantTypes())) {
            log.error("Invalid grant types found in the request");
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid grant types found in the request");
        }

        //check whether response types are valid
        if (!UKValidationUtil.isValidResponseType(registrationRequest.getResponseTypes())) {
            log.error("Invalid response types found in the request");
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid response types found in the request");
        }

        //check request_object signing algorithm
        if (!UKValidationUtil.isValidSigningAlgorithm(registrationRequest.getRequestObjectSigningAlg())) {
            log.error("Invalid request object signing algorithm found in the request");
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid request object signing algorithm found in the request");
        }

        //check id_token signing algorithm
        if (!UKValidationUtil.isValidSigningAlgorithm(registrationRequest.getIdTokenSignedResponseAlg())) {
            log.error("Invalid id token signing algorithm found in the request");
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid id token signing algorithm found in the request");
        }

        //check whether the requested scopes are valid
        if (registrationRequest.getScope() != null &&
                !UKValidationUtil.isValidScope(
                        registrationRequest.getScope(), softwareStatementPayload.getSoftwareRoles())) {
            log.error("Invalid scopes found in the request");
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid scopes found in the request");
        }
        return new UK320RegistrationError();
    }
}
