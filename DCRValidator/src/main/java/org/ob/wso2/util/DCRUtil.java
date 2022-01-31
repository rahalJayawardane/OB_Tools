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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ob.wso2.constants.ApplicationConstants;
import org.ob.wso2.model.OBClientRegistrationRequestDetail;
import org.ob.wso2.model.OBSoftwareStatementPayload;

import java.rmi.RemoteException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class relevant to API manager operations required for DCR.
 */
public abstract class DCRUtil {
    private static final String UNLIMITED_TIER = "Unlimited";
    private static final String TOKEN_TYPE_OAUTH = "OAUTH";
    private static Log log = LogFactory.getLog(DCRUtil.class);
    private static String backendServerURL;


    /**
     * Get application attributes with software environment.
     *
     * @param softwareStatementPayload - softwareStatementPayload
     * @return - application attribute map
     */
    private static Map<String, String> getApplicationAttributes(OBSoftwareStatementPayload softwareStatementPayload,
                                                                OBClientRegistrationRequestDetail registrationRequest) {

        Gson gson = new Gson();
        Map<String, String> alteredAppAttributeMap = new HashMap<>();
        String environment = StringUtils.lowerCase(getSoftwareEnvironment(softwareStatementPayload));
        String ssaString = gson.toJson(softwareStatementPayload);
        Map<String, String> appAttributeMap = gson.fromJson(ssaString, Map.class);

        for (Map.Entry entry : appAttributeMap.entrySet()) {
            String newKey = entry.getKey().toString().concat("_" + environment);
            String value = gson.toJson(entry.getValue()).replaceAll("^\"|\"$", ""); //remove
            // unnecessary inverted commas.
            alteredAppAttributeMap.put(newKey, value);
        }

        //add the regulatory compliance attribute
        alteredAppAttributeMap.put(ApplicationConstants.REGULATORY, "true");

        //Change jwks_uri key to support "software_jwks_endpoint" sp attribute.
        if (!appAttributeMap.containsKey(ApplicationConstants.SOFTWARE_JWKS_ENDPOINT)) {
            alteredAppAttributeMap.put(ApplicationConstants.SOFTWARE_JWKS_ENDPOINT + "_" + environment,
                    alteredAppAttributeMap.remove(ApplicationConstants.JWKS_URI + "_" + environment));
        }
        return alteredAppAttributeMap;
    }

    /**
     * Return client's environment (SANDBOX/PRODUCTION) when the SSA is given.
     *
     * @param softwareStatementPayload - SSA
     * @return - softwareEnvironment
     */
    public static String getSoftwareEnvironment(OBSoftwareStatementPayload softwareStatementPayload) {
        if (ApplicationConstants.SANDBOX_ENV.equalsIgnoreCase(softwareStatementPayload.getSoftwareEnvironment())) {
            return ApplicationConstants.SANDBOX_ENV;
        } else {
            return ApplicationConstants.PRODUCTION_ENV;
        }
    }
}
