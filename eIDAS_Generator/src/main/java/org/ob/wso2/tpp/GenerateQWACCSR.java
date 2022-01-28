package org.ob.wso2.tpp;

import org.ob.wso2.utils.PropertyFile;
import org.ob.wso2.utils.Utils;

import java.io.IOException;

/**
 * Create TPP CSR
 */
public class GenerateQWACCSR {

    String cnfFile;
    Process process;
    String outputFolder = PropertyFile.getInstance().getOutputLocation() + "/output/";

    public GenerateQWACCSR() throws Exception {

        writeCNFFile();
        execCommands();
    }

    private void execCommands() throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();
        process = runtime.exec("openssl req -new -config " + outputFolder + "open-ssl-config-qwac.cnf -out "
                + outputFolder + "qwac.csr -keyout " + outputFolder + "qwac.key -sha256 -passout pass:wso2carbon");
        process.waitFor();
        process = runtime.exec("openssl rsa -in " + outputFolder + "qwac.key -out " + outputFolder + "qwac_decrypt.key " +
                "-passin pass:wso2carbon");
        process.waitFor();
    }

    private void writeCNFFile() throws Exception {

        cnfFile = Utils.readFile(true);
        Utils.writeFile(Utils.replaceProperties(cnfFile, false), false, false);
    }

}
