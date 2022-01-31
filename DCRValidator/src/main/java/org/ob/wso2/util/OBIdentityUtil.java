/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under this
 * license, please see the license as well as any agreement you’ve entered into
 * with WSO2 governing the purchase of this software and any associated services.
 */

package com.wso2.finance.open.banking.common.identity.utils;

import com.nimbusds.jwt.SignedJWT;
import com.wso2.finance.open.banking.common.config.CommonConfigParser;
import com.wso2.finance.open.banking.common.exception.OpenBankingException;
import com.wso2.finance.open.banking.common.identity.IdentityConstants;
import com.wso2.finance.open.banking.common.identity.ServiceProviderRetiriever;
import com.wso2.finance.open.banking.common.identity.retirevers.sp.ServiceProviderRetrieverFactory;
import com.wso2.finance.open.banking.common.parser.ClientTransportCertParser;
import com.wso2.finance.open.banking.common.parser.ClientTransportCertParserImpl;
import com.wso2.finance.open.banking.common.util.CommonConstants;
import com.wso2.finance.open.banking.common.util.client.registration.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.transport.passthru.util.RelayUtils;
import org.wso2.carbon.apimgt.gateway.handlers.security.AuthenticationContext;
import org.wso2.carbon.identity.application.common.model.FederatedAuthenticatorConfig;
import org.wso2.carbon.identity.application.common.model.IdentityProvider;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.application.common.util.IdentityApplicationConstants;
import org.wso2.carbon.identity.application.common.util.IdentityApplicationManagementUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.oauth.common.OAuth2ErrorCodes;
import org.wso2.carbon.identity.oauth.stub.OAuthAdminServiceIdentityOAuthAdminException;
import org.wso2.carbon.identity.oauth.stub.OAuthAdminServiceStub;
import org.wso2.carbon.identity.oauth.stub.dto.OAuthConsumerAppDTO;
import org.wso2.carbon.identity.oauth2.RequestObjectException;
import org.wso2.carbon.idp.mgt.IdentityProviderManagementException;
import org.wso2.carbon.idp.mgt.IdentityProviderManager;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Common Utilities.
 */
public class OBIdentityUtil {

    private static final Log log = LogFactory.getLog(OBIdentityUtil.class);
    private static final String OIDC_IDP_ENTITY_ID = "IdPEntityId";
    private static final String OAUTH2_TOKEN_EP_URL = "OAuth2TokenEPUrl";
    private static final String OIDC_ID_TOKEN_ISSUER_ID = "OAuth.OpenIDConnect.IDTokenIssuerID";
    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT = "-----END CERTIFICATE-----";
    private static final String X509 = "X.509";


    /**
     * Return Certificate for give Certificate Content.
     *
     * @param content Certificate Content
     * @return X509Certificate
     * @throws CertificateException
     */
    public static X509Certificate parseCertificate(String content) throws CertificateException {

        // Trim extra spaces
        String decodedContent = content.trim();

        // Remove Certificate Headers
        byte[] decoded = Base64.getDecoder().decode(decodedContent
                        .replaceAll(IdentityConstants.BEGIN_CERT, StringUtils.EMPTY)
                        .replaceAll(IdentityConstants.END_CERT, StringUtils.EMPTY).trim()
                                                   );

        return (java.security.cert.X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(decoded));
    }

    /**
     * Parse the client transport certificate sent as a header from the Load Balancer when MTLS is terminated at Load
     * Balancer.
     *
     * @param content PEM encoded client transport cert string
     * @return X509Certificate instance of the client transport string
     * @throws CertificateException when an error occurs while parsing the certificate
     */
    public static X509Certificate parseCertificateFromHeader(String content) throws CertificateException {

        // Trim extra spaces
        String decodedContent = content.trim();
        X509Certificate clientCertificate;

        ClientTransportCertParser clientCertParser;
        String clientCertParserImplClass = CommonConfigParser.getInstance().getClientTransportCertParserImplClass();
        if (clientCertParserImplClass != null) {
            try {
                clientCertParser = (ClientTransportCertParser) Class.forName(clientCertParserImplClass).newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new CertificateException("Error occurred while reading the client certificate parser " +
                        "implementation", e);
            }
        } else {
            // Use the default client certificate parser class implementation.
            clientCertParser = new ClientTransportCertParserImpl();
        }

        try {
            clientCertificate = Utils.convert(clientCertParser.parse(decodedContent));
        } catch (OpenBankingException e) {
            throw new CertificateException("Error occurred while parsing the client certificate from header", e);
        }
        return clientCertificate;
    }

