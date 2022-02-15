package org.ob.wso2.Decrypt;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import javax.crypto.Cipher;

public class MainDecrypt {

    public static void main(String[] args) {
        String password = "wso2carbon"; //args[0];
        String file = "/Users/rahal/Desktop/ARB_Docker/wso2-obam-1.3.0/repository/resources/security/wso2carbon.jks"; // args[1];
        String encryptedTextKey = "eyJjIjoiZmhpSFF6VnNYTy8vMjVySzgrdXhaQllqY2htSVgzWkRZbmhpRFh0UlllUmo5Y1J4MnNGN3BIZC82dGt3c0RGd0p0ZXVGK1BPL3ExYVNMN1ZtZTVPRExGNmdZV1k4NW16Wmp3RGFUOVc2U0VOa2RVUEJtblMrN3llQVMweFVBaWNTNSsrcmtlcXY2b2tkN3d1bFRWS2dsYklacy9RVDBLNHJQMHhFY1cwTWhiU2FQTWlXc3o2VzRuYmJ5NUllT0h0bklxZlJyVDV6MGl0NmdZQXpZSWRsS0ZnbmJhRWhLRkNzdHBORXB2RUpjWnBBQkhwVjFnL0NBNHoyQXhTWmMwSCs2elZpMG9vV0VTZEpJZDlJVC9mN20ybnJmUVBqRC9JbDRwZjl0ZGlzL2trZWJzN2xGZnJQOUNhaHlBMW9oRjNVWVJVVGE4alROTmc2cWxjOGhUNm13XHUwMDNkXHUwMDNkIiwidCI6IlJTQS9FQ0IvT0FFUHdpdGhTSEExYW5kTUdGMVBhZGRpbmciLCJ0cCI6IjUwMUZDMTQzMkQ4NzE1NURDNDMxMzgyQUVCODQzRUQ1NThBRDYxQjEiLCJ0cGQiOiJTSEEtMSJ9"; //  args[2];


        decrypt(password, file, encryptedTextKey);


    }

    public static void decrypt(String password, String file, String encryptedTextKey) {
        byte[] decryptedData = null;

        try {
            FileInputStream is = new FileInputStream(file);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, password.toCharArray());
            String alias = password;
            Key key = keystore.getKey(alias, password.toCharArray());

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedData = cipher.doFinal(encryptedTextKey.getBytes());
            System.out.println("Decrypted Key: " + decryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
