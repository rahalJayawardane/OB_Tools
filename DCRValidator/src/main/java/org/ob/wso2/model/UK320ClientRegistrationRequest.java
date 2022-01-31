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
 *
 */
package org.ob.wso2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Model class for UK320ClientRegistrationRequest.
 */
public class UK320ClientRegistrationRequest extends OBClientRegistrationRequestDetail {

    @JsonProperty("scope")
    private String scope = null;

    @JsonProperty("tls_client_auth_subject_dn")
    private String tlsClientAuthSubjectDn = null;

    @JsonProperty("software_id")
    private String softwareId = null;

    /**
     * Scopes the client is asking for.
     **/
    @ApiModelProperty(required = true, value = "Scopes the client is asking for")
    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Specifies the claim that contain the DN of the certificate that the TPP will present to the ASPSP token endpoint.
     **/
    @JsonProperty("tls_client_auth_subject_dn")
    public String getTlsClientAuthDn() {
        return tlsClientAuthSubjectDn;
    }

    public void setTlsClientAuthDn(String tlsClientAuthSubjectDn) {
        this.tlsClientAuthSubjectDn = tlsClientAuthSubjectDn;
    }

    /**
     * OB Organisation ID
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("software_id")
    public String getSoftwareId() {

        return softwareId;
    }

    public void setSoftwareId(String softwareId) {

        this.softwareId = softwareId;
    }
}

