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

package org.ob.wso2.model;

/**
 * Contains data to generate the http response in the common endpoint module.
 */
public class OBRegistrationSetupResponse {

    /**
     * Statuses mapping to http response statuses.
     */
    public enum ResponseStatusEnum {
        BAD_REQUEST(400),
        CONFLICT(409),
        CREATED(201),
        INTERNAL_SERVER_ERROR(500),
        NO_CONTENT(204),
        NOT_FOUND(404),
        OK(200),
        UNAUTHORIZED(401),
        FORBIDDEN(403);

        private int code;

        ResponseStatusEnum(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public OBRegistrationSetupResponse(int httpStatusCode, String entity) {
        this.httpStatusCode = httpStatusCode;
        this.entity = entity;
    }

    private int httpStatusCode;

    private String entity;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getEntity() {
        return entity;
    }
}
