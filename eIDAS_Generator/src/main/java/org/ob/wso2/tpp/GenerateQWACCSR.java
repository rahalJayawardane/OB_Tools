package org.ob.wso2.tpp;

import org.ob.wso2.utils.Utils;

import java.io.IOException;

/**
 * Create TPP CSR
 */
public class GenerateQWACCSR {

    String cnfFile;

    public GenerateQWACCSR() throws Exception {
        writeCNFFile();
        runCommand();
    }

    private void runCommand() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("openssl req -new -config ./output/open-ssl-config-qwac.cnf -out ./output/qwac.csr " +
                "-keyout ./output/qwac.key -sha256 -passout pass:wso2carbon");
        runtime.exec("openssl rsa -in ./output/qwac.key -out ./output/qwac_decrypt.key -passin " +
                "pass:wso2carbon");
    }

    private void writeCNFFile() throws Exception {
        cnfFile = Utils.readFile();
        Utils.writeFile(Utils.replaceProperties(cnfFile, false), false);
    }

}
