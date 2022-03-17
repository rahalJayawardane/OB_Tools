package org.ob.wso2.Decrypt;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;
import javax.crypto.Cipher;

public class MainDecrypt {

    static PrivateKey privateKey = null;
    static PublicKey publicKey = null;

    public static void main(String[] args) throws Exception {

        String password = args[0];
        String file = args[1];
        String encryptedTextKey = args[2];
        getKeysFromKeyStore(file, password);
        byte[] decodeCipher = Base64.getDecoder().decode(encryptedTextKey.getBytes(StandardCharsets.UTF_8));
        JSONObject json = new JSONObject(new String(decodeCipher));
        String cipher = json.getString("c");

        String generateDecryptedData = generateDecryptedData(cipher.getBytes(StandardCharsets.UTF_8));
        System.out.println(generateDecryptedData);
    }

    private static PrivateKey getKeysFromKeyStore(String keyStoreFilePath, String keyStorePassword) throws Exception {

        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
//            BASE64Encoder encoder = new BASE64Encoder();
            keystore.load(new FileInputStream(keyStoreFilePath), keyStorePassword.toCharArray());
            Key key = keystore.getKey(keyStorePassword, keyStorePassword.toCharArray());
            if (key instanceof PrivateKey) {
                Certificate cert = keystore.getCertificate(keyStorePassword);
                publicKey = cert.getPublicKey();
                KeyPair keyPair = new KeyPair(publicKey, (PrivateKey) key);
                privateKey = keyPair.getPrivate();
            }
            //privateKeyEncoded = encoder.encode(privateKey.getEncoded());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return privateKey;
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
