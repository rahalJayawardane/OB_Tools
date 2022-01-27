package org.ob.wso2;

import org.ob.wso2.ca.GenerateCAKeys;
import org.ob.wso2.tpp.GenerateQSEALCSR;
import org.ob.wso2.tpp.GenerateQWACCSR;
import org.ob.wso2.tpp.SignCA;
import org.ob.wso2.utils.ConfigFileReader;
import org.ob.wso2.utils.PropertyFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Main Class
 */
public class Generate {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Error: Add the config file location as parameter.");
            System.exit(0);
        }

        String fileLocation = args[0];
        boolean exit = false;
        if (fileLocation.contains("config.properties")) {
            try {
                ConfigFileReader file = new ConfigFileReader();
                file.getPropValues(fileLocation);
            } catch (Exception e) {
                exit = true;
            }
        } else {
            System.out.println("Error: Path should include the file name 'config.properties'");
            exit = true;
        }

        if (exit) {
            System.exit(0);
        }

        PropertyFile properties = PropertyFile.getInstance();

        // Generate QSEAL CSR
        System.out.println("Creating QSeal CSR...");
        new GenerateQSEALCSR();
        System.out.println("done");

        // Generate QWAC CSR
        System.out.println("Creating QWAC CSR...");
        new GenerateQWACCSR();
        System.out.println("done");

        if (properties.isCreateCA()) {
            System.out.println("Creating CA Self-Signing Certificate...");
            new GenerateCAKeys();
            System.out.println("done");

            System.out.println("Signing CSRs from CA's Certificate...");
            new SignCA();
            System.out.println("done");
            System.out.println("Finished.");
        } else {
            System.out.println("CSR creation completed. Now you have to sign those from a QSTP before using OB Flows.");
        }
    }
}
