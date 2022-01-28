package org.ob.wso2.common;

import java.util.Date;
import java.util.List;

public class CertificateContent {
    private String pspAuthorisationNumber;
    private List<String> pspRoles;
    private String name;
    private String ncaName;
    private String ncaId;
    private Date notAfter = null;
    private Date notBefore = null;


    public String getPspAuthorisationNumber() {

        return pspAuthorisationNumber;
    }

    public void setPspAuthorisationNumber(String pspAuthorisationNumber) {

        this.pspAuthorisationNumber = pspAuthorisationNumber;
    }

    public List<String> getPspRoles() {

        return pspRoles;
    }

    public void setPspRoles(List<String> pspRoles) {

        this.pspRoles = pspRoles;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getNcaName() {

        return ncaName;
    }

    public void setNcaName(String ncaName) {

        this.ncaName = ncaName;
    }

    public String getNcaId() {

        return ncaId;
    }

    public void setNcaId(String ncaId) {

        this.ncaId = ncaId;
    }

    public Date getNotAfter() {

        return new Date(notAfter.getTime());
    }

    public void setNotAfter(Date notAfter) {

        this.notAfter = new Date(notAfter.getTime());
    }

    public Date getNotBefore() {

        return new Date(notBefore.getTime());
    }

    public void setNotBefore(Date notBefore) {

        this.notBefore = new Date(notBefore.getTime());
    }
}
