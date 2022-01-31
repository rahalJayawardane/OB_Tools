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

/**
 * UK320SoftwareStatementHeader class.
 */
public class UK320SoftwareStatementHeader {

    private String typ;
    private String alg;
    private String kid;

    public String getContentType() {

        return typ;
    }

    public void setTyp(String typ) {

        this.typ = typ;
    }

    public String getAlg() {

        return alg;
    }

    public void setAlg(String alg) {

        this.alg = alg;
    }

    public String getKid() {

        return kid;
    }

    public void setKid(String kid) {

        this.kid = kid;
    }
}
