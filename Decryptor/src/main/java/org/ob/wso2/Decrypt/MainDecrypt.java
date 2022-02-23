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

    public static void main(String[] args) throws Exception{
        String password = "wso2carbon"; //args[0];
        String file = "/Users/rahal/Desktop/ARB_Docker/wso2-obam-1.3.0/repository/resources/security/wso2carbon.jks"; // args[1];
        String encryptedTextKey = "eyJjIjoiZmhpSFF6VnNYTy8vMjVySzgrdXhaQllqY2htSVgzWkRZbmhpRFh0UlllUmo5Y1J4MnNGN3BIZC82dGt3c0RGd0p0ZXVGK1BPL3ExYVNMN1ZtZTVPRExGNmdZV1k4NW16Wmp3RGFUOVc2U0VOa2RVUEJtblMrN3llQVMweFVBaWNTNSsrcmtlcXY2b2tkN3d1bFRWS2dsYklacy9RVDBLNHJQMHhFY1cwTWhiU2FQTWlXc3o2VzRuYmJ5NUllT0h0bklxZlJyVDV6MGl0NmdZQXpZSWRsS0ZnbmJhRWhLRkNzdHBORXB2RUpjWnBBQkhwVjFnL0NBNHoyQXhTWmMwSCs2elZpMG9vV0VTZEpJZDlJVC9mN20ybnJmUVBqRC9JbDRwZjl0ZGlzL2trZWJzN2xGZnJQOUNhaHlBMW9oRjNVWVJVVGE4alROTmc2cWxjOGhUNm13XHUwMDNkXHUwMDNkIiwidCI6IlJTQS9FQ0IvT0FFUHdpdGhTSEExYW5kTUdGMVBhZGRpbmciLCJ0cCI6IjUwMUZDMTQzMkQ4NzE1NURDNDMxMzgyQUVCODQzRUQ1NThBRDYxQjEiLCJ0cGQiOiJTSEEtMSJ9"; //  args[2];
//        getKeysFromKeyStore(file, password, password, password);
//        String enc = encryptPlainText("arbUK@123");
//        System.out.println(enc);
//        decrypt(password, file, enc);
        test();

    }

    private static void test() {
        String ssa ="eyJraWQiOiJyZWciLCJ0eXAiOiJKV1QiLCJhbGciOiJQUzI1NiJ9.eyJpc3MiOiJkaW8tcmVnaXN0ZXIiLCJpYXQiOjE2NDUwODcxNjksImV4cCI6MTY3NjYyMzE2OSwianRpIjoieDlod3o3cHhkMXQyIiwib3JnX2lkIjoiYWRyMDEiLCJsZWdhbF9lbnRpdHlfaWQiOiJhZHIwMSIsIm9yZ19uYW1lIjoiQ29tbUJhbmsiLCJjbGllbnRfbmFtZSI6Ik1vbmV5IE1hbmFnZW1lbnQiLCJjbGllbnRfZGVzY3JpcHRpb24iOiJBIG1vY2sgc29mdHdhcmUgcHJvZHVjdCBmb3IgdGVzdGluZyBTU0EgLSBhZHIxMSBhbmQgc3AxOCIsImNsaWVudF91cmkiOiJodHRwczovL3d3dy5tb2NrY29tcGFueS5jb20uYXUiLCJyZWRpcmVjdF91cmlzIjpbImh0dHBzOi8vYml6YS1jZHItdGVzdGluZy5zZi5oaXQuNjkyMjY1Lm5ldC5hdS90ZXN0L2Evdm9za2hvZF9tcC9jYWxsYmFjaz9kdW1teTE9bG9yZW0mZHVtbXkyPWlwc3VtIiwiaHR0cHM6Ly9iaXphLWNkci10ZXN0aW5nLnNmLmhpdC42OTIyNjUubmV0LmF1L3Rlc3QvYS92b3NraG9kX2tkL2NhbGxiYWNrP2R1bW15MT1sb3JlbSZkdW1teTI9aXBzdW0iLCJodHRwczovL2JpemEtY2RyLXRlc3Rpbmcuc2YuaGl0LjY5MjI2NS5uZXQuYXUvdGVzdC9hL3Zvc2tob2RfdGcvY2FsbGJhY2siLCJodHRwczovL2JpemEtcHJhay5zZi5oaXQuNjkyMjY1Lm5ldC5hdS92b3NraG9kLmNvbmZvcm1hbmNlLjEvcmV0dXJuIiwiaHR0cHM6Ly9iaXphLWNkci10ZXN0aW5nLnNmLmhpdC42OTIyNjUubmV0LmF1L3Rlc3QvYS92b3NraG9kX2JrL2NhbGxiYWNrP2R1bW15MT1sb3JlbSZkdW1teTI9aXBzdW0iLCJodHRwczovL2JpemEtY2RyLXRlc3Rpbmcuc2YuaGl0LjY5MjI2NS5uZXQuYXUvdGVzdC9hL3Zvc2tob2RfZ2MvY2FsbGJhY2s_ZHVtbXkxPWxvcmVtJmR1bW15Mj1pcHN1bSIsImh0dHBzOi8vYml6YS1jZHItdGVzdGluZy5zZi5oaXQuNjkyMjY1Lm5ldC5hdS90ZXN0L2Evdm9za2hvZF90Zy9jYWxsYmFjaz9kdW1teTE9bG9yZW0mZHVtbXkyPWlwc3VtIiwiaHR0cHM6Ly9iaXphLWNkci10ZXN0aW5nLnNmLmhpdC42OTIyNjUubmV0LmF1L3Rlc3QvYS92b3NraG9kX2tkL2NhbGxiYWNrIiwiaHR0cHM6Ly9sb2NhbGhvc3QuZW1vYml4LmNvLnVrOjg0NDMvdGVzdC9hL3Zvc2tob2QvY2FsbGJhY2s_ZHVtbXkxPWxvcmVtJmR1bW15Mj1pcHN1bSIsImh0dHBzOi8vYml6YS1jZHItdGVzdGluZy5zZi5oaXQuNjkyMjY1Lm5ldC5hdS90ZXN0L2Evdm9za2hvZF9tcC9jYWxsYmFjayIsImh0dHBzOi8vYml6YS1jZHItdGVzdGluZy5zZi5oaXQuNjkyMjY1Lm5ldC5hdS90ZXN0L2Evdm9za2hvZF9zbC9jYWxsYmFjaz9kdW1teTE9bG9yZW0mZHVtbXkyPWlwc3VtIiwiaHR0cHM6Ly9iaXphLWNkci10ZXN0aW5nLnNmLmhpdC42OTIyNjUubmV0LmF1L3Rlc3QvYS92b3NraG9kX2JrL2NhbGxiYWNrIiwiaHR0cHM6Ly9iaXphLWNkci10ZXN0aW5nLnNmLmhpdC42OTIyNjUubmV0LmF1L3Rlc3QvYS92b3NraG9kX2djL2NhbGxiYWNrIiwiaHR0cHM6Ly9iaXphLWNkci10ZXN0aW5nLnNmLmhpdC42OTIyNjUubmV0LmF1L3Rlc3QvYS92b3NraG9kX3NsL2NhbGxiYWNrIiwiaHR0cHM6Ly9sb2NhbGhvc3QuZW1vYml4LmNvLnVrOjg0NDMvdGVzdC9hL3Zvc2tob2QvY2FsbGJhY2siLCJodHRwczovL2JpemEtY2RyLXRlc3Rpbmcuc2YuaGl0LjY5MjI2NS5uZXQuYXUvdGVzdC9hL3Zvc2tob2QvY2FsbGJhY2siLCJodHRwczovL2JpemEtY2RyLXRlc3Rpbmcuc2YuaGl0LjY5MjI2NS5uZXQuYXUvdGVzdC9hL3Zvc2tob2QvY2FsbGJhY2s_ZHVtbXkxPWxvcmVtJmR1bW15Mj1pcHN1bSJdLCJsb2dvX3VyaSI6Imh0dHBzOi8vd3d3Lm1vY2tjb21wYW55LmNvbS5hdS9sb2dvcy9sb2dvMS5wbmciLCJqd2tzX3VyaSI6Imh0dHA6Ly9vYmFwaTozMDAwL2Fkci9qd2tzL3NwMDEiLCJyZXZvY2F0aW9uX3VyaSI6Imh0dHA6Ly9vcGVuYmFua2luZy1wcmU6ODQ0My9pZHAvLndlbGwta25vd24vb3BlbmlkLWNvbmZpZ3VyYXRpb24vYWRycmV2b2tlZGp3a3MiLCJyZWNpcGllbnRfYmFzZV91cmkiOiJodHRwczovL2RlOTUtMTEyLTEzNC0yMjAtMjM1Lm5ncm9rLmlvIiwic29mdHdhcmVfaWQiOiJzcDAxIiwicG9saWN5X3VyaSI6Imh0dHBzOi8vd3d3Lm1vY2tjb21wYW55LmNvbS5hdSIsInRvc191cmkiOiJodHRwczovL3d3dy5tb2NrY29tcGFueS5jb20uYXUiLCJzb2Z0d2FyZV9yb2xlcyI6ImRhdGEtcmVjaXBpZW50LXNvZnR3YXJlLXByb2R1Y3QiLCJzY29wZSI6ImJhbms6YWNjb3VudHMuYmFzaWM6cmVhZCBiYW5rOmFjY291bnRzLmRldGFpbDpyZWFkIGJhbms6dHJhbnNhY3Rpb25zOnJlYWQgYmFuazpwYXllZXM6cmVhZCBjb21tb246Y3VzdG9tZXIuZGV0YWlsOnJlYWQgY29tbW9uOmN1c3RvbWVyLmJhc2ljOnJlYWQgYmFuazpyZWd1bGFyX3BheW1lbnRzOnJlYWQgY2RyOnJlZ2lzdHJhdGlvbiBvcGVuaWQgcHJvZmlsZSJ9.kr_ov-P-0bzX2zE4Oin9aKh9iykdCGN3QYjHJmCYSCGCkgeWEmPwXwrAWxik2fKLZGjzpJK8x4QgXFr65scS2QKJwxgnVsZDrviSpL9Rnl5bGrFcC1OlCrV3JBvld26iahsQK_Z_MhM1x4PAjX7fIOlNC1BUTgbeLfH8DSyrDboPMRl34iCm7qS5f29cbw-VP_P_tSC5ZwE8zTQG05lJXV8mJGYUtRDOOExh4yC6oMFovMnT1NJOqQOMPLwY8UOBRLI8aHS_r2EMgGCL05G77O9JKYYPPIApv_qlktXtUz24vz9-ZupIQydXJkG2SzO4S198nTNECUWQESFhH7VAOg";
        String ssaEncode = ssa.split("\\.")[1];
        System.out.println(ssaEncode);
        ssaEncode = ssaEncode.replace("_", "/").replace("-", "+");
        System.out.println(ssaEncode);
        byte[] body = Base64.getDecoder().decode(ssaEncode);
        JSONObject jsonSSA = new JSONObject(new String(body, StandardCharsets.UTF_8));
        String adrDetails = jsonSSA.getString("org_name") + "," + jsonSSA.getString("client_name");
        System.out.println(adrDetails);

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
    static PrivateKey privateKey = null;
    static PublicKey publicKey = null;
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
//    public static String encryptPlainText(String plainText) throws Exception {
//        Security.addProvider(new BouncyCastleProvider());
//        String cipherTransformation = "RSA/ECB/OAEPwithSHA1andMGF1Padding";
//        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding","BC");
//        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//        byte[] encryptedByte = cipher.doFinal(plainText.getBytes());
////        return Base64.encode(encryptedByte);
//        return null;
////
////        try {
////            byte[] encryptedKey = cipher.doFinal((plainText.getBytes()));
////            return Base64.encode(encryptedKey);
////        } catch (GeneralSecurityException e) {
////            String errMsg = "Failed to generate the cipher text";
////            throw new Exception(errMsg, e);
////        } catch (ArrayIndexOutOfBoundsException e) {
////            System.out.println("Error while adding the password - too much data for RSA block");
////            throw e;
////        }
//    }


}
