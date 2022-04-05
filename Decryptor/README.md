# PassPhrase Decryptor
This tool will decrypt the _**encrypted userstore Base64 passwords**_.

Following are **not valid** input passphrases:
* User Passwords (user passwords are kept as HASH values + Salt value [unique value])
* Passwords which are encrypted by Symmetric Key Encryption (ex: Cipher tool)

Make sure the passphrases are in base64 format. 
ex: 
```sh
eyJjIjoiWVRZL1VVbnZQNkJqb21BWGRWRStwbGdObjBpbE8rQkNtbitmMU5CdENMT25lbjFubjIrMm00QStlN1VyWk0yMWFLUTBWSlFKaGNwbDZuMUR1a3JNdjBmUzYwS1NoL2t1cmJPU2cwa2hlZEJuZm5ZMVhYcUtDcEpPM2RwRGt1cHBGNjFrLzg4Ny82QzZXd1p3Z0tsdmtTWXJLaW5MWk5CUVZvSE90TnhhT0k1UzdZQU9pWGR6MUlxYmZMbFZnWWl4VmxveVV5NnpsRENJMkVvdmdsLytNbUFkME5JWGZKeHZDZGhEWUpDMTdVT0lqMjBwa2tHUUFPMmo2REJSVDZDTTBhOTJyT1V6YnlRZkV5NUgrY1BqNE83VVdmWTV1QmxJK3JFVUQ5V3pYMXNsOEtpWUNHbVpYWnRLZE1QOERIUkJkajFZWGNsYzQyd29RdStXS3RCYm53PT0iLCJ0IjoiUlNBL0VDQi9PQUVQd2l0aFNIQTFhbmRNR0YxUGFkZGluZyIsInRwIjoiNTAxRkMxNDMyRDg3MTU1REM0MzEzODJBRUI4NDNFRDU1OEFENjFCMSIsInRwZCI6IlNIQS0xIn0=
```

format:
```json
{
  "c": "YTY/UUnvP6BjomAXdVE+plgNn0ilO+BCmn+f1NBtCLOnen1nn2+2m4A+e7UrZM21aKQ0VJQJhcpl6n1DukrMv0fS60KSh/kurbOSg0khedBnfnY1XXqKCpJO3dpDkuppF61k/887/6C6WwZwgKlvkSYrKinLZNBQVoHOtNxaOI5S7YAOiXdz1IqbfLlVgYixVloyUy6zlDCI2Eovgl/+MmAd0NIXfJxvCdhDYJC17UOIj20pkkGQAO2j6DBRT6CM0a92rOUzbyQfEy5H+cPj4O7UWfY5uBlI+rEUD9WzX1sl8KiYCGmZXZtKdMP8DHRBdj1YXclc42woQu+WKtBbnw==",
  "t": "RSA/ECB/OAEPwithSHA1andMGF1Padding",
  "tp": "501FC1432D87155DC431382AEB843ED558AD61B1",
  "tpd": "SHA-1"
}
```

## Prerequisite
- JAVA (JDK 1.8)

## How to Use:
Download the following JAR and run the command with correct parameters.
- passphrase_decryptor-1.0.jar

```sh
java -jar passphrase_decryptor-1.0.jar <keystore_location> <keystore_password> <encrypted_passphrase>
```

| Parameters | Description |
| ------ | ------ |
| keystore_location | wso2carbon.jks (or the primary/internal) JKS file path  |
| keystore_password | JKS file password  |
| encrypted_passphrase | Encrypted value that you want to decrypt |

_example:_
```sh
java -jar passphrase_decryptor-1.0.jar wso2is-5.7.0/repository/resources/security/wso2carbon.jks wso2carbon eyJjIjoiWVRZL1VVbnZQNkJqb21BWGRWRStwbGdObjBpbE8rQkNtbitmMU5CdENMT25lbjFubjIrMm00QStlN1VyWk0yMWFLUTBWSlFKaGNwbDZuMUR1a3JNdjBmUzYwS1NoL2t1cmJPU2cwa2hlZEJuZm5ZMVhYcUtDcEpPM2RwRGt1cHBGNjFrLzg4Ny82QzZXd1p3Z0tsdmtTWXJLaW5MWk5CUVZvSE90TnhhT0k1UzdZQU9pWGR6MUlxYmZMbFZnWWl4VmxveVV5NnpsRENJMkVvdmdsLytNbUFkME5JWGZKeHZDZGhEWUpDMTdVT0lqMjBwa2tHUUFPMmo2REJSVDZDTTBhOTJyT1V6YnlRZkV5NUgrY1BqNE83VVdmWTV1QmxJK3JFVUQ5V3pYMXNsOEtpWUNHbVpYWnRLZE1QOERIUkJkajFZWGNsYzQyd29RdStXS3RCYm53PT0iLCJ0IjoiUlNBL0VDQi9PQUVQd2l0aFNIQTFhbmRNR0YxUGFkZGluZyIsInRwIjoiNTAxRkMxNDMyRDg3MTU1REM0MzEzODJBRUI4NDNFRDU1OEFENjFCMSIsInRwZCI6IlNIQS0xIn0=
```
_output:_ 
```sh
Decrypted Value: wso2carbon
```
