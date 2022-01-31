/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.util;

import com.wso2.finance.open.banking.common.config.uk.UKSpecConfigParser;
import com.wso2.finance.open.banking.common.identity.utils.OBIdentityUtil;
import com.wso2.finance.open.banking.dynamic.client.registration.common.util.CertificateUtil;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.constants.UKValidationConstants;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model.UK320ClientRegistrationRequest;
import com.wso2.finance.open.banking.dynamic.client.registration.mgt.uk.model.UK320SoftwareStatementBody;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UK spec specific validations.
 */
public class UKValidationUtil {
    private static final Log log = LogFactory.getLog(UKValidationUtil.class);

    /**
     * List of allowed scopes in UK specification.
     */
    private static List<String> allowedScopes = new ArrayList<>(Arrays.asList(
            UKValidationConstants.SCOPE_ACCOUNT,
            UKValidationConstants.SCOPE_PAYMENT,
            UKValidationConstants.SCOPE_FUNDSCONFIRMATION,
            UKValidationConstants.SCOPE_OPENID)
    );

    /**
     * Validate grant types.
     *
     * @param grantTypes requested grant types by the data recipient
     */
    public static boolean isValidGrantType(List<String> grantTypes) {

        for (String grantType : grantTypes) {
            if (!(UKValidationConstants.AUTHORIZATION_CODE.equals(grantType) ||
                    UKValidationConstants.CLIENT_CREDENTIALS.equals(grantType) ||
                    UKValidationConstants.REFRESH_TOKEN.equals(grantType))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether the application type is web.
     *
     * @param appType the value for the application type sent in the ssa
     */
    public static boolean isValidApplicationType(String appType) {

        return (UKValidationConstants.APPLICATION_WEB.equalsIgnoreCase(appType) ||
                UKValidationConstants.APPLICATION_MOBILE.equalsIgnoreCase(appType));
    }

    /**
     * Check response type.
     *
     * @param responseTypes the response types requested by the data recipient
     */
    public static boolean isValidResponseType(List<String> responseTypes) {

        for (String responseType : responseTypes) {
            if (!(UKValidationConstants.CODE_ID_TOKEN.equals(responseType))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate the signing algorihms requested by the data recipient.
     *
     * @param algorithm the algorithm used for signing
     */
    public static boolean isValidSigningAlgorithm(String algorithm) {

        return (UKValidationConstants.SIGNING_ALGO_PS256.equals(algorithm) ||
                UKValidationConstants.SIGNING_ALGO_ES256.equals(algorithm));
    }

    /**
     * Validate  the scope requested by tpp is allowed.
     *
     * @param scopes the scopes requested by the tpp
     * @param roles  the roles tpp  is registered for
     */
    public static boolean isValidScope(String scopes, String roles) {
        for (String scope : scopes.split(" ")) {
            if (!allowedScopes.contains(scope)) {
                return false;
            }
            if (UKValidationConstants.SCOPE_ACCOUNT.equals(scope) &&
                    !roles.contains(UKValidationConstants.ROLE_AISP)) {
                return false;
            }
            if (UKValidationConstants.SCOPE_PAYMENT.equals(scope) &&
                    !roles.contains(UKValidationConstants.ROLE_PISP)) {
                return false;
            }
            if (UKValidationConstants.SCOPE_FUNDSCONFIRMATION.equals(scope) &&
                    !roles.contains(UKValidationConstants.ROLE_CBPII)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate the TLS certificate against the jwks in SSA.
     *
     * @param tlsCerificateString - TLS certificate
     * @param jwksEndpoint        - Jwks endpoint
     * @return - boolean
     */
    public static boolean validateTlsCertificate(String tlsCerificateString, String jwksEndpoint) {

        log.debug("Validating the TLS certificate sent by the TPP..");
        //check whether the certificate sent by the tpp is signed by a CA listed in the JWKS end point
        try {
            X509Certificate tlsCertificate = OBIdentityUtil.parseCertificate(tlsCerificateString);
            if (!CertificateUtil.verifyCertChain(jwksEndpoint, tlsCertificate)) {
                log.error("TLS certificate sent by the TPP is not signed by the CA listed in the provided " +
                        "JWKS endpoint");
                return false;
            }
        } catch (CertificateException  e) {
            log.error("Error occurred while validating the TPP sent TLS certificate");
            return false;
        } catch (IOException e) {
            log.error("Error occurred while trying to retrieve the certificate from the jwks endpoint", e);
            return false;
        }
        log.debug("Validated the TLS certificate sent by the TPP successfully");
        return true;
    }

    /**
     * Validate whether the tpp sent authentication method is supported by the SP.
     *
     * @param authMethod the authentication method requested by the tpp
     */
    public static boolean checkAuthMethod(String authMethod) {

        List<String> authMethods = UKSpecConfigParser.getInstance().getAuthenticationMethods();
        for (String auth : authMethods) {
            if (auth.equals(authMethod)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the softwareId in the registration request and SSA are matching if the
     * software id is present in the registration request.
     *
     * @param registrationRequest registration request body
     * @param softwareStatement   body of the SSA
     */
    public static boolean validateSoftareId(UK320ClientRegistrationRequest registrationRequest,
                                            UK320SoftwareStatementBody softwareStatement) {

        if (StringUtils.isEmpty(registrationRequest.getSoftwareId())) {
            return true;
        } else {
            return (registrationRequest.getSoftwareId().equals(softwareStatement.getSoftwareId()));
        }
    }
}
