package org.ob.wso2.tpp;

import org.ob.wso2.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Create TPP CSR
 */
public class GenerateQSEALCSR {
    String cnfFile;

    public GenerateQSEALCSR() throws Exception {
        writeCNFFile();
        runCommand();
    }

    private void runCommand() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("openssl req -new -config ./output/open-ssl-config-qseal.cnf -out ./output/qseal.csr " +
                "-keyout ./output/qseal.key -sha256 -passout pass:wso2carbon");
        runtime.exec("openssl rsa -in ./output/qseal.key -out ./output/qseal_decrypt.key -passin " +
                "pass:wso2carbon");
    }

    private void writeCNFFile() throws Exception {
        cnfFile = Utils.readFile();
        Utils.writeFile(Utils.replaceProperties(cnfFile, true), true);
    }
}
