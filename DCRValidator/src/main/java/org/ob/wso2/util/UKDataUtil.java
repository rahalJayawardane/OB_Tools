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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import groovy.json.internal.Exceptions;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ob.wso2.model.UK320ClientRegistrationRequest;
import org.ob.wso2.model.UK320SoftwareStatementBody;

import java.util.Optional;

/**
 * Util class for UK v3.2 data handling.
 */
public class UKDataUtil {
     public static UK320SoftwareStatementBody getSoftwareStatementPayloadFromRequest(
            UK320ClientRegistrationRequest registrationRequest) throws Exception {

        Gson gson = new GsonBuilder().create();
        Optional<UK320SoftwareStatementBody> softwareStatementBody = Optional.empty();
        JSONObject decodedSoftwareStatementBody;

        //decode SSA
        try {
            decodedSoftwareStatementBody = JWTUtils.decodeRequestJWT(registrationRequest.
                    getSoftwareStatementPayload(), JWTUtils.JwtPart.body);
            //map SSA body to the UK320SoftwareStatementBody model
            softwareStatementBody = Optional.ofNullable(gson.fromJson(decodedSoftwareStatementBody.toString()
                    , UK320SoftwareStatementBody.class));
        } catch (Exception e) {
            System.out.println(String.format("Error occurred while decoding the provided SSA : %s", e));
        }
        return softwareStatementBody.orElseThrow(() -> new Exceptions.JsonInternalException(
                "Malformed or unsupported SSA"));
    }
}
