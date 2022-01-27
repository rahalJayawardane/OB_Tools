package org.ob.wso2.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Utils {
    static PropertyFile properties = PropertyFile.getInstance();
    static String fileName = "temple.cnf";
    static String roles;
    static String qcStatement;

    public static String readFile() throws Exception {
        InputStream inputStream = Utils.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream != null) {
            BufferedInputStream bufferInput = new BufferedInputStream(inputStream);
            ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();
            for (int result = bufferInput.read(); result != -1; result = bufferInput.read()) {
                bufferOutput.write((byte) result);
            }
            return bufferOutput.toString("UTF-8");
        } else {
            throw new FileNotFoundException(fileName + " not found in the classpath");
        }
    }

    public static String replaceProperties(String text, boolean isQseal) throws Exception {

        String type = "QSEAL";
        if (!isQseal) {
            type = "QWAC";
        }
        setQSealQCStatement();
        text = text.replaceAll("<COUN>", properties.getCountryName());
        text = text.replaceAll("<ORG>", properties.getOrganizationName());
        text = text.replaceAll("<ORG_ID>", properties.getOrganizationIdentifier());
        text = text.replaceAll("<CN>", properties.getCommonName());
        text = text.replaceAll("<EMAIL>", properties.getEmailAddress());
        text = text.replaceAll("<STAT>", qcStatement);
        text = text.replaceAll("<ROLES>", roles);
        text = text.replaceAll("<TYPE>", type);
        return text;
    }

    public static void writeFile(String text, boolean isQSeal) throws Exception {

        String fileName;

        if (isQSeal) {
            fileName = "open-ssl-config-qseal.cnf";
        } else {
            fileName = "open-ssl-config-qwac.cnf";
        }

        File file = new File("./output/" + fileName);
        FileOutputStream fileOutput = new FileOutputStream(file, false);
        byte[] textBytes = text.getBytes();
        fileOutput.write(textBytes);
        fileOutput.close();
    }

    public static void setQSealQCStatement () throws Exception {

        int total = 0;
        total = total + (properties.isPSP_AS() ? 1 : 0);
        total = total + (properties.isPSP_AI() ? 2 : 0);
        total = total + (properties.isPSP_PI() ? 4 : 0);
        total = total + (properties.isPSP_IC() ? 8 : 0);

        if (total == 0) {
            throw new Exception("No Roles selected");
        } else {
            switch (total) {
                case 1:
                    roles = "PSP_AS";
                    qcStatement = "DER:305b3013060604008e4601063009060704008e4601060230440606040081982702303a301330110607040081982701010c065053505f41530c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 2:
                    roles = "PSP_AI";
                    qcStatement = "DER:305b3013060604008e4601063009060704008e4601060230440606040081982702303a301330110607040081982701030c065053505f41490c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 3:
                    roles = "PSP_AS PSP_AI";
                    qcStatement = "DER:306c3013060604008e4601063009060704008e4601060230550606040081982702304b302430220607040081982701010c065053505f41530607040081982701030c065053505f41490c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 4:
                    roles = "PSP_PI";
                    qcStatement = "DER:305b3013060604008e4601063009060704008e4601060230440606040081982702303a301330110607040081982701020c065053505f50490c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 5:
                    roles = "PSP_AS PSP_PI";
                    qcStatement = "DER:306c3013060604008e4601063009060704008e4601060230550606040081982702304b302430220607040081982701010c065053505f41530607040081982701020c065053505f50490c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 6:
                    roles = "PSP_PI PSP_AI";
                    qcStatement = "DER:306c3013060604008e4601063009060704008e4601060230550606040081982702304b302430220607040081982701020c065053505f50490607040081982701030c065053505f41490c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 7:
                    roles = "PSP_AS PSP_PI PSP_AI";
                    qcStatement = "DER:307d3013060604008e4601063009060704008e4601060230660606040081982702305c303530330607040081982701010c065053505f4153060701940081982701020c065053505f50490607040081982701030c065053505f41490c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 8:
                    roles = "PSP_IC";
                    qcStatement = "DER:305b3013060604008e4601063009060704008e4601060230440606040081982702303a301330110607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 9:
                    roles = "PSP_AS PSP_IC";
                    qcStatement = "DER:306c3013060604008e4601063009060704008e4601060230550606040081982702304b302430220607040081982701010c065053505f41530607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 10:
                    roles = "PSP_AI PSP_IC";
                    qcStatement = "DER:306c3013060604008e4601063009060704008e4601060230550606040081982702304b302430220607040081982701030c065053505f41490607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 11:
                    roles = "PSP_AS PSP_AI PSP_IC";
                    qcStatement = "DER:307d3013060604008e4601063009060704008e4601060230660606040081982702305c303530330607040081982701010c065053505f41530607040081982701030c065053505f41490607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 12:
                    roles = "PSP_PI PSP_IC";
                    qcStatement = "DER:306c3013060604008e4601063009060704008e4601060230550606040081982702304b302430220607040081982701020c065053505f50490607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 13:
                    roles = "PSP_AS PSP_PI PSP_IC";
                    qcStatement = "DER:307d3013060604008e4601063009060704008e4601060230660606040081982702305c303530330607040081982701010c065053505f41530607040081982701020c065053505f50490607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 14:
                    roles = "PSP_PI PSP_AI PSP_IC";
                    qcStatement = "DER:307d3013060604008e4601063009060704008e4601060230660606040081982702305c303530330607040081982701020c065053505f50490607040081982701030c065053505f41490607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                case 15:
                    roles = "PSP_AS PSP_PI PSP_AI PSP_IC";
                    qcStatement = "DER:30818e3013060604008e4601063009060704008e4601060230770606040081982702306d304630440607040081982701010c065053505f41530607040081982701020c065053505f50490607040081982701030c065053505f41490607040081982701040c065053505f49430c1b46696e616e6369616c20436f6e6475637420417574686f726974790c0647422d464341";
                    break;
                default:
                    throw new Exception("Undefined Roles selection");
            }
        }
    }

}
