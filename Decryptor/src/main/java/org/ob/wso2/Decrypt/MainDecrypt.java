package org.ob.wso2.Decrypt;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.StringWriter;
import java.security.cert.X509Certificate;
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
import java.util.Base64;

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
//        byte[] ciper = encrypt(privateKey, string.getBytes(StandardCharsets.UTF_8));
//        System.out.println(ciper.toString());
//        System.out.println(decrypt(publicKey, ciper));

        byte[] ciper = generateEncryptedData(string);
        String string1 = new String(ciper);
        System.out.println(string1);

        System.out.println(generateDecryptedData("fhiHQzVsXO//25rK8+uxZBYjchmIX3ZDYnhiDXtRYeRj9cRx2sF7pHd/6tkwsDFwJteuF+PO/q1aSL7Vme5ODLF6gYWY85mzZjwDaT9W6SENkdUPBmnS+7yeAS0xUAicS5++rkeqv6okd7wulTVKglbIZs/QT0K4rP0xEcW0MhbSaPMiWsz6W4nbby5IeOHtnIqfRrT5z0it6gYAzYIdlKFgnbaEhKFCstpNEpvEJcZpABHpV1g/CA4z2AxSZc0H+6zVi0ooWESdJId9IT/f7m2nrfQPjD/Il4pf9tdis/kkebs7lFfrP9CahyA1ohF3UYRUTa8jTNNg6qlc8hT6mw==".getBytes(StandardCharsets.UTF_8)));

//        decrypt(password, file, enc);
//        eyJjIjoiZmhpSFF6VnNYTy8vMjVySzgrdXhaQllqY2htSVgzWkRZbmhpRFh0UlllUmo5Y1J4MnNGN3BIZC82dGt3c0RGd0p0ZXVGK1BPL3ExYVNMN1ZtZTVPRExGNmdZV1k4NW16Wmp3RGFUOVc2U0VOa2RVUEJtblMrN3llQVMweFVBaWNTNSsrcmtlcXY2b2tkN3d1bFRWS2dsYklacy9RVDBLNHJQMHhFY1cwTWhiU2FQTWlXc3o2VzRuYmJ5NUllT0h0bklxZlJyVDV6MGl0NmdZQXpZSWRsS0ZnbmJhRWhLRkNzdHBORXB2RUpjWnBBQkhwVjFnL0NBNHoyQXhTWmMwSCs2elZpMG9vV0VTZEpJZDlJVC9mN20ybnJmUVBqRC9JbDRwZjl0ZGlzL2trZWJzN2xGZnJQOUNhaHlBMW9oRjNVWVJVVGE4alROTmc2cWxjOGhUNm13XHUwMDNkXHUwMDNkIiwidCI6IlJTQS9FQ0IvT0FFUHdpdGhTSEExYW5kTUdGMVBhZGRpbmciLCJ0cCI6IjUwMUZDMTQzMkQ4NzE1NURDNDMxMzgyQUVCODQzRUQ1NThBRDYxQjEiLCJ0cGQiOiJTSEEtMSJ9
//        Hj6Aa4Po8K+8DHpUvDtXbDCDZZR+HtnD2dWqUzZufxnehQKScUn9B3UiQBT6euvmjl/pQax7+gB0kgYtxIfB3XQtEfn+ZZeS9JbFHQ/pbTpOen9vxLSEBFistR6hJBJugXeSJZxDP+vbj1KP2j/vOvx6AAtLhgOpevADJGm7QKvwKBQNGhR6ozzHJPa43LbtoBpDzscFt5C19eyVjEMuNX2pTVINxK5Khw0SVvbNAilr69a/CCpshoI8rd+ewOn7x9mrO3TQ8MLgKa2IY1FdXrXMW4ieDWI3FmkAz470aqPFMWZav2boxM3TVKbi5CeNNlfQRPtAqnPT7lu500Uveg==
        //fhiHQzVsXO//25rK8+uxZBYjchmIX3ZDYnhiDXtRYeRj9cRx2sF7pHd/6tkwsDFwJteuF+PO/q1aSL7Vme5ODLF6gYWY85mzZjwDaT9W6SENkdUPBmnS+7yeAS0xUAicS5++rkeqv6okd7wulTVKglbIZs/QT0K4rP0xEcW0MhbSaPMiWsz6W4nbby5IeOHtnIqfRrT5z0it6gYAzYIdlKFgnbaEhKFCstpNEpvEJcZpABHpV1g/CA4z2AxSZc0H+6zVi0ooWESdJId9IT/f7m2nrfQPjD/Il4pf9tdis/kkebs7lFfrP9CahyA1ohF3UYRUTa8jTNNg6qlc8hT6mw==
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
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(PublicKey key, byte[] ciphertext) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    private static byte[] generateEncryptedData(String data) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] ecrypteddata = (Base64.getEncoder().encode((rsa.doFinal(data.getBytes(StandardCharsets.UTF_8)))));
        return ecrypteddata;
    }

    public static String generateDecryptedData(final byte[] encryptedData) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decodedData = (rsa.doFinal(decodedValue));
        return new String(decodedData);
    }


}