    /**
     * Build ServiceProvider application name from authentication context.
     *
     * @param authContext authentication context
     * @return application name string
     */
    public static Optional<String> buildApplicationNameFromAuthContext(AuthenticationContext authContext,
                                                                       MessageContext messageContext) {

        if (authContext == null) {
            try {

                String clientId = getClientIdFromTokenRequest(messageContext);
                return Optional.of(getOAuthConsumerAppDTO(clientId).getApplicationName());

            } catch (OpenBankingException e) {
                log.error("Error while building the message context of token request.", e);
            }
            return Optional.empty();
        }

        String subscriberName = authContext.getSubscriber();
        String userStoreDomain = UserCoreUtil.extractDomainFromName(subscriberName);
        String subscriberNameWithoutDomain = UserCoreUtil.removeDomainFromName(subscriberName);
        subscriberNameWithoutDomain = removeTenantDomain(subscriberNameWithoutDomain);
        String userStoreFormattedSubscriberName =
                CommonConstants.PRIMARY_DOMAIN_NAME.equals(userStoreDomain) ||
                        StringUtils.isEmpty(userStoreDomain) ? subscriberNameWithoutDomain :
                        userStoreDomain + "_" + subscriberNameWithoutDomain;

        return Optional.of(userStoreFormattedSubscriberName
                .replace("@", "-AT-")
                .concat("_")
                .concat(authContext.getApplicationName())
                .concat("_")
                .concat(authContext.getKeyType()));
    }

    /**
     * Build ServiceProvider common oAuthConsumerAppDTO from client ID.
     *
     * @param clientId string
     * @return oAuthConsumerAppDTO object
     */
    public static OAuthConsumerAppDTO getOAuthConsumerAppDTO(String clientId) {

        if (clientId != null) {
            try {
                ServiceProviderRetiriever serviceProviderRetrieverInstance =
                        new ServiceProviderRetrieverFactory().getServiceProviderRetriever();
                OAuthAdminServiceStub oAuthAdminServiceStub =
                        serviceProviderRetrieverInstance.getOauthAdminServiceStub();
                if (oAuthAdminServiceStub != null) {
                    OAuthConsumerAppDTO oAuthConsumerAppDTO =
                            oAuthAdminServiceStub.getOAuthApplicationData(clientId);
                    if (oAuthConsumerAppDTO != null) {
                        return oAuthConsumerAppDTO;
                    }
                }
            } catch (OAuthAdminServiceIdentityOAuthAdminException e) {
                log.error("Error while trying to retrieve OauthAdminService.", e);
            } catch (OpenBankingException e) {
                log.error("Error while building the message context of token request.", e);
            } catch (RemoteException e) {
                log.error("Error while retrieving application data from OauthAdminStub.", e);
            }
            return null;
        }
        return null;
    }

    /**
     * Remove only the tenant domain from a given name.
     *
     * @param name name including the tenant domain
     * @return name without the tenant domain
     */
    public static String removeTenantDomain(String name) {

        int index;

        if (name != null) {
            if (MultitenantUtils.isEmailUserName() && name.indexOf(UserCoreConstants.TENANT_DOMAIN_COMBINER) ==
                    name.lastIndexOf(UserCoreConstants.TENANT_DOMAIN_COMBINER)) {
                return name;
            } else {
                if ((index = name.lastIndexOf(UserCoreConstants.TENANT_DOMAIN_COMBINER)) >= 0) {
                    name = name.substring(0, index);
                }
            }
        }
        return name;
    }

