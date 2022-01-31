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

package org.ob.wso2.util;


import org.ob.wso2.model.OBRegistrationSetupResponse;
import org.ob.wso2.model.UK320RegistrationError;

/**
 * UK specific error response util class.
 */
public class UKErrorResponseUtil {

    public static OBRegistrationSetupResponse getForbiddenResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.FORBIDDEN.getCode(),
                setupError(UK320RegistrationError.ErrorCodeEnum.RESOURCE_FORBIDDEN,
                        "Deployed specification doesn't support the request"));
    }

    public static OBRegistrationSetupResponse getMissingClientIdResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.BAD_REQUEST.getCode(),
                setupError(UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "A valid client id is not found with the request"));
    }

    public static OBRegistrationSetupResponse getMissingRequestBodyResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.BAD_REQUEST.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "A valid jwt is not found with the request"));
    }

    public static OBRegistrationSetupResponse getInvalidJWTResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.BAD_REQUEST.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "Provided jwt is malformed and cannot be decoded"));
    }

    public static OBRegistrationSetupResponse getInvalidSSAResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.BAD_REQUEST.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.UNAPPROVED_SOFTWARE_STATEMENT,
                        "Provided SSA is malformed or unsupported by the specification"));
    }

    public static OBRegistrationSetupResponse getMalformedRequestBodyResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.BAD_REQUEST.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "Provided request body is malformed or unsupported by the specification"));
    }

    public static OBRegistrationSetupResponse getApplicationDoesNotExistResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.NOT_FOUND.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.RESOURCE_NOT_FOUND,
                        "An application mapping to the given client id cannot be found"));
    }

    public static OBRegistrationSetupResponse getApplicationAlreadyExistsResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.CONFLICT.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.RESOURCE_ALREADY_EXISTS,
                        "An application created with the given SSA is already available. Please use " +
                                "HTTP PUT method if you need to update the existing application"));
    }

    public static OBRegistrationSetupResponse getInternalServerErrorResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.INTERNAL_SERVER_ERROR.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.INTERNAL_SERVER_ERROR,
                        "Error occurred while processing the request"));
    }

    public static OBRegistrationSetupResponse getInvalidClientIdResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.UNAUTHORIZED.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "Request failed due to unknown or invalid Client"));
    }

    public static OBRegistrationSetupResponse getInvalidTlsCertResponse() {
        return new OBRegistrationSetupResponse(
                OBRegistrationSetupResponse.ResponseStatusEnum.UNAUTHORIZED.getCode(),
                setupError(
                        UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                        "Request failed due to invalid TLS certificate"));
    }

    public static UK320RegistrationError getInvalidClientMetadataError(String message) {
        return setupErrorObject(
                UK320RegistrationError.ErrorCodeEnum.INVALID_CLIENT_METADATA,
                message);
    }

    public static UK320RegistrationError getInvalidSignatureError(String message) {
        return setupErrorObject(
                UK320RegistrationError.ErrorCodeEnum.SIGNATURE_VALIDATION_FAILED,
                message);
    }

    public static UK320RegistrationError getInvalidSSAError(String message) {
        return setupErrorObject(
                UK320RegistrationError.ErrorCodeEnum.INVALID_SOFTWARE_STATEMENT,
                message);
    }

    /**
     * Generate UK spec specific error string using UK320RegistrationError object.
     *
     * @param errorCode        - Error code
     * @param errorDescription - Error description
     * @return
     */
    public static String setupError(
            UK320RegistrationError.ErrorCodeEnum errorCode, String errorDescription) {

        UK320RegistrationError errorDTO = new UK320RegistrationError();
        errorDTO.setCode(errorCode);
        errorDTO.setErrorDescription(errorDescription);
        return errorDTO.toString();
    }

    /**
     * Generate UK spec specific error object using UK320RegistrationError object.
     *
     * @param errorCode        - Error code
     * @param errorDescription - Error description
     * @return
     */
    public static UK320RegistrationError setupErrorObject(
            UK320RegistrationError.ErrorCodeEnum errorCode, String errorDescription) {

        UK320RegistrationError errorDTO = new UK320RegistrationError();
        errorDTO.setCode(errorCode);
        errorDTO.setErrorDescription(errorDescription);
        return errorDTO;
    }
}
