package org.ob.wso2.tpp;

import org.ob.wso2.utils.PropertyFile;
import org.ob.wso2.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Sign CSR from CA
 */
public class SignCA {

    String outputFolder = PropertyFile.getInstance().getOutputLocation() + "/output/";

    public SignCA() throws Exception{
        createExtFiles();
        execCommands();
    }

    private void createExtFiles() throws Exception {

        String extFile = Utils.readFile(false);

        // QSeal ext file
        Utils.writeFile(Utils.replaceProperties(extFile, true), true, true);

        // QWAC ext file
        Utils.writeFile(Utils.replaceProperties(extFile, false), false, true);
    }

    private void execCommands() throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();
        String cmdWAC = "openssl x509 -req -days 365 -in " + outputFolder + "qwac.csr -CA "
                + outputFolder + "ca_cert.pem -CAkey " + outputFolder + "ca_key.pem -CAcreateserial " +
                "-out " + outputFolder + "qwac.pem -sha256 -extensions v3_ca " +
                "-extfile " + outputFolder + "extensions-qwac.txt -passin pass:wso2carbon";

        String cmdSeal = "openssl x509 -req -days 365 -in " + outputFolder + "qseal.csr -CA "
                + outputFolder + "ca_cert.pem -CAkey " + outputFolder + "ca_key.pem -CAcreateserial " +
                "-out " + outputFolder + "qseal.pem -sha256 -extensions v3_ca " +
                "-extfile " + outputFolder + "extensions-qseal.txt -passin pass:wso2carbon";

        // signing QWAC
        runtime.exec(cmdWAC);

        // signing QSeal
        runtime.exec(cmdSeal);
    }
}
