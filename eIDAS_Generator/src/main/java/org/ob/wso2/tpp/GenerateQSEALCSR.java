package org.ob.wso2.tpp;

import org.ob.wso2.utils.PropertyFile;
import org.ob.wso2.utils.Utils;

import java.io.IOException;

/**
 * Create TPP CSR
 */
public class GenerateQSEALCSR {
    String cnfFile;
    Process process;
    String outputFolder = PropertyFile.getInstance().getOutputLocation() + "/output/";

    public GenerateQSEALCSR() throws Exception {
        writeCNFFile();
        execCommands();
    }

    private void execCommands() throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();
        process = runtime.exec("openssl req -new -config " + outputFolder + "open-ssl-config-qseal.cnf -out "
                + outputFolder + "qseal.csr -keyout " + outputFolder + "qseal.key -sha256 -passout pass:wso2carbon");
        process.waitFor();
        process = runtime.exec("openssl rsa -in " + outputFolder + "qseal.key -out " + outputFolder + "qseal_decrypt.key" +
                " -passin pass:wso2carbon");
        process.waitFor();
    }

    private void writeCNFFile() throws Exception {
        cnfFile = Utils.readFile(true);
        Utils.writeFile(Utils.replaceProperties(cnfFile, true), true, false);
    }
}
