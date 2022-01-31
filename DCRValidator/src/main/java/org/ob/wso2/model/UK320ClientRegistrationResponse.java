/*
 * Copyright (c)  2019, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 *
 */

package com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wso2.finance.open.banking.dynamic.client.registration.common.model.OBClientRegistrationResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for UK320ClientRegistrationResponse.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UK320ClientRegistrationResponse extends OBClientRegistrationResponse {

    @JsonProperty("client_secret")
    private String clientSecret = null;

    @JsonProperty("client_secret_expires_at")
    private Integer clientSecretExpiresAt = null;

    @JsonProperty("token_endpoint_auth_method")
    private String tokenEndpointAuthMethod = null;

    @JsonProperty("response_types")
    private List<String> responseTypes = new ArrayList<String>();

    @JsonProperty("software_statement")
    private String softwareStatement = null;

    private String tlsClientAuthSubjectDn = null;

    /**
     * OAuth 2.0 client secret string.
     **/
    @JsonProperty("client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Time at which the client secret will expire expressed as seconds since 1970-01-01T00:00:00Z as measured in UTC.
     * Set to 0 if does not expire.
     * minimum: 0.0
     **/
    public Integer getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public void setClientSecretExpiresAt(Integer clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }

    /**
     * Specifies which Token endpoint authentication method the TPP wants to use.
     **/
    public String getTokenEndpointAuthMethod() {
        return tokenEndpointAuthMethod;
    }

    public void setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) {
        this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
    }

    /**
     * Specifies what the TPP can request to be returned from the ASPSP authorisation endpoint.
     **/
    public List<String> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(List<String> responseTypes) {
        this.responseTypes = responseTypes;
    }

    /**
     * Software statement assertion issued by the issuer.
     **/
    public String getSoftwareStatement() {
        return softwareStatement;
    }

    public void setSoftwareStatement(String softwareStatement) {
        this.softwareStatement = softwareStatement;
    }

    /**
     * Specifies the claim that contain the DN of the certificate that the TPP will present to the ASPSP token endpoint.
     **/
    @JsonProperty("tls_client_auth_subject_dn")
    public String getTlsClientAuthSubjectDn() {
        return tlsClientAuthSubjectDn;
    }

    @JsonProperty("tls_client_auth_subject_dn")
    public void setTlsClientAuthDn(String tlsClientAuthSubjectDn) {
        this.tlsClientAuthSubjectDn = tlsClientAuthSubjectDn;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class DynamicClientRegistrationResponse {\n");

        sb.append("  client_id: ").append(clientId).append("\n");
        sb.append("  client_secret: ").append(clientSecret).append("\n");
        sb.append("  client_id_issued_at: ").append(clientIdIssuedAt).append("\n");
        sb.append("  client_secret_expires_at: ").append(clientSecretExpiresAt).append("\n");
        sb.append("  redirect_uris: ").append(redirectUris).append("\n");
        sb.append("  token_endpoint_auth_method: ").append(tokenEndpointAuthMethod).append("\n");
        sb.append("  grant_types: ").append(grantTypes).append("\n");
        sb.append("  response_types: ").append(responseTypes).append("\n");
        sb.append("  software_id: ").append(softwareId).append("\n");
        sb.append("  scope: ").append(scope).append("\n");
        sb.append("  software_statement: ").append(softwareStatement).append("\n");
        sb.append("  application_type: ").append(applicationType).append("\n");
        sb.append("  id_token_signed_response_alg: ").append(idTokenSignedResponseAlg).append("\n");
        sb.append("  request_object_signing_alg: ").append(requestObjectSigningAlg).append("\n");
        sb.append("  token_endpoint_auth_signing_alg: ").append(tokenEndpointAuthMethod).append("\n");
        sb.append("  tls_client_auth_subject_dn: ").append(tlsClientAuthSubjectDn).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
