package org.ob.wso2.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CommonParser {

    InputStream inputStream;
    private static CommonParser commonParser;
    private int connectionTimeOut;
    private int readTimeout;
    private String dcrProduction;
    private String dcrSandbox;
    private String tppJWT;
    private String tppTLS;
    private String trustStore;
    private String trustStorePassword;
    private boolean validateURI;
    private boolean ValidateURIHostName;
    private List<String> TokenAuthenticationMethods;

    public static CommonParser getInstance() throws IOException {

        if (commonParser == null)
            commonParser = new CommonParser();
        return commonParser;
    }

    private void setProperties(Properties prop) {
        setConnectionTimeOut(Integer.parseInt(prop.getProperty("ConnectionTimeout")));
        setReadTimeout(Integer.parseInt(prop.getProperty("ReadTimeout")));
        setDcrSandbox(prop.getProperty("DCRJwksUrlSandbox"));
        setDcrProduction(prop.getProperty("DCRJwksUrlProduction"));
        setValidateURI(Boolean.parseBoolean(prop.getProperty("ValidateURI")));
        setValidateURIHostName(Boolean.parseBoolean(prop.getProperty("ValidateURIHostName")));
        String AuthenticationMethods = prop.getProperty("TokenAuthenticationMethods");
        setTokenAuthenticationMethods(Arrays.asList(AuthenticationMethods.split(",")));
        setTppJWT(prop.getProperty("TPP_JWT"));
        setTppTLS(prop.getProperty("TPP_TLS"));
        setTrustStore(prop.getProperty("client_trustStore"));
        setTrustStorePassword(prop.getProperty("trustStore_password"));
    }

    public void readConfigFile(String fileName) throws IOException {

        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(fileName);
            prop.load(inputStream);
            setProperties(prop);
        } catch (Exception e) {
            throw new FileNotFoundException(fileName + "' is not a correct property file");
        } finally {
            inputStream.close();
        }
    }

    public int getConnectionTimeOut() {

        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {

        this.connectionTimeOut = connectionTimeOut;
    }

    public int getReadTimeout() {

        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {

        this.readTimeout = readTimeout;
    }

    public String getDcrProduction() {

        return dcrProduction;
    }

    public void setDcrProduction(String dcrProduction) {

        this.dcrProduction = dcrProduction;
    }

    public String getDcrSandbox() {

        return dcrSandbox;
    }

    public void setDcrSandbox(String dcrSandbox) {

        this.dcrSandbox = dcrSandbox;
    }

    public boolean isValidateURI() {

        return validateURI;
    }

    public void setValidateURI(boolean validateURI) {

        this.validateURI = validateURI;
    }

    public boolean isValidateURIHostName() {

        return ValidateURIHostName;
    }

    public void setValidateURIHostName(boolean validateURIHostName) {

        ValidateURIHostName = validateURIHostName;
    }

    public List<String> getTokenAuthenticationMethods() {

        return TokenAuthenticationMethods;
    }

    public void setTokenAuthenticationMethods(List<String> tokenAuthenticationMethods) {

        TokenAuthenticationMethods = tokenAuthenticationMethods;
    }

    public String getTppJWT() {

        return tppJWT;
    }

    public void setTppJWT(String tppJWT) {

        this.tppJWT = tppJWT;
    }

    public String getTppTLS() {

        return tppTLS;
    }

    public void setTppTLS(String tppTLS) {

        this.tppTLS = tppTLS;
    }

    public String getTrustStore() {

        return trustStore;
    }

    public void setTrustStore(String trustStore) {

        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {

        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {

        this.trustStorePassword = trustStorePassword;
    }
}
