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

package com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wso2.finance.open.banking.dynamic.client.registration.common.model.OBRegistrationError;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * UK320RegistrationError Class.
 */
@ApiModel(description = "This DTO represents Error body")
public class UK320RegistrationError extends OBRegistrationError {

    @JsonProperty("error")
    private ErrorCodeEnum code;

    @JsonProperty("error_description")
    private String errorDescription = null;

    /**
     * Error code.
     **/
    public ErrorCodeEnum getCode() {
        return code;
    }

    public void setCode(ErrorCodeEnum code) {
        this.code = code;
    }

    /**
     * Description of the error.
     **/
    @ApiModelProperty(value = "Description of the error")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getErrorDescription() {

        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {

        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\"error\": \"").append(code).append("\",\n");
        sb.append("\"error_description\": \"").append(errorDescription).append("\"\n");
        sb.append("}\n");
        return sb.toString();
    }
}
