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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Common registration request object.
 */
public class OBClientRegistrationRequestDetail {

    @NotNull
    private String iss = null;

    @NotNull
    private Integer iat = null;

    @NotNull
    private Integer exp = null;

    @NotNull
    private String aud = null;

    @NotNull
    private String jti = null;

    @SerializedName("redirect_uris")
    private List<String> redirectUris = new ArrayList<String>();

    @NotNull
    @SerializedName("token_endpoint_auth_method")
    private String tokenEndpointAuthMethod = null;

    @NotNull
    @SerializedName("grant_types")
    private List<String> grantTypes = new ArrayList<>();

    @NotNull
    @SerializedName("response_types")
    private List<String> responseTypes = new ArrayList<>();

    @SerializedName("software_id")
    private String softwareId = null;

    @NotNull
    @SerializedName("software_statement")
    private String softwareStatement = null;

    @SerializedName("application_type")
    private String applicationType = null;

    @NotNull
    @SerializedName("id_token_signed_response_alg")
    private String idTokenSignedResponseAlg = null;

    @SerializedName("request_object_signing_alg")
    private String requestObjectSigningAlg = null;

    @NotNull
    @SerializedName("token_endpoint_auth_signing_alg")
    private String tokenEndpointAuthSigningAlg = null;

    @NotNull
    @SerializedName("id_token_encrypted_response_enc")
    protected String idTokenEncryptedResponseEnc = null;

    @NotNull
    @SerializedName("id_token_encrypted_response_alg")
    protected String idTokenEncryptedResponseAlg = null;

    /**
     * Unique identifier for the TPP. Implemented as Base62 encoded GUID
     **/
    @ApiModelProperty(required = true, value = "Unique identifier for the TPP. Implemented as Base62 encoded GUID")
    @JsonProperty("iss")
    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    /**
     * The time at which the request was issued by the TPP  expressed as seconds since
     * 1970-01-01T00:00:00Z as measured in UTC.
     **/
    @ApiModelProperty(required = true, value = "The time at which the request was issued by the TPP  " +
            "expressed as seconds since 1970-01-01T00:00:00Z as measured in UTC")
    @JsonProperty("iat")
    public Integer getIat() {
        return iat;
    }

    public void setIat(Integer iat) {
        this.iat = iat;
    }

    /**
     * The time at which the request expires expressed as seconds since 1970-01-01T00:00:00Z as measured in UTC.
     **/
    @ApiModelProperty(required = true, value = "The time at which the request expires " +
            "expressed as seconds since 1970-01-01T00:00:00Z as measured in UTC")
    @JsonProperty("exp")
    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    /**
     * The audience for the request. This should be the unique identifier\nfor the ASPSP issued by the issuer
     * of the software statement.\nImplemented as Base62 encoded GUID\n
     **/
    @ApiModelProperty(required = true, value = "The audience for the request. This should be the unique identifier" +
            "\nfor the ASPSP issued by the issuer of the software statement.\nImplemented as Base62 encoded GUID\n")
    @JsonProperty("aud")
    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    /**
     * Unique identifier for the JWT implemented as UUID v4.
     **/
    @ApiModelProperty(required = true, value = "Unique identifier for the JWT implemented as UUID v4")
    @JsonProperty("jti")
    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("redirect_uris")
    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("token_endpoint_auth_method")
    public String getTokenEndpointAuthMethod() {
        return tokenEndpointAuthMethod;
    }

    public void setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) {
        this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("grant_types")
    public List<String> getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(List<String> grantTypes) {
        this.grantTypes = grantTypes;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("response_types")
    public List<String> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(List<String> responseTypes) {
        this.responseTypes = responseTypes;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("software_id")
    public String getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(String softwareId) {
        this.softwareId = softwareId;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("software_statement")
    public String getSoftwareStatementPayload() {
        return softwareStatement;
    }

    public void setSoftwareStatement(String softwareStatement) {
        this.softwareStatement = softwareStatement;
    }

    /**
     *
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("application_type")
    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("id_token_signed_response_alg")
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
    @JsonProperty("request_object_signing_alg")
    public String getRequestObjectSigningAlg() {
        return requestObjectSigningAlg;
    }

    public void setRequestObjectSigningAlg(String requestObjectSigningAlg) {
        this.requestObjectSigningAlg = requestObjectSigningAlg;
    }

    /**
     *
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("token_endpoint_auth_signing_alg")
    public String getTokenEndpointAuthSigningAlg() {
        return tokenEndpointAuthSigningAlg;
    }

    public void setTokenEndpointAuthSigningAlg(String tokenEndpointAuthSigningAlg) {
        this.tokenEndpointAuthSigningAlg = tokenEndpointAuthSigningAlg;
    }

    /**
     * JWE `enc` algorithm with which an id_token is to be encrypted.
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("id_token_encrypted_response_enc")
    public String getIdTokenEncryptedResponseEnc() {
        return idTokenEncryptedResponseEnc;
    }

    public void setIdTokenEncryptedResponseEnc(String idTokenEncryptedResponseEnc) {
        this.idTokenEncryptedResponseEnc = idTokenEncryptedResponseEnc;
    }

    /**
     * JWE `alg` algorithm with which an id_token is to be encrypted.
     * **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("id_token_encrypted_response_alg")
    public String getIdTokenEncryptedResponseAlg() {
        return idTokenEncryptedResponseAlg;
    }

    public void setIdTokenEncryptedResponseAlg(String idTokenEncryptedResponseAlg) {
        this.idTokenEncryptedResponseAlg = idTokenEncryptedResponseAlg;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class com.wso2.finance.open.banking.dynamic.client.registration.common." +
                "OBClientRegistrationRequestDetail {\n");

        sb.append("  iss: ").append(iss).append("\n");
        sb.append("  iat: ").append(iat).append("\n");
        sb.append("  exp: ").append(exp).append("\n");
        sb.append("  aud: ").append(aud).append("\n");
        sb.append("  jti: ").append(jti).append("\n");
        sb.append("  redirectUris: ").append(redirectUris).append("\n");
        sb.append("  tokenEndpointAuthMethod: ").append(tokenEndpointAuthMethod).append("\n");
        sb.append("  grantTypes: ").append(grantTypes).append("\n");
        sb.append("  responseTypes: ").append(responseTypes).append("\n");
        sb.append("  softwareId: ").append(softwareId).append("\n");
        sb.append("  softwareStatement: ").append(softwareStatement).append("\n");
        sb.append("  applicationType: ").append(applicationType).append("\n");
        sb.append("  idTokenSignedResponseAlg: ").append(idTokenSignedResponseAlg).append("\n");
        sb.append("  requestObjectSigningAlg: ").append(requestObjectSigningAlg).append("\n");
        sb.append("  tokenEndpointAuthSigningAlg: ").append(tokenEndpointAuthSigningAlg).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
