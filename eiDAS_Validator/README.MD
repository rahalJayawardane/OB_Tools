# eIDAS Validator
This tool validate QWAC or QSeal certificate and extract the content out of it.

## Prerequisite
- JAVA (JDK 1.8)

## How to Use:
Download the following jar.
- eiDAS_Validator-1.0.jar

If you have `cer` or `crt` file, first convert it to `PEM` file using online tool or
openSSL command,

```shell
openssl x509 -inform der -in qwac_certificate.cer -out qwac.pem
```

Then, execute the JAR with the correct `PEM` file location as below. 
```sh
java -jar eiDAS_Validator-1.0.jar [path]/qwac.pem
                     or
java -jar eiDAS_Validator-1.0.jar [path]/qseal.pem
```

The output will be similar as following.
```json
{
  "pspAuthorisationNumber": "PSDGB-OB-testTPP",
  "pspRoles": [
    "ASPSP",
    "PISP",
    "CBPII"
  ],
  "name": "TPP_0015800001HQQrZAAX",
  "ncaName": "Financial Conduct Authority",
  "ncaId": "GB-FCA",
  "notAfter": "Jan 28, 2023 11:50:58 AM",
  "notBefore": "Jan 28, 2022 11:50:58 AM"
}
```
