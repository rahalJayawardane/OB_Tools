package org.ob.wso2.ca;

import org.ob.wso2.utils.PropertyFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Create CA's private and public keys
 */
public class GenerateCAKeys {

    String outputFolder = PropertyFile.getInstance().getOutputLocation() + "/output/";
    PropertyFile properties = PropertyFile.getInstance();

    public GenerateCAKeys() throws Exception {

        createKeys();
    }

    private void createKeys() throws Exception {

        Runtime runtime = Runtime.getRuntime();
        ProcessBuilder openSslBuilder = new ProcessBuilder("openssl", "req", "-x509", "-newkey",
                "rsa:2048", "-keyout", outputFolder + "/ca_key.pem", "-out",
                outputFolder + "ca_cert.pem", "-days", "365", "-passout", "pass:wso2carbon",
                "-subj", "/C=" + properties.getCa_country() + "/ST=" + properties.getCa_country() +
                "/L=" + properties.getCa_country() + "/O=" + properties.getCa_country() +
                "/OU=" + properties.getCa_country() + "/CN=" + properties.getCa_country());
        Process process = openSslBuilder.start();
        process.waitFor();

        String command = "openssl x509 -outform der -in " + outputFolder + "ca_cert.pem -out " + outputFolder +
                "ca_certificate.cer";
        runtime.exec(command);
    }

}
