# DCR Validator
This tool will validate the DCR JWT request and the SSA without setting up a DCR setup.

## Prerequisite
- JAVA (JDK 1.8)
- client-truststore which includes the OBIE sandbox or/and production certificates
- SSA given by OBIE / TPP's JWT request
- Transport certificate
- Sign certificate (optional, if you have the TPP's JWT request)

## How to Use:
Download the following files first.
- DCRValidator-1.0.jar
- config.properties

Open the `config.properties` file and change the configuration as you wish.

| Config | Description |
| ------ | ------ |
| TPP_JWT | TPP's JWT request (_If you do not have the TTP JWT request, to create that please refer to the https://ob.docs.wso2.com/en/latest/get-started/dynamic-client-registation/#step-3-register-an-application. <br /> Use the Sign certificate to sign the object_)|
| TPP_TLS | Transport certificate (PEM) location If you have `cer` or `crt` file, first convert it to `PEM` file using online tool or openSSL command, <br /> ```openssl x509 -inform der -in transport.cer -out transport.pem```  |
| client_trustStore | client-truststore (which has the OBIE certificate) location |
| trustStore_password | client-truststore passphase |
| ConnectionTimeout | JWKS endpoints connection timeout (default: 300s [5m]) |
| ReadTimeout | JWKS endpoints read timeout (default: 300s [5m])  |
| DCRJwksUrlSandbox | OBIE Sandbox JWKS URL |
| DCRJwksUrlProduction | OBIE Production JWKS URL |
| ValidateURIHostName | Validate the hostnames of logoURI, clientURI, policyURI, termOfServiceURI with redirect URL hostname  |
| ValidateURI | Connection validation of logoURI, clientURI, policyURI, termOfServiceURIs |
| TokenAuthenticationMethods | "_token_endpoint_auth_method_" in JWT object <br> - private_key_jwt <br> - client_secret_jwt |

Then, execute the JAR with the `config.properties` file location as below.
```sh
java -jar DCRValidator-1.0.jar [path]/config.properties
```

## Outputs:
The Success output will be similar as following.
```json
{
  "httpStatusCode": 200,
  "entity": "JWT Request with SSA is valid"
}
```

### - Errors:
| Error Message | Description |
| ------ | ------ |
| A valid jwt is not found in the config file | TPP_JWT is empty |
| Error occurred while decoding the provided jwt | Decode request JWT failed (not a valid JWT) |
| Request payload cannot be mapped to the specification supported format | JWT is not in specification supported format |
| Error occurred while mapping the provided jwt | Error when mapping the JWT object with Class |
| Mandatory parameter [PARAM] is null/ empty | [PARAM] is null or empty in your JWT request |
| Error occurred while decoding the provided SSA | SSA decode failed  |
| SSA cannot be mapped to the specification supported format | SSA header or body is null |
| Error occurred while mapping the provided software statement | Error when mapping the SSA with Class  |
| Malformed jwks uri | JWKS URL is malformed |
| A valid jwkSet could not be found in the given jwks url | JWKS endpoint does not have a key set |
| Error while loading the jwk set | Connection issue (HTTPS or network) |
| TLS certificate sent by the TPP is not signed by the CA listed in the provided JWKS endpoint | TPP_TLS certificate not signed by OBIE |
| Error occurred while validating the TPP sent TLS certificate | Certificate issue (refer to the stacktrace) |
| Error occurred while trying to retrieve the certificate from the jwks endpoint | Connection issue (HTTPS or network) |
| Failed to validate the TLS certificate | TPP_TLS certificate validation failed |
| Provided SSA is revoked | SSA revoked. (Validated with revoke endpoint) |
| Request jwt signature does not match with the SSA | JWT request signature validation failed. |
| Failed to validate SSA signature | SSA signature validation failed |
| ISS claim of the request JWT does not match with the SSA's software ID | ISS not identical with SSA |
| Invalid software_id found in the request | Software ID is not identical or null |
| Invalid token_endpoint_auth_method found in the request | token_endpoint_auth_method is not identical or null |
| Provided redirect uris does not match with the SSA | redirect_uri are not identical |
| Invalid redirect_uris found in the SSA | redirect_uri are not in "https" or having "localhost" |
| Host names of logo_uri/tos_uri/policy_uri/client_uri does not match with the redirect_uris | Hostnames are not identical with redirect hostname |
| Provided logo_uri/client_uri/policy_uri/tos_uri in the request does not resolve to a valid web page | URIs are not having successful connection |
| Invalid application type [PARAM] found in the request. | Application type is not "web" or "mobile" |
| Invalid grant types found in the request | Grant types are "authorization_code", "client_credentials" or "refresh_token" |
| Invalid response types found in the request | Response type must be set as "code id_token" |
| Invalid request object signing algorithm found in the request | Signing algorithm should be "PS256" or "ES256" |
| Invalid scopes found in the request | Scopes can be "accounts", "payments", "fundsconfirmations", "openid" |