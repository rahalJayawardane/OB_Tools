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
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERUTF8String;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * PSPRole enum
 */
public enum PSPRole {
    PSP_AS(PSD2Constants.PSP_AS_OID, PSD2Constants.PSP_AS, PSD2Constants.ASPSP),
    PSP_PI(PSD2Constants.PSP_PI_OID, PSD2Constants.PSP_PI, PSD2Constants.PISP),
    PSP_AI(PSD2Constants.PSP_AI_OID, PSD2Constants.PSP_AI, PSD2Constants.AISP),
    PSP_IC(PSD2Constants.PSP_IC_OID, PSD2Constants.PSP_IC, PSD2Constants.CBPII);

    private String pspRoleOid;    //Object Identifier in the Certificate
    private String pspRoleName;   //Role Name stated on the certificate
    private String psd2RoleName;  //PSD2 Actor Name related to the role in the certificate

    PSPRole(String pspRoleOid, String pspRoleName, String psd2RoleName) {

        this.pspRoleOid = pspRoleOid;
        this.pspRoleName = pspRoleName;
        this.psd2RoleName = psd2RoleName;
    }

    public static List<PSPRole> getInstance(ASN1Encodable asn1Encodable) {

        List<PSPRole> pspRoleList = new ArrayList<>();
        ASN1Sequence sequence = ASN1Sequence.getInstance(asn1Encodable);

        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            ASN1ObjectIdentifier objectIdentifier = ASN1ObjectIdentifier.getInstance(it.next());
            DERUTF8String instance = DERUTF8String.getInstance(it.next());

            pspRoleList.add(Arrays.stream(PSPRole.values())
                    .filter(role -> role.getPspRoleOid().equals(objectIdentifier.getId())
                            && role.getPspRoleName().equals(instance.getString()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException(
                            "unknown object in getInstance: " + asn1Encodable.getClass().getName())));
        }

        return pspRoleList;
    }


    public String getPspRoleOid() {

        return pspRoleOid;
    }

    public String getPspRoleName() {

        return pspRoleName;
    }

    public String getPsd2RoleName() {

        return psd2RoleName;
    }

}
