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
 * Class containing the authority claims of TPP.
 */
public class AuthorityClaims {

    @SerializedName("authority_id")
    private String authorityID;

    @SerializedName("registration_id")
    private String registrationID;

    private String status;
    private ArrayList<AuthorizedRoles> authorisations;

    public String getAuthorityID() {

        return authorityID;
    }

    public void setAuthorityID(String authorityID) {

        this.authorityID = authorityID;
    }

    public String getRegistrationID() {

        return registrationID;
    }

    public void setRegistrationID(String registrationID) {

        this.registrationID = registrationID;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public ArrayList<AuthorizedRoles> getAuthorisations() {

        return authorisations;
    }

    public void setAuthorisations(ArrayList<AuthorizedRoles> authorisations) {

        this.authorisations = authorisations;
    }

}
