package org.ob.wso2.Decrypt;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.Base64;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;
import org.apache.axiom.util.base64.Base64Utils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class MainDecrypt {

    static PrivateKey privateKey = null;
    static PublicKey publicKey = null;

    public static void main(String[] args) throws Exception{
        String password = "wso2carbon"; //args[0];
        String file = "/Users/rahal/Desktop/ARB_Docker/wso2-obam-1.3.0/repository/resources/security/wso2carbon.jks"; // args[1];
        String encryptedTextKey = "eyJjIjoiZmhpSFF6VnNYTy8vMjVySzgrdXhaQllqY2htSVgzWkRZbmhpRFh0UlllUmo5Y1J4MnNGN3BIZC82dGt3c0RGd0p0ZXVGK1BPL3ExYVNMN1ZtZTVPRExGNmdZV1k4NW16Wmp3RGFUOVc2U0VOa2RVUEJtblMrN3llQVMweFVBaWNTNSsrcmtlcXY2b2tkN3d1bFRWS2dsYklacy9RVDBLNHJQMHhFY1cwTWhiU2FQTWlXc3o2VzRuYmJ5NUllT0h0bklxZlJyVDV6MGl0NmdZQXpZSWRsS0ZnbmJhRWhLRkNzdHBORXB2RUpjWnBBQkhwVjFnL0NBNHoyQXhTWmMwSCs2elZpMG9vV0VTZEpJZDlJVC9mN20ybnJmUVBqRC9JbDRwZjl0ZGlzL2trZWJzN2xGZnJQOUNhaHlBMW9oRjNVWVJVVGE4alROTmc2cWxjOGhUNm13XHUwMDNkXHUwMDNkIiwidCI6IlJTQS9FQ0IvT0FFUHdpdGhTSEExYW5kTUdGMVBhZGRpbmciLCJ0cCI6IjUwMUZDMTQzMkQ4NzE1NURDNDMxMzgyQUVCODQzRUQ1NThBRDYxQjEiLCJ0cGQiOiJTSEEtMSJ9"; //  args[2];
        getKeysFromKeyStore(file, password, password, password);
//        String enc = encryptPlainText("arbUK@123");
        String string = "arbUK@123";
        byte[] ciper = encrypt(privateKey, string.getBytes(StandardCharsets.UTF_8));
        System.out.println(ciper.toString());
        System.out.println(decrypt(publicKey, ciper));
//        decrypt(password, file, enc);

    }

    public static void decrypt(String password, String file, String encryptedTextKey) {
        byte[] decryptedData = null;

        try {
            FileInputStream is = new FileInputStream(file);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, password.toCharArray());
            String alias = password;
            Key key = keystore.getKey(alias, password.toCharArray());

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding","BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedData = cipher.doFinal(encryptedTextKey.getBytes());
            System.out.println("Decrypted Key: " + decryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//

//
    private static PrivateKey getKeysFromKeyStore(String keyStoreFilePath, String keyStorePassword,
                                                 String privateKeyCertAlias, String privateKeyPassword) throws Exception {
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
//            BASE64Encoder encoder = new BASE64Encoder();
            keystore.load(new FileInputStream(keyStoreFilePath), keyStorePassword.toCharArray());
            Key key=keystore.getKey(privateKeyCertAlias,keyStorePassword.toCharArray());
            if(key instanceof PrivateKey) {
                Certificate cert=keystore.getCertificate(privateKeyCertAlias);
                publicKey=cert.getPublicKey();
                KeyPair keyPair = new KeyPair(publicKey,(PrivateKey)key);
                privateKey = keyPair.getPrivate();
            }
            //privateKeyEncoded = encoder.encode(privateKey.getEncoded());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return privateKey;
    }
//
    public static String encryptPlainText(String plainText) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        String cipherTransformation = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedByte = cipher.doFinal(plainText.getBytes());
//        return Base64.encode(encryptedByte);
        return null;
//
//        try {
//            byte[] encryptedKey = cipher.doFinal((plainText.getBytes()));
//            return Base64.encode(encryptedKey);
//        } catch (GeneralSecurityException e) {
//            String errMsg = "Failed to generate the cipher text";
//            throw new Exception(errMsg, e);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("Error while adding the password - too much data for RSA block");
//            throw e;
//        }
    }

    public static byte[] encrypt(PrivateKey key, byte[] plaintext) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(PublicKey key, byte[] ciphertext) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }



}
