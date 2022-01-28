/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package org.ob.wso2.common;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PSPRoles class
 */
public class PSPRoles {

    private final List<PSPRole> roles;

    public PSPRoles(List<PSPRole> roles) {

        this.roles = roles;
    }

    public static PSPRoles getInstance(Object obj) {

        if (obj instanceof PSPRoles) {
            return (PSPRoles) obj;
        }

        ASN1Encodable[] array = DERSequence.getInstance(obj).toArray();

        List<PSPRole> pspRoles = new ArrayList<>();
        List<ASN1Encodable> arrayList = Arrays.asList(array);
        for (ASN1Encodable asn1Encodable : arrayList) {
            pspRoles.addAll(PSPRole.getInstance(asn1Encodable));
        }
        return new PSPRoles(pspRoles);
    }


    public List<PSPRole> getRoles() {

        return roles;
    }

}
