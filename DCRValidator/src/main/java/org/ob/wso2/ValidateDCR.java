package org.ob.wso2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.ob.wso2.constants.ErrorConstants;
import org.ob.wso2.model.OBRegistrationSetupResponse;
import org.ob.wso2.model.UK320ClientRegistrationRequest;
import org.ob.wso2.model.UK320RegistrationError;
import org.ob.wso2.model.UK320SoftwareStatementBody;
import org.ob.wso2.model.UK320SoftwareStatementHeader;
import org.ob.wso2.util.CommonParser;
import org.ob.wso2.util.CommonUtil;
import org.ob.wso2.util.JWTUtils;
import org.ob.wso2.util.UKErrorResponseUtil;
import org.ob.wso2.util.UKValidationUtil;
import org.ob.wso2.validator.UK320RegistrationRequestValidator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ValidateDCR {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Please input config file location");
            System.exit(0);
        }

        String configFile = args[0];

        CommonParser parser = CommonParser.getInstance();
        parser.readConfigFile(configFile);
        System.setProperty("javax.net.ssl.trustStore", parser.getTrustStore());
        System.setProperty("javax.net.ssl.trustStorePassword", parser.getTrustStorePassword());
        OBRegistrationSetupResponse response = handleDCRPost(readFile(parser.getTppTLS()), parser.getTppJWT());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

    }

    private static OBRegistrationSetupResponse handleDCRPost(String tlsCertificate, String requestBody) throws Exception{

        if (StringUtils.isBlank(requestBody)) {
            throw new Exception("ERROR: A valid jwt is not found in the config file");
        }

        //decode request jwt
        JSONObject decodedRequest;
        try {
            decodedRequest = JWTUtils.decodeRequestJWT(requestBody, JWTUtils.JwtPart.body);
        } catch (Exception e) {
            System.out.println("ERROR: Error occurred while decoding the provided jwt: " + e);
            return UKErrorResponseUtil.getInvalidJWTResponse();
        }

        //map registrationRequest to UK320ClientRegistrationRequest model
        UK320ClientRegistrationRequest registrationRequest;
        Gson gson = new GsonBuilder().create();
        try {
            registrationRequest = gson.fromJson(decodedRequest.toString(), UK320ClientRegistrationRequest.class);
            if (registrationRequest == null) {
                System.out.println("ERROR: Request payload cannot be mapped to the specification supported format");
                return UKErrorResponseUtil.getMalformedRequestBodyResponse();
            }
        } catch (Exception e) {
            System.out.println(String.format("ERROR: Error occurred while mapping the provided jwt"));
            return UKErrorResponseUtil.getMalformedRequestBodyResponse();
        }

        //null/empty checks for mandatory parameters
        try {
            UK320RegistrationRequestValidator.validateMandatoryParameters(registrationRequest);
        } catch (Exception e) {
            UK320RegistrationError registrationError = UKErrorResponseUtil
                    .getInvalidClientMetadataError(e.getMessage());
            String responseString = CommonUtil.getResponseString(registrationError);
            System.out.println(String.format("%s: %s", ErrorConstants.PAYLOAD_VALIDATION_FAILED, responseString));
            return new OBRegistrationSetupResponse(OBRegistrationSetupResponse.ResponseStatusEnum.BAD_REQUEST.getCode(),
                    responseString);
        }

        //decode SSA
        JSONObject decodedSoftwareStatementBody;
        JSONObject decodedSoftwareStatementHeader;
        try {
            decodedSoftwareStatementBody = JWTUtils.decodeRequestJWT(registrationRequest.
                    getSoftwareStatementPayload(), JWTUtils.JwtPart.body);
            decodedSoftwareStatementHeader = JWTUtils.decodeRequestJWT(registrationRequest.
                    getSoftwareStatementPayload(), JWTUtils.JwtPart.header);
        } catch (Exception e) {
            System.out.println(String.format("ERROR: Error occurred while decoding the provided SSA : %s", e));
            return UKErrorResponseUtil.getInvalidSSAResponse();
        }

        //map SSA header/body to the UK320SoftwareStatementHeader/UK320SoftwareStatementBody models
        UK320SoftwareStatementHeader softwareStatementHeader;
        UK320SoftwareStatementBody softwareStatementBody;
        try {
            softwareStatementHeader = gson.fromJson(decodedSoftwareStatementHeader.toString(),
                    UK320SoftwareStatementHeader.class);
            softwareStatementBody = gson.fromJson(decodedSoftwareStatementBody.toString()
                    , UK320SoftwareStatementBody.class);
            if (softwareStatementBody == null || softwareStatementHeader == null) {
                System.out.println("ERROR: SSA cannot be mapped to the specification supported format");
                return UKErrorResponseUtil.getInvalidSSAResponse();
            }
        } catch (Exception e) {
            System.out.println(String.format("ERROR: Error occurred while mapping the provided software statement : %s", e));
            return UKErrorResponseUtil.getMalformedRequestBodyResponse();
        }

        //tpp sent certificate validation
        boolean isValidCert;
        isValidCert = UKValidationUtil.validateTlsCertificate(tlsCertificate,
                softwareStatementBody.getSoftwareJwksEndpoint());
        if (!isValidCert) {
            System.out.println("ERROR: Failed to validate the TLS certificate");
            return UKErrorResponseUtil.getInvalidTlsCertResponse();
        }

        //parameter validations (including signature and ssa)
        UK320RegistrationError validationError = UK320RegistrationRequestValidator.validateRegistrationPayload(
                requestBody, registrationRequest, softwareStatementHeader, softwareStatementBody);
        if (validationError.getCode() != null) {
            System.out.println("SSA validation failed");
            System.out.println(String.format(
                    "%s: %s", ErrorConstants.PAYLOAD_VALIDATION_FAILED, gson.toJson(validationError)));
            String responseString = CommonUtil.getResponseString(validationError);
            return new OBRegistrationSetupResponse(OBRegistrationSetupResponse.ResponseStatusEnum
                    .BAD_REQUEST.getCode(), responseString);
        }

        return new OBRegistrationSetupResponse(OBRegistrationSetupResponse.ResponseStatusEnum.OK.getCode(),
                "JWT Request with SSA is valid");
    }

    public static String readFile(String fileName) throws Exception {

        InputStream inputStream = new FileInputStream(fileName);

        if (inputStream != null) {
            BufferedInputStream bufferInput = new BufferedInputStream(inputStream);
            ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();
            for (int result = bufferInput.read(); result != -1; result = bufferInput.read()) {
                bufferOutput.write((byte) result);
            }
            return bufferOutput.toString("UTF-8");
        } else {
            throw new FileNotFoundException(fileName + " not found in the classpath");
        }
    }
}
