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
package org.ob.wso2.validator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import org.apache.commons.lang3.StringUtils;
import org.ob.wso2.constants.ErrorConstants;
import org.ob.wso2.constants.UKValidationConstants;
import org.ob.wso2.model.UK320ClientRegistrationRequest;
import org.ob.wso2.model.UK320RegistrationError;
import org.ob.wso2.model.UK320SoftwareStatementBody;
import org.ob.wso2.model.UK320SoftwareStatementHeader;
import org.ob.wso2.util.CertificateUtil;
import org.ob.wso2.util.CommonParser;
import org.ob.wso2.util.CommonUtil;
import org.ob.wso2.util.DCRUtil;
import org.ob.wso2.util.JWTUtils;
import org.ob.wso2.util.UKErrorResponseUtil;
import org.ob.wso2.util.UKValidationUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * Request validator class for UK DCR v3.2.
 */
public class UK320RegistrationRequestValidator {

    /**
     * Mandatory parameter checks for DCR v3.2.
     *
     * @param registrationRequest - Registration request object
     */
    public static void validateMandatoryParameters(
            UK320ClientRegistrationRequest registrationRequest) throws Exception {

        //null checks
        if (StringUtils.isBlank(registrationRequest.getIss())) {
            throw new Exception(ErrorConstants.ISS_NULL);
        }

        if (registrationRequest.getIat() == null) {
            throw new Exception(ErrorConstants.IAT_NULL);
        }

        if (registrationRequest.getExp() == null) {
            throw new Exception(ErrorConstants.EXP_NULL);
        }

        if (StringUtils.isBlank(registrationRequest.getJti())) {
            throw new Exception(ErrorConstants.JTI_NULL);
        }

        if (StringUtils.isBlank(registrationRequest.getAud())) {
            throw new Exception(ErrorConstants.AUD_NULL);
        }

        if (registrationRequest.getRedirectUris() == null) {
            throw new Exception(ErrorConstants.REDIRECT_URIS_NULL);
        }

        if (registrationRequest.getRedirectUris().isEmpty()) {
            throw new Exception(ErrorConstants.REDIRECT_URIS_EMPTY);
        }

        if (StringUtils.isBlank(registrationRequest.getApplicationType())) {
            throw new Exception(ErrorConstants.APPLICATION_TYPE_NULL);
        }

        if (StringUtils.isBlank(registrationRequest.getTokenEndpointAuthMethod())) {
            throw new Exception(ErrorConstants.TOKEN_ENDPOINT_AUTH_METHOD_NULL);
        } else {
            //signing algorithm should be present if auth_method is private_key_jwt or client_secret_jwt
            String tokenEndpointAuthMethod = registrationRequest.getTokenEndpointAuthMethod();
            if ((UKValidationConstants.PRIVATE_KEY_JWT.equals(tokenEndpointAuthMethod) ||
                    UKValidationConstants.CLIENT_SECRET_JWT.equals(tokenEndpointAuthMethod)) &&
                    StringUtils.isBlank(registrationRequest.getTokenEndpointAuthSigningAlg())) {
                throw new Exception(ErrorConstants.TOKEN_ENDPOINT_AUTH_SIGNING_ALG_NULL);

            }
        }

        if (registrationRequest.getGrantTypes() == null) {
            throw new Exception(ErrorConstants.GRANT_TYPES_NULL);

        } else if (registrationRequest.getGrantTypes().isEmpty()) {
            throw new Exception(ErrorConstants.GRANT_TYPES_EMPTY);
        }

        if (registrationRequest.getResponseTypes() == null) {
            throw new Exception(ErrorConstants.RESPONSE_TYPES_NULL);

        } else if (registrationRequest.getResponseTypes().isEmpty()) {
            throw new Exception(ErrorConstants.RESPONSE_TYPES_EMPTY);

        }

        if (StringUtils.isBlank(registrationRequest.getSoftwareStatementPayload())) {
            throw new Exception(ErrorConstants.SSA_NULL);
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
            UK320SoftwareStatementHeader softwareStatementHeader, UK320SoftwareStatementBody softwareStatementPayload) throws IOException {

        //check whether SSA jwt is revoked
        if (CertificateUtil.checkJWKRevoked(softwareStatementHeader.getKid(),
                softwareStatementPayload.getSoftwareJwksRevokedEndpoint())) {
            return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError.ErrorCodeEnum.INVALID_SOFTWARE_STATEMENT,
                    "Provided SSA is revoked");
        }

        //Get jwks uri
        String jwksURI;
        String softwareEnvironment = DCRUtil.getSoftwareEnvironment(softwareStatementPayload);
        if (UKValidationConstants.SOFTWARE_ENVIRONMENT_PROD.equalsIgnoreCase(softwareEnvironment)) {
            jwksURI = CommonParser.getInstance().getDcrProduction();
        } else {
            jwksURI = CommonParser.getInstance().getDcrSandbox();
        }

        //validate request jwt signature
        try {
            if (!JWTUtils.validateJWTSignature(requestJWT, softwareStatementPayload.getSoftwareJwksEndpoint(),
                    softwareStatementHeader.getAlg(), CommonParser.getInstance())) {
                System.out.println("Request jwt signature does not match with the SSA");
                return UKErrorResponseUtil.getInvalidSignatureError("Failed to validate jwt signature");
            }
        } catch (ParseException e) {
            return UKErrorResponseUtil.getInvalidSignatureError("Error occurred while parsing the jwt: " + e);
        } catch (MalformedURLException e) {
            return UKErrorResponseUtil.getInvalidSignatureError("Provided jwks uri is malformed");
        } catch (BadJOSEException | JOSEException e) {
            return UKErrorResponseUtil.getInvalidSignatureError("Failed to validate jwt signature");
        }

        //validate SSA signature
        try {
            if (!JWTUtils.validateJWTSignature(registrationRequest.getSoftwareStatementPayload(), jwksURI,
                    softwareStatementHeader.getAlg(), CommonParser.getInstance())) {
                return UKErrorResponseUtil.getInvalidSignatureError("Failed to validate SSA signature");
            }
        } catch (ParseException e) {
            return UKErrorResponseUtil.getInvalidSSAError("Failed to validate SSA signature");
        } catch (MalformedURLException e) {
            return UKErrorResponseUtil.getInvalidSSAError("Provided jwks uri is malformed");
        } catch (BadJOSEException | JOSEException e) {
            return UKErrorResponseUtil.getInvalidSSAError("Failed to validate SSA signature");
        }

        //validate iss against SSA softwareId (required for OBIE SSAs)
        if (!softwareStatementPayload.getSoftwareId().equals(registrationRequest.getIss())) {
            return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                    "ISS claim of the request JWT does not match with the SSA's software ID");
        }

