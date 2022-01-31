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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Common software statement model.
 */
public abstract class OBSoftwareStatementPayload {
    private String iss;
    private String iat;
    private String jti;

    @SerializedName("software_environment")
    private String softwareEnvironment;

    @SerializedName("software_mode")
    private String softwareMode;

    @SerializedName("software_id")
    private String softwareId;

    @SerializedName("software_client_id")
    private String softwareClientId;

    @SerializedName("organisation_competent_authority_claims")
    private AuthorityClaims organisationCompetentAuthorityClaims;

    @SerializedName("org_status")
    private String orgStatus;

    @SerializedName("org_id")
    private String orgId;

    @SerializedName("org_name")
    private String orgName;

    @SerializedName("org_jwks_endpoint")
    private String orgJwksEndpoint;

    @SerializedName("org_jwks_revoked_endpoint")
    private String orgJwksRevokedEndpoint;

    @SerializedName("software_on_behalf_of_org")
    private String softwareOnBehalfOfOrg;

    @SerializedName("ob_registry_tos")
    private String obRegistryTOS;

    public String getIss() {

        return iss;
    }

    public void setIss(String iss) {

        this.iss = iss;
    }

    public String getIat() {

        return iat;
    }

    public void setIat(String iat) {

        this.iat = iat;
    }

    public String getJti() {

        return jti;
    }

    public void setJti(String jti) {

        this.jti = jti;
    }

    public String getSoftwareEnvironment() {

        return softwareEnvironment;
    }

    public void setSoftwareEnvironment(String softwareEnvironment) {

        this.softwareEnvironment = softwareEnvironment;
    }

    public String getSoftwareMode() {

        return softwareMode;
    }

    public void setSoftware_mode(String softwareMode) {

        this.softwareMode = softwareMode;
    }

    public String getSoftwareId() {

        return softwareId;
    }

    public void setSoftwareId(String softwareId) {

        this.softwareId = softwareId;
    }

    public String getSoftwareClientId() {

        return softwareClientId;
    }

    public void setSoftwareClientId(String softwareClientId) {

        this.softwareClientId = softwareClientId;
    }

    public abstract String getSoftwareClientName();

    public abstract String getSoftwareClientDescription();

    public AuthorityClaims getOrganisationCompetentAuthorityClaims() {

        return organisationCompetentAuthorityClaims;
    }

    public void setOrganisationCompetentAuthorityClaims(AuthorityClaims organisationCompetentAuthorityClaims) {

        this.organisationCompetentAuthorityClaims = organisationCompetentAuthorityClaims;
    }

    public String getOrgStatus() {

        return orgStatus;
    }

    public void setOrgStatus(String orgStatus) {

        this.orgStatus = orgStatus;
    }

    public String getOrgId() {

        return orgId;
    }

    public void setOrgId(String orgId) {

        this.orgId = orgId;
    }

    public String getOrgName() {

        return orgName;
    }

    public void setOrgName(String orgName) {

        this.orgName = orgName;
    }

    public String getOrgJwksEndpoint() {

        return orgJwksEndpoint;
    }

    public void setOrgJwksEndpoint(String orgJwksEndpoint) {

        this.orgJwksEndpoint = orgJwksEndpoint;
    }

    public String getOrgJwksRevokedEndpoint() {

        return orgJwksRevokedEndpoint;
    }

    public void setOrgJwksRevokedEndpoint(String orgJwksRevokedEndpoint) {

        this.orgJwksRevokedEndpoint = orgJwksRevokedEndpoint;
    }

    public String getSoftwareOnBehalfOfOrg() {

        return softwareOnBehalfOfOrg;
    }

    public void setSoftwareOnBehalfOfOrg(String softwareOnBehalfOfOrg) {

        this.softwareOnBehalfOfOrg = softwareOnBehalfOfOrg;
    }

    public String getObRegistryTOS() {

        return obRegistryTOS;
    }

    public void setObRegistryTOS(String obRegistryTOS) {

        this.obRegistryTOS = obRegistryTOS;
    }

    public abstract String getSoftwareJwksEndpoint();

    public abstract String getSoftwareJwksRevokedEndpoint();

    public abstract String getSoftwareRoles();

    public abstract String getSoftwareClientUri();

    public abstract String getLogoUri();

    public abstract String getTosUri();

    public abstract String getPolicyUri();

    public abstract String getScope();

    public abstract ArrayList<String> getRedirectUris();

    public abstract String getRecipientBaseUri();
}
