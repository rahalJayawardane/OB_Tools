/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.finance.open.banking.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Aid in creating IAM regex based redirect URI templates.
 */
public class RegexRedirectURIBuilder {

    private static final String REGEX_IAM_TEMPLATE = "regexp=(%s)";
    private static final String REGEX_URI_TEMPLATE = "^%s$";

    private List<String> uriSet = new ArrayList<>();

    /**
     * Add URI to RegexRedirect.
     *
     * @param uri redirect URI.
     * @return builder.
     */
    public RegexRedirectURIBuilder addURI(String uri) {

        if (StringUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("URI cannot be null or empty");
        }

        uriSet.add(uri);
        return this;
    }

    /**
     * Add List of URIs to RegexRedirect.
     *
     * @param uriList list of URIs.
     * @return builder.
     */
    public RegexRedirectURIBuilder addURIList(List<String> uriList) {

        if (uriList == null) {
            throw new IllegalArgumentException("URI List cannot be null");
        }

        uriSet.addAll(uriList);
        return this;
    }

    /**
     * Build regex template.
     *
     * @return regex template.
     */
    public String build() {

        if (uriSet.isEmpty()) {
            throw new IllegalStateException("No URIs are available in builder, unable to build");
        }

        // Format URIs for regex template.
        List<String> formattedUris = uriSet.stream()
                // Trim leading or trailing spaces.
                .map(String::trim)
                // Escape `?` if any
                .map(uri -> uri.replace("?", "\\?"))
                // Add absolute matching template
                .map(uri -> String.format(REGEX_URI_TEMPLATE, uri))
                .collect(Collectors.toList());

        // Join URI set with delimiter
        String formattedRegex = String.join("|", formattedUris);

        // Format to IAM regex template.
        return String.format(REGEX_IAM_TEMPLATE, formattedRegex);
    }

    /**
     * Given a string in regex format, unwrap and create list of seperate URIs.
     * @param uriString The joined URIs in string format.
     * @return List of URIs.
     */
    public List<String> unwrapURIString(String uriString) {

        Pattern outerPattern = Pattern.compile("regexp=\\((.*?)\\)");
        Pattern innerPattern = Pattern.compile("\\^(.*?)\\$");

        Matcher matcher = outerPattern.matcher(uriString);

        String delimitedUri;

        if (matcher.find()) {
            // remove regex= part
            delimitedUri = matcher.group(1);
        } else {
            // URI is not having regex format
            return Collections.singletonList(uriString);
        }

        String[] uris = delimitedUri.split("\\|");

        // remove ^..$ part
        return Arrays.stream(uris)
                .map(uri -> {
                    Matcher m = innerPattern.matcher(uri);
                    if (m.find()) {
                        return m.group(1);
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

}
