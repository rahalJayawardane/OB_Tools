package org.ob.wso2.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFileReader {
    InputStream inputStream;
    PropertyFile properties = PropertyFile.getInstance();
    Properties prop = new Properties();

    public void getPropValues() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            properties.setCreateCA(Boolean.parseBoolean(prop.getProperty("createCA")));
            properties.setCountryName(prop.getProperty("countryName"));
            properties.setOrganizationName(prop.getProperty("organizationName"));
            properties.setOrganizationIdentifier(prop.getProperty("organizationIdentifier"));
            properties.setCommonName(prop.getProperty("commonName"));
            properties.setEmailAddress(prop.getProperty("emailAddress"));
            properties.setPSP_AI(Boolean.parseBoolean(prop.getProperty("PSP_AI")));
            properties.setPSP_AS(Boolean.parseBoolean(prop.getProperty("PSP_AS")));
            properties.setPSP_PI(Boolean.parseBoolean(prop.getProperty("PSP_PI")));
            properties.setPSP_IC(Boolean.parseBoolean(prop.getProperty("PSP_IC")));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

}
