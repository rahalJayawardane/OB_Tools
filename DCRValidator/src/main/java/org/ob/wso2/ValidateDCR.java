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
import org.ob.wso2.util.CommonUtil;
import org.ob.wso2.util.JWTUtils;
import org.ob.wso2.util.UKErrorResponseUtil;
import org.ob.wso2.util.UKValidationUtil;
import org.ob.wso2.validator.UK320RegistrationRequestValidator;

import java.util.Map;

public class ValidateDCR {

    public static void main(String[] args) {

    }

    public OBRegistrationSetupResponse handleDCRPost(String tlsCertificate, String requestBody) throws Exception{

        if (StringUtils.isBlank(requestBody)) {
            throw new Exception("A valid jwt is not found with the request");
        }

        //decode request jwt
        JSONObject decodedRequest;
        try {
            decodedRequest = JWTUtils.decodeRequestJWT(requestBody, JWTUtils.JwtPart.body);
        } catch (Exception e) {
            System.out.println("Error occurred while decoding the provided jwt: " + e);
            return UKErrorResponseUtil.getInvalidJWTResponse();
        }

        //map registrationRequest to UK320ClientRegistrationRequest model
        UK320ClientRegistrationRequest registrationRequest;
        Gson gson = new GsonBuilder().create();
        try {
            registrationRequest = gson.fromJson(decodedRequest.toString(), UK320ClientRegistrationRequest.class);
            if (registrationRequest == null) {
                System.out.println("Request payload cannot be mapped to the specification supported format");
                return UKErrorResponseUtil.getMalformedRequestBodyResponse();
            }
        } catch (Exception e) {
            System.out.println(String.format("Error occurred while mapping the provided jwt"));
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
            System.out.println(String.format("Error occurred while decoding the provided SSA : %s", e));
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
                System.out.println("SSA cannot be mapped to the specification supported format");
                return UKErrorResponseUtil.getInvalidSSAResponse();
            }
        } catch (Exception e) {
            System.out.println(String.format("Error occurred while mapping the provided software statement : %s", e));
            return UKErrorResponseUtil.getMalformedRequestBodyResponse();
        }

        //tpp sent certificate validation
        boolean isValidCert;
        isValidCert = UKValidationUtil.validateTlsCertificate(tlsCertificate,
                softwareStatementBody.getSoftwareJwksEndpoint());
        if (!isValidCert) {
            System.out.println("Failed to validate the TLS certificate");
            return UKErrorResponseUtil.getInvalidTlsCertResponse();
        }

        //parameter validations (including signature and ssa)
        UK320RegistrationError validationError = UK320RegistrationRequestValidator.validateRegistrationPayload(
                requestBody, registrationRequest, softwareStatementHeader, softwareStatementBody);
        if (validationError.getCode() != null) {
            System.out.println(String.format(
                    "%s: %s", ErrorConstants.PAYLOAD_VALIDATION_FAILED, gson.toJson(validationError)));
            String responseString = CommonUtil.getResponseString(validationError);
            return new OBRegistrationSetupResponse(OBRegistrationSetupResponse.ResponseStatusEnum
                    .BAD_REQUEST.getCode(), responseString);
        }
        // TODO: correct reponse
        return null;
    }
}