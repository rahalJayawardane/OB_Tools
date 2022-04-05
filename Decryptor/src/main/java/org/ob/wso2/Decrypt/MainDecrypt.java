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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;

public class MainDecrypt {

    static PrivateKey privateKey = null;
    static PublicKey publicKey = null;

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            System.out.println("ERROR: Invalid number of inputs. Please refer to the README.md file");
            System.exit(0);
        }

        String file = args[0];
        String password = args[1];
        String encryptedTextKey = args[2];

        getKeysFromKeyStore(file, password);
        if (checkForEncode(encryptedTextKey)) {
            encryptedTextKey = decode64(encryptedTextKey);
        }
        System.out.println(new String(generateEncryptedData("wso2carbon")));
        String generateDecryptedData = generateDecryptedData(encryptedTextKey.getBytes(StandardCharsets.UTF_8));
        System.out.println("Decrypted Value: " + generateDecryptedData);
    }

    private static String decode64(String encryptedTextKey) throws Exception{

        byte[] decodeCipher = Base64.getDecoder().decode(encryptedTextKey.getBytes(StandardCharsets.UTF_8));
        try {
            JSONObject json = new JSONObject(new String(decodeCipher));
            return json.getString("c");
        } catch (Exception e) {
            throw new Exception("Encrypted value is not in Base64 JSON format. Please check again...");
        }

    }

    public static boolean checkForEncode(String string) {
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(string);
        return m.find();
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
