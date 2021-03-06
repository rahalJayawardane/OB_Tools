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
package org.ob.wso2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Class containing authorized role details for TPP.
 */
public class AuthorizedRoles {

    @SerializedName("member_state")
    private String memberState;

    private ArrayList<String> roles;

    public String getMemberState() {

        return memberState;
    }

    public void setMember_state(String memberState) {

        this.memberState = memberState;
    }

    public ArrayList<String> getRoles() {

        return roles;
    }

    public void setRoles(ArrayList<String> roles) {

        this.roles = roles;
    }
}
