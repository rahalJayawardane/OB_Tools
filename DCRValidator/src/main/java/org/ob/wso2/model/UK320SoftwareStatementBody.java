/*
 * Copyright (c)  2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
package org.ob.wso2.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * UK320SoftwareStatementBody class.
 */
public class UK320SoftwareStatementBody extends OBSoftwareStatementPayload {

    @SerializedName("software_client_name")
    private String softwareClientName;

    @SerializedName("software_client_description")
    private String softwareClientDescription;

    @SerializedName("software_version")
    private String softwareVersion;

    @SerializedName("software_client_uri")
    private String softwareClientUri;

    @SerializedName("software_redirect_uris")
    private ArrayList<String> softwareRedirectUri;

    @SerializedName("software_roles")
    private ArrayList<String> softwareRoles;

    @SerializedName("org_contacts")
    private ArrayList<Contact> orgContacts;

    @SerializedName("software_logo_uri")
    private String softwareLogoUri;

    @SerializedName("software_jwks_endpoint")
    private String softwareJwksEndpoint;

    @SerializedName("software_jwks_revoked_endpoint")
    private String softwareJwksRevokedEndpoint;

    @SerializedName("software_policy_uri")
    private String softwarePolicyUri;

    @SerializedName("software_tos_uri")
    private String softwareTOSUri;

    public String getSoftwareClientName() {
        return softwareClientName;
    }

    public void setSoftwareClientName(String softwareClientName) {
        this.softwareClientName = softwareClientName;
    }

    public String getSoftwareClientDescription() {
        return softwareClientDescription;
    }

    public void setSoftwareClientDescription(String softwareClientDescription) {
        this.softwareClientDescription = softwareClientDescription;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    @Override
    public String getSoftwareClientUri() {
        return softwareClientUri;
    }

    public void setSoftwareClientUri(String softwareClientUri) {
        this.softwareClientUri = softwareClientUri;
    }

    public ArrayList<String> getRedirectUris() {
        return softwareRedirectUri;
    }

    @Override
    public String getRecipientBaseUri() {
        return null;
    }

    public void setRedirectUris(ArrayList<String> softwareRedirectUri) {
        this.softwareRedirectUri = softwareRedirectUri;
    }

    public String getSoftwareRoles() {
        return StringUtils.join(softwareRoles, ",");
    }

    public void setSoftwareRoles(ArrayList<String> softwareRoles) {
        this.softwareRoles = softwareRoles;
    }

    public ArrayList<Contact> getOrgContacts() {
        return orgContacts;
    }

    public void setOrgContacts(ArrayList<Contact> orgContacts) {
        this.orgContacts = orgContacts;
    }

    @Override
    public String getLogoUri() {
        return softwareLogoUri;
    }

    public void setLogoUri(String softwareLogoUri) {
        this.softwareLogoUri = softwareLogoUri;
    }

    @Override
    public String getSoftwareJwksEndpoint() {
        return softwareJwksEndpoint;
    }

    public void setSoftwareJwksEndpoint(String softwareJwksEndpoint) {
        this.softwareJwksEndpoint = softwareJwksEndpoint;
    }

    @Override
    public String getSoftwareJwksRevokedEndpoint() {
        return softwareJwksRevokedEndpoint;
    }

    public void setSoftwareJwksRevokedEndpoint(String softwareJwksRevokedEndpoint) {
        this.softwareJwksRevokedEndpoint = softwareJwksRevokedEndpoint;
    }

    @Override
    public String getPolicyUri() {
        return softwarePolicyUri;
    }

    public void setPolicyUri(String softwarePolicyUri) {
        this.softwarePolicyUri = softwarePolicyUri;
    }

    @Override
    public String getTosUri() {
        return softwareTOSUri;
    }

    public void setTosUri(String softwareTOSUri) {
        this.softwareTOSUri = softwareTOSUri;
    }

    @Override
    public String getScope() {
        return null;
    }
}