        //check whether softwareId in the request matches with SSA if present
        if (!UKValidationUtil.validateSoftareId(registrationRequest, softwareStatementPayload)) {
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid software_id found in the request");
        }

        //check whether the requested authentication method is valid
        if (!UKValidationUtil.checkAuthMethod(registrationRequest.getTokenEndpointAuthMethod())) {
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid token_endpoint_auth_method found in the request");
        }

        //if redirect_uris available in the request object, check whether it is a subset of the redirect_uris in SSA
        if (registrationRequest.getRedirectUris() != null) {
            if (!registrationRequest.getRedirectUris().isEmpty()) {
                if (!CommonUtil.matchRedirectURI(registrationRequest.getRedirectUris(),
                        softwareStatementPayload.getRedirectUris())) {
                    return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError
                                    .ErrorCodeEnum.INVALID_REDIRECT_URI,
                            "Provided redirect uris does not match with the SSA");
                }
            }
        }

        //check whether the redirect URIs are valid in SSA
        if (!CommonUtil.validateRedirectURIs(softwareStatementPayload.getRedirectUris())) {
            return UKErrorResponseUtil.setupErrorObject(UK320RegistrationError.ErrorCodeEnum.INVALID_REDIRECT_URI,
                    "Invalid redirect_uris found in the SSA");
        }
        //check whether the redirect URIs and other uris have same hostname
        if (!CommonUtil.validateURIHostNames(softwareStatementPayload.getRedirectUris(),
                softwareStatementPayload.getLogoUri(), softwareStatementPayload.getSoftwareClientUri(),
                softwareStatementPayload.getPolicyUri(), softwareStatementPayload.getTosUri())) {

            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Host names of logo_uri/tos_uri/policy_uri/client_uri does not match with the redirect_uris");
        }

        //check whether the policy, client,terms of service and logo uri resolve to a valid web page if the validation
        //is set to true
        if (CommonParser.getInstance().isValidateURI()) {
            if (!CommonUtil.checkValidityOfURI(softwareStatementPayload.getLogoUri(),
                    softwareStatementPayload.getSoftwareClientUri(),
                    softwareStatementPayload.getPolicyUri(), softwareStatementPayload.getTosUri())) {
                return UKErrorResponseUtil.getInvalidClientMetadataError(
                        "Provided logo_uri/client_uri/policy_uri/tos_uri in the request does not resolve " +
                                "to a valid web page");
            }
        }

        //check whether application type is web(optional parameter)
        if (StringUtils.isNotBlank(registrationRequest.getApplicationType())) {
            if (!UKValidationUtil.isValidApplicationType(registrationRequest.getApplicationType())) {
                return UKErrorResponseUtil.getInvalidClientMetadataError(
                        "Invalid application type %s found in the request.");
            }
        }

        //check whether grant types are valid
        if (!UKValidationUtil.isValidGrantType(registrationRequest.getGrantTypes())) {
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid grant types found in the request");
        }

        //check whether response types are valid
        if (!UKValidationUtil.isValidResponseType(registrationRequest.getResponseTypes())) {
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid response types found in the request");
        }

        //check request_object signing algorithm
        if (!UKValidationUtil.isValidSigningAlgorithm(registrationRequest.getRequestObjectSigningAlg())) {
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid request object signing algorithm found in the request");
        }

        //check id_token signing algorithm
        if (!UKValidationUtil.isValidSigningAlgorithm(registrationRequest.getIdTokenSignedResponseAlg())) {
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid id token signing algorithm found in the request");
        }

        //check whether the requested scopes are valid
        if (registrationRequest.getScope() != null &&
                !UKValidationUtil.isValidScope(
                        registrationRequest.getScope(), softwareStatementPayload.getSoftwareRoles())) {
            return UKErrorResponseUtil.getInvalidClientMetadataError(
                    "Invalid scopes found in the request");
        }
        return new UK320RegistrationError();
    }
}
