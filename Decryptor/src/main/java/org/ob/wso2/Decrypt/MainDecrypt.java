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
        byte[] decodeCipher = Base64.getDecoder().decode(encryptedTextKey.getBytes(StandardCharsets.UTF_8));
        JSONObject json = new JSONObject(new String(decodeCipher));
        String cipher = json.getString("c");

        String generateDecryptedData = generateDecryptedData(cipher.getBytes(StandardCharsets.UTF_8));
        System.out.println(generateDecryptedData);
    }

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
