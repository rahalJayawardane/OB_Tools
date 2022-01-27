package org.ob.wso2.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFileReader {

    InputStream inputStream;
    PropertyFile properties = PropertyFile.getInstance();
    Properties prop = new Properties();

    public void getPropValues(String fileName) throws IOException {

        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(fileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException(fileName + "' is not a correct property file");
            }

            String output = prop.getProperty("output");

            if (output.endsWith("/")) {
                properties.setOutputLocation(output.substring(0, output.length() - 2));
            } else {
                properties.setOutputLocation(prop.getProperty("output"));
            }

            properties.setCreateCA(Boolean.parseBoolean(prop.getProperty("createCA")));
            properties.setCountryName(prop.getProperty("countryName"));
            properties.setOrganizationName(prop.getProperty("organizationName"));
            properties.setOrganizationIdentifier(prop.getProperty("organizationIdentifier"));
            properties.setCommonName(prop.getProperty("commonName"));
            properties.setEmailAddress(prop.getProperty("emailAddress"));

            //Roles
            properties.setPSP_AI(Boolean.parseBoolean(prop.getProperty("PSP_AI")));
            properties.setPSP_AS(Boolean.parseBoolean(prop.getProperty("PSP_AS")));
            properties.setPSP_PI(Boolean.parseBoolean(prop.getProperty("PSP_PI")));
            properties.setPSP_IC(Boolean.parseBoolean(prop.getProperty("PSP_IC")));

            //CA Details
            properties.setCa_country(prop.getProperty("ca_country"));
            properties.setCa_state(prop.getProperty("ca_state"));
            properties.setCa_locality(prop.getProperty("ca_locality"));
            properties.setCa_org(prop.getProperty("ca_org"));
            properties.setCa_orgUnit(prop.getProperty("ca_orgUnit"));
            properties.setCa_commonName(prop.getProperty("ca_commonName"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

}
