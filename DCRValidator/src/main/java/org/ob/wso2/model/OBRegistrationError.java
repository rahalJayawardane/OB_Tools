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

package com.wso2.finance.open.banking.dynamic.client.registration.common.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Common registration error class.
 */
public class OBRegistrationError {

    /**
     * Common error codes used for DCR response.
     */
    public enum ErrorCodeEnum {

        //Specification defined error codes
        INVALID_REDIRECT_URI("invalid_redirect_uri"),
        INVALID_CLIENT_METADATA("invalid_client_metadata"),
        INVALID_SOFTWARE_STATEMENT("invalid_software_statement"),
        UNAPPROVED_SOFTWARE_STATEMENT("unapproved_software_statement"),

        //Custom error codes
        INTERNAL_SERVER_ERROR("internal_server_error"),
        SIGNATURE_VALIDATION_FAILED("signature_validation_failed"),
        RESOURCE_ALREADY_EXISTS("resource_already_exists"),
        RESOURCE_NOT_FOUND("resource_not_found"),
        RESOURCE_FORBIDDEN("resource_forbidden");

        private String value;

        ErrorCodeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }
    }
}