    /**
     * Remove both user store domain and tenant domain from the name
     *
     * @param name name with user store domain and tenant domain
     * @return name without user store domain and tenant domain
     */
    public static String removeUserStoreDomainAndTenantDomain(String name) {

        return removeTenantDomain(UserCoreUtil.removeDomainFromName(name));
    }

    /**
     * Retrieve client id from client assertion in token request
     *
     * @param messageContext
     * @return clientID
     * @throws OpenBankingException
     */
    public static String getClientIdFromTokenRequest(MessageContext messageContext) throws OpenBankingException {

        String clientId = "";

        org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) messageContext).
                getAxis2MessageContext();

        if (((String) messageContext.getProperty(IdentityConstants.REST_API_CONTEXT))
                .equalsIgnoreCase(IdentityConstants.TOKEN_ENDPOINT)) {
            try {
                RelayUtils.buildMessage(axis2MC);
            } catch (IOException | XMLStreamException e) {
                throw new OpenBankingException("Unable to build message", e);
            }
        }

        if (messageContext.getEnvelope().getBody().getFirstElement() != null) {
            if (messageContext.getEnvelope().getBody().getFirstElement().getFirstChildWithName(
                    new QName(null, CommonConstants.CLIENT_ID)) != null) {
                clientId = messageContext.getEnvelope().getBody().getFirstElement().getFirstChildWithName(
                        new QName(null, CommonConstants.CLIENT_ID)).getText();
            } else if (messageContext.getEnvelope().getBody().getFirstElement().getFirstChildWithName(
                    new QName(null, IdentityConstants.CLIENT_ASSSERTION)) != null) {
                try {
                    clientId = SignedJWT.parse(messageContext.getEnvelope().getBody().getFirstElement()
                            .getFirstChildWithName(new QName(null,
                                    IdentityConstants.CLIENT_ASSSERTION))
                            .getText()).getJWTClaimsSet().getIssuer();
                } catch (ParseException e) {
                    log.error("Error occurred while parsing the signed message", e);
                }
            } else {
                org.apache.axis2.context.MessageContext ctx = ((Axis2MessageContext) messageContext)
                        .getAxis2MessageContext();
                Map headers = (Map) ctx.getProperty(IdentityConstants.TRANSPORT_HEADERS);
                if (headers != null) {
                    if (headers.containsKey(IdentityConstants.AUTHORIZATION)) {
                        if (((String) (headers.get(IdentityConstants.AUTHORIZATION)))
                                .split(" ").length > 1) {
                            String authHeader = ((String) (headers.get(IdentityConstants.AUTHORIZATION)))
                                    .split(" ")[1];
                            clientId = new String(Base64.getDecoder().decode(authHeader), StandardCharsets.UTF_8)
                                    .split(":")[0];
                        }
                    }
                }
            }
        }
        return clientId;
    }

    /**
     * Check whether token request is invoked during the DCR call.
     *
     * @param messageContext
     * @param clientId
     * @return
     */
    public static boolean isTokenRequestFromDCR(MessageContext messageContext, String clientId)
            throws OpenBankingException {

        boolean isTokenRequestFromDCR = false;
        String grantType = "";
        String clientSecret = "";

        if (messageContext.getEnvelope().getBody().getFirstElement() != null) {
            if (messageContext.getEnvelope().getBody().getFirstElement().getFirstChildWithName(
                    new QName(null, CommonConstants.GRANT_TYPE)) != null) {
                grantType = messageContext.getEnvelope().getBody().getFirstElement().getFirstChildWithName(
                        new QName(null, CommonConstants.GRANT_TYPE)).getText();
            }
            if (messageContext.getEnvelope().getBody().getFirstElement().getFirstChildWithName(
                    new QName(null, IdentityConstants.CLIENT_SECRET)) != null) {
                clientSecret = messageContext.getEnvelope().getBody().getFirstElement().getFirstChildWithName(
                        new QName(null, IdentityConstants.CLIENT_SECRET)).getText();
            }
        }

        if (IdentityConstants.CLIENT_CREDENTIALS.equals(grantType) && !StringUtils.isEmpty(clientSecret)) {
            // thus set isTokenRequestFromDCR to true
            isTokenRequestFromDCR = true;
            if (log.isDebugEnabled()) {
                log.debug(String.format("Token request for client_id : %s ,uses client_credentials " +
                        "(client_id and client secret).", clientId));
            }
        }

        return isTokenRequestFromDCR;
    }

    /**
     * Return the alias of the resident IDP (issuer of Authorization Server), PAR Endpoint and Token Endpoint
     * to validate the audience value of the PAR Request Object.
     *
     * @param tenantDomain
     * @return issuer, Token Endpoint of the Issuer and PAR Endpoint
     * @throws RequestObjectException
     */
    public static List<String> getAllowedPARAudienceValues(String tenantDomain) throws RequestObjectException {

        List<String> validAudUrls = new ArrayList<>();
        String residentIdpAlias = org.apache.commons.lang.StringUtils.EMPTY;
        IdentityProvider residentIdP;
        try {
            residentIdP = IdentityProviderManager.getInstance().getResidentIdP(tenantDomain);
            FederatedAuthenticatorConfig oidcFedAuthn = IdentityApplicationManagementUtil
                    .getFederatedAuthenticator(residentIdP.getFederatedAuthenticatorConfigs(),
                            IdentityApplicationConstants.Authenticator.OIDC.NAME);

            Property idPEntityIdProperty =
                    IdentityApplicationManagementUtil.getProperty(oidcFedAuthn.getProperties(), OIDC_IDP_ENTITY_ID);
            if (idPEntityIdProperty != null) {
                residentIdpAlias = idPEntityIdProperty.getValue();
                if (log.isDebugEnabled()) {
                    log.debug("Found IdPEntityID: " + residentIdpAlias + " for tenantDomain: " + tenantDomain);
                }
            }

            Property oAuth2TokenEPUrlProperty =
                    IdentityApplicationManagementUtil.getProperty(oidcFedAuthn.getProperties(), OAUTH2_TOKEN_EP_URL);
            if (oAuth2TokenEPUrlProperty != null) {
                // add Token EP Url as a valid "aud" value
                validAudUrls.add(oAuth2TokenEPUrlProperty.getValue());
                if (log.isDebugEnabled()) {
                    log.debug("Found OAuth2TokenEPUrl: " + oAuth2TokenEPUrlProperty.getValue() +
                            " for tenantDomain: " + tenantDomain);
                }
            }
        } catch (IdentityProviderManagementException e) {
            log.error("Error while loading OAuth2TokenEPUrl of the resident IDP of tenant:" + tenantDomain, e);
            throw new RequestObjectException(OAuth2ErrorCodes.SERVER_ERROR, "Server Error while validating audience " +
                    "of Request Object.");
        }

        if (isEmpty(residentIdpAlias)) {
            residentIdpAlias = IdentityUtil.getProperty(OIDC_ID_TOKEN_ISSUER_ID);
            if (isNotEmpty(residentIdpAlias)) {
                if (log.isDebugEnabled()) {
                    log.debug("'IdPEntityID' property was empty for tenantDomain: " + tenantDomain + ". Using " +
                            "OIDC IDToken Issuer value: " + residentIdpAlias + " as alias to identify Resident IDP.");
                }
            }
        }

        // add IdPEntityID or the "issuer" as a valid "aud" value
        validAudUrls.add(residentIdpAlias);

        try {
            URL residentIdPUrl = new URL(residentIdpAlias);
            // derive PAR EP URL from the residentIdP base URL
            URL parEpUrl = new URL(residentIdPUrl, IdentityConstants.PAR_ENDPOINT);
            // add PAR EP URL as a valid "aud" value
            validAudUrls.add(parEpUrl.toString());
        } catch (MalformedURLException e) {
            if (log.isDebugEnabled()) {
                log.error("Error occurred while deriving PAR endpoint URL.", e);
            }
        }

        return validAudUrls;
    }
}
