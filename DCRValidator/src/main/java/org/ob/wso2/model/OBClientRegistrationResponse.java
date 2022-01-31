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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Common registration response class.
 */
public class OBClientRegistrationResponse {

    @JsonProperty("client_id")
    protected String clientId = null;

    @JsonProperty("client_id_issued_at")
    protected Integer clientIdIssuedAt = null;

    @JsonProperty("redirect_uris")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<String> redirectUris = new ArrayList<String>();

    @JsonProperty("grant_types")
    protected List<String> grantTypes = new ArrayList<>();

    @JsonProperty("application_type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String applicationType = null;

    @JsonProperty("id_token_signed_response_alg")
    protected String idTokenSignedResponseAlg = null;

    @JsonProperty("token_endpoint_auth_signing_alg")
    protected String tokenEndpointAuthSigningAlg = null;

    @JsonProperty("request_object_signing_alg")
    protected String requestObjectSigningAlg = null;

    @JsonProperty("scope")
    protected String scope = null;

    @JsonProperty("software_id")
    protected String softwareId = null;

    /**
     * OAuth 2.0 client identifier string.
     **/
    @ApiModelProperty(value = "OAuth 2.0 client identifier string")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Time at which the client identifier was issued expressed as seconds since 1970-01-01T00:00:00Z as measured in UTC
     * minimum: 0.0.
     **/
    @ApiModelProperty(value = "Time at which the client identifier was issued expressed as seconds " +
            "since 1970-01-01T00:00:00Z as measured in UTC")
    public Integer getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public void setClientIdIssuedAt(Integer clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    /**
     * String representing a unique identifier assigned by the ACCC Register and used
     * by registration endpoints to identify the software product to be dynamically registered.
     **/
    @ApiModelProperty(value = "String representing a unique identifier assigned " +
            "by the ACCC Register and used by registration endpoints to identify " +
            "the software product to be dynamically registered.")
    public String getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(String softwareId) {
        this.softwareId = softwareId;
    }

    /**
     * String containing a space-separated list of scope values that the client can use when requesting access tokens.
     **/
    @ApiModelProperty(value = "String containing a space-separated list of " +
            "scope values that the client can use when requesting access tokens")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    public String getTokenEndpointAuthSigningAlg() {
        return tokenEndpointAuthSigningAlg;
    }

    public void setTokenEndpointAuthSigningAlg(String tokenEndpointAuthSigningAlg) {
        this.tokenEndpointAuthSigningAlg = tokenEndpointAuthSigningAlg;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    public List<String> getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(List<String> grantTypes) {
        this.grantTypes = grantTypes;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    public String getIdTokenSignedResponseAlg() {
        return idTokenSignedResponseAlg;
    }

    public void setIdTokenSignedResponseAlg(String idTokenSignedResponseAlg) {
        this.idTokenSignedResponseAlg = idTokenSignedResponseAlg;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    public String getRequestObjectSigningAlg() {
        return requestObjectSigningAlg;
    }

    public void setRequestObjectSigningAlg(String requestObjectSigningAlg) {
        this.requestObjectSigningAlg = requestObjectSigningAlg;
    }
}
