package org.ob.wso2.tpp;

public class SignCA {
    //if sign by CA is true


    // openssl x509 -req -days 360 -in qwac.csr -CA ../common/cert.pem -CAkey ../common/caKey.pem -CAcreateserial -out qwac.pem -sha256 -extensions v3_ca -extfile extensions-qwac.txt -passin pass:wso2carbon

    // openssl x509 -req -days 360 -in qseal.csr -CA ../common/cert.pem -CAkey ../common/caKey.pem -CAcreateserial -out qseal.pem -sha256 -extensions v3_ca -extfile extensions-qseal.txt -passin pass:wso2carbon

}
