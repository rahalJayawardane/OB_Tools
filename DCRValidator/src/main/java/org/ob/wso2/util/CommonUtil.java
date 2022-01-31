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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.ob.wso2.model.OBSoftwareStatementPayload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

/**
 * Contains miscellaneous utility methods.
 */
public class CommonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Convert object to JSON.
     *
     * @param modelObject - modelObject
     * @return - String
     */
    public static String toJSON(Object modelObject) {
        Gson gson = new Gson();
        String json = gson.toJson(modelObject);
        return json;
    }

    /**
     * Check whether the redirect uris in the request are a subset of the redirect uris in the software statement
     * assertion.
     */
    public static boolean matchRedirectURI(List<String> redirectURIRequest, List<String> redirectURISoftwareStatement) {

        int matchedURis = 0;
        for (String requestURI : redirectURIRequest) {
            for (String softwareStatementURI : redirectURISoftwareStatement) {
                if (requestURI.equals(softwareStatementURI)) {
                    matchedURis = matchedURis + 1;
                }
            }
        }
        return matchedURis == redirectURIRequest.size();
    }

    /**
     * Check validity and connection of redirect uris.
     *
     * @param redirectURIs redirect uris included in the software statement
     * @return true if the uris are validated
     */
    public static boolean validateRedirectURIs(List<String> redirectURIs) {

        for (String redirectURI : redirectURIs) {
            if (!(redirectURI != null && redirectURI.contains("https") && !redirectURI.contains("localhost"))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check the hostnames of redirect uris and other uris.
     *
     * @param redirectURIs     redirect uris included in the software statement
     * @param logoURI          logo uri in the software statement
     * @param clientURI        client uri included in the software statement
     * @param policyURI        policy uri included in the software statement
     * @param termOfServiceURI termOfService uri included in the software statement
     * @return true if the uris are validated
     */
    public static boolean validateURIHostNames(List<String> redirectURIs, String logoURI, String clientURI,
                                               String policyURI, String termOfServiceURI) throws IOException {

        String logoURIHost;
        String clientURIHost;
        String policyURIHost;
        String termsOfServiceURIHost;
        try {
            logoURIHost = new URI(logoURI).getHost();
            clientURIHost = new URI(clientURI).getHost();
            policyURIHost = new URI(policyURI).getHost();
            termsOfServiceURIHost = new URI(termOfServiceURI).getHost();
            //check whether the hostnames of policy,logo,client and terms of service uris match with redirect uri
            //hostname if the validation is set to true
            if (CommonParser.getInstance().isValidateURIHostName()) {
                for (String redirectURI : redirectURIs) {
                    //check whether the redirect uris and other given uris have same host name
                    String uriHost = new URI(redirectURI).getHost();
                    if (!(logoURIHost.equals(uriHost) && clientURIHost.equals(uriHost)
                            && policyURIHost.equals(uriHost) && termsOfServiceURIHost.equals(uriHost))) {
                        return false;
                    }
                }
            }
        } catch (URISyntaxException e) {
            System.out.println("Malformed redirect uri: " + e);
            return false;
        }
        return true;
    }

    /**
     * Validate the policy,terms of service,logo and client uris.
     */
    public static boolean checkValidityOfURI(String logoURI, String clientURI, String policyURI,
                                             String termOfServiceURI) {

        HttpsURLConnection connection = null;
        String[] uris = new String[]{logoURI, clientURI, policyURI, termOfServiceURI};
        for (String uri : uris) {
            try {
                connection = (HttpsURLConnection) new URL(uri).openConnection();
                //check whether uri resolves to valid web page
                connection.connect();
                if (!(connection.getResponseCode() == 200)) {
                    return false;
                }
            } catch (MalformedURLException e) {
                System.out.println("Malformed redirect uri: " + e);
                return false;
            } catch (IOException e) {
                System.out.println("Error while connecting to the redirect uri: " + e);
                return false;
            } finally {
                assert connection != null;
                connection.disconnect();
            }
        }
        return true;
    }

    /**
     * Convert a given object to string with proper json properties.
     *
     * @param responseObject - responseObject (DTO)
     * @return response string
     */
    public static String getResponseString(Object responseObject) {
        try {
            return mapper.writeValueAsString(responseObject);
        } catch (Exception e) {
            System.out.println("Error occurred while creating the response string: " + e);
            return "{internal_server_error}";
        }
    }

    /**
     * Join multiple callback uris if available and return as a single string.
     * ex: regex=(uri1|uri2|uri3)
     *
     * @param callbackUris - list of callback uris
     * @return callbackUriString
     */
    public static String getCallbackUriString(List<String> callbackUris) {

        return new RegexRedirectURIBuilder()
                .addURIList(callbackUris)
                .build();
    }
}
