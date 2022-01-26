package org.ob.wso2;

import org.ob.wso2.tpp.GenerateQSEALCSR;
import org.ob.wso2.utils.ConfigFileReader;
import org.ob.wso2.utils.PropertyFile;

public class Generate {

    public static void main(String[] args) throws Exception {
        ConfigFileReader file = new ConfigFileReader();
        file.getPropValues();
        PropertyFile properties = PropertyFile.getInstance();
        System.out.println(properties.getCountryName());
        GenerateQSEALCSR QSeal =  new GenerateQSEALCSR();
    }
}
