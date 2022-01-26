package org.ob.wso2.tpp;

import org.ob.wso2.utils.Utils;

/**
 * Create TPP CSR
 */
public class GenerateQSEALCSR {
    String cnfFile;

    public GenerateQSEALCSR() throws Exception {
        cnfFile = Utils.readFile();
        Utils.writeFile(Utils.replaceProperties(cnfFile), true);
    }

    // openssl req -new -config open-ssl-config-qwac.cnf -out qwac.csr -keyout qwac.key -sha256 -passout pass:wso2carbon
    // openssl rsa -in qwac.key -out qwac_decrypt.key -passin pass:wso2carbon
}
