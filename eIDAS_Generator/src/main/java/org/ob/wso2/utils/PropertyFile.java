package org.ob.wso2.utils;

/**
 * Property class
 */
public class PropertyFile {

    private static PropertyFile property_file = null;

    public static PropertyFile getInstance()
    {
        if (property_file == null)
            property_file = new PropertyFile();

        return property_file;
    }

    private boolean createCA;
    private String countryName;
    private String organizationName;
    private String organizationIdentifier;
    private String commonName;
    private String emailAddress;
    private boolean PSP_AS;
    private boolean PSP_PI;
    private boolean PSP_AI;
    private boolean PSP_IC;

    public boolean isCreateCA() {

        return createCA;
    }

    public void setCreateCA(boolean createCA) {

        this.createCA = createCA;
    }

    public String getCountryName() {

        return countryName;
    }

    public void setCountryName(String countryName) {

        this.countryName = countryName;
    }

    public String getOrganizationName() {

        return organizationName;
    }

    public void setOrganizationName(String organizationName) {

        this.organizationName = organizationName;
    }

    public String getOrganizationIdentifier() {

        return organizationIdentifier;
    }

    public void setOrganizationIdentifier(String organizationIdentifier) {

        this.organizationIdentifier = organizationIdentifier;
    }

    public String getCommonName() {

        return commonName;
    }

    public void setCommonName(String commonName) {

        this.commonName = commonName;
    }

    public String getEmailAddress() {

        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {

        this.emailAddress = emailAddress;
    }

    public boolean isPSP_AS() {

        return PSP_AS;
    }

    public void setPSP_AS(boolean PSP_AS) {

        this.PSP_AS = PSP_AS;
    }

    public boolean isPSP_PI() {

        return PSP_PI;
    }

    public void setPSP_PI(boolean PSP_PI) {

        this.PSP_PI = PSP_PI;
    }

    public boolean isPSP_AI() {

        return PSP_AI;
    }

    public void setPSP_AI(boolean PSP_AI) {

        this.PSP_AI = PSP_AI;
    }

    public boolean isPSP_IC() {

        return PSP_IC;
    }

    public void setPSP_IC(boolean PSP_IC) {

        this.PSP_IC = PSP_IC;
    }
}
