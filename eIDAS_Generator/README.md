# eIDAS Generator
This tool will provide QWAC and QSeal CSRs with minor modifications. Also, if you want, this also implemented to create a self-signed CA, sign CSR and generate eIDAS certificates.

## Prerequisite
- [OpenSSL](https://www.openssl.org/)
- JAVA (JDK 1.8)

## How to Use:
Download the following files first.
- eIDAS_Generator-1.0.jar
- config.properties

Open the `config.properties` file and change the configuration as you wish.

| Config | Description |
| ------ | ------ |
| createCA | True, if you want to create a CA and sign CSR using it  |
| OB_Certs | True, if you want to create OBWAC and OBSEAL  |
| ca_* | CA details to create self-signed certificate |
| organizationName | TPP's Organization Name |
| organizationIdentifier | TPP's Organization ID |
| commonName | Common Name for the eIDAS certificate |
| emailAddress | TPP's email Address |
| countryName | TPP's country |
| PSP_AS | True, if you need Account Service |
| PSP_AI | True, if you need Account Information  |
| PSP_PI | True, if you need Payment Initiation |
| PSP_IC | True, if you need issuing of Card-Based Payment Instruments |

Then, execute the JAR with the `config.properties` file location as below.
```sh
java -jar eIDAS_Generator-1.0.jar [path]/config.properties
```

The `output` directory (avalible in [path] location) will contain following 16 files.

- CA Details (if [createCA] is true) -
    - **ca_cert.pem** -  CA's public certificate as PEM file
    - **ca_cert.srl** -  keeps track of the serial number available for the ca_certificate
    - **ca_certificate.cer** -  CA's public certificate
    - **ca_key.pem** -  CA's Private Key (ENCRYPTED) --> (passphase: wso2carbon)
- QWAC Details -
    - **extensions-qwac.txt** - QWAC extension file
    - **open-ssl-config-qwac.cnf** - QWAC config file
    - **qwac_decrypt.key** - QWAC decrypted private key
    - **qwac.csr** - QWAC CSR file
    - **qwac.key** - QWAC encrpyted private key
    - **qwac.pem** - QWAC public certificate
- QSeal Details -
    - **extensions-qseal.txt** - QSeal extension file
    - **open-ssl-config-qseal.cnf** - Qseal config file
    - **qseal_decrypt.key** - Qseal decrypted private key
    - **qseal.csr** - Qseal CSR file
    - **qseal.key** - Qseal encrpyted private key
    - **qseal.pem** - Qseal public certificate


    
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
## Source Code
QSeal and QWAC configuration and extension file templates are available in resources. Passwords/ Passphrases all are set as “**wso2carbon**”


