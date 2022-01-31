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

package com.wso2.finance.open.banking.dynamic.client.registration.common.util;

import com.google.gson.Gson;
import com.wso2.finance.open.banking.common.config.CommonConfigParser;
import com.wso2.finance.open.banking.common.config.uk.UKSpecConfigParser;
import com.wso2.finance.open.banking.common.util.ApplicationNameUtils;
import com.wso2.finance.open.banking.common.util.CommonConstants;
import com.wso2.finance.open.banking.dynamic.client.registration.common.constants.APIDataConstants;
import com.wso2.finance.open.banking.dynamic.client.registration.common.constants.ApplicationConstants;
import com.wso2.finance.open.banking.dynamic.client.registration.common.data.DynamicClientRegistrationDAO;
import com.wso2.finance.open.banking.dynamic.client.registration.common.exception.DynamicClientRegistrationException;
import com.wso2.finance.open.banking.dynamic.client.registration.common.internal.DynamicClientRegistrationCommonDataHolder;
import com.wso2.finance.open.banking.dynamic.client.registration.common.model.OBClientRegistrationRequestDetail;
import com.wso2.finance.open.banking.dynamic.client.registration.common.model.OBSoftwareStatementPayload;
import com.wso2.finance.open.banking.dynamic.client.registration.common.persistence.DataStoreInitializer;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIConsumer;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.ApiTypeWrapper;
import org.wso2.carbon.apimgt.api.model.Application;
import org.wso2.carbon.apimgt.api.model.Scope;
import org.wso2.carbon.apimgt.api.model.Subscriber;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.APIManagerFactory;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.keymgt.service.APIKeyMgtSubscriberService;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.identity.application.common.model.xsd.ServiceProvider;
import org.wso2.carbon.identity.application.common.model.xsd.ServiceProviderProperty;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;
import org.wso2.carbon.user.core.util.UserCoreUtil;

import java.rmi.RemoteException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class relevant to API manager operations required for DCR.
 */
public abstract class DCRUtil {
    public static final APIManagerFactory API_MANAGER_FACTORY = APIManagerFactory.getInstance();
    private static APIManagerConfiguration config = DynamicClientRegistrationCommonDataHolder.getInstance().
            getApiManagerConfigurationService().getAPIManagerConfiguration();
    private static final String ADMIN_USER = config.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME);
    private static final String UNLIMITED_TIER = "Unlimited";
    private static final String TOKEN_TYPE_OAUTH = "OAUTH";
    private static Log log = LogFactory.getLog(DCRUtil.class);
    private static APIManagerConfiguration apiManagerConfiguration = ServiceReferenceHolder.getInstance()
            .getAPIManagerConfigurationService().getAPIManagerConfiguration();
    private static String adminUsername =
            apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_USERNAME);
    private static String adminPassword =
            apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_PASSWORD);
    private static String backendServerURL;
    private AuthenticationAdminStub authenticationAdminStub;

    /**
     * Gets the configured Dynamic Client Registration DAO interface.
     *
     * @return Configured Dynamic Client Registration DAO interface.
     */
    public static DynamicClientRegistrationDAO getDynamicClientRegistrationDAOimpl() throws
            DynamicClientRegistrationException {
        return DataStoreInitializer.initializeDynamicClientRegistrationDAO();
    }

    /**
     * Call methods to create API store application and generate keys.
     *
     * @param registrationRequest - registrationRequest
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    public static Map<String, String> createApplication(
            OBClientRegistrationRequestDetail registrationRequest, OBSoftwareStatementPayload softwareStatementPayload,
            String scopes) throws DynamicClientRegistrationException {
        int appId = createAPIStoreApp(registrationRequest, softwareStatementPayload);
        //subscribe apis
        DCRUtil.subscribeAPIs(appId, scopes);
        Map<String, Object> appDetailsObjMap = generateAppKeys(appId,
                StringUtils.upperCase(getSoftwareEnvironment(softwareStatementPayload)));
        Map<String, String> appDetails = getAppDetailsMap(appId, appDetailsObjMap);

        return appDetails;
    }

    /**
     * Create an application in API Store.
     *
     * @param registrationRequest - DynamicClientRegistrationRequest
     * @return - appId
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    private static int createAPIStoreApp(OBClientRegistrationRequestDetail registrationRequest,
                                         OBSoftwareStatementPayload softwareStatementPayload)
            throws DynamicClientRegistrationException {
        String clientName = softwareStatementPayload.getSoftwareClientName();
        Application application = getApplicationObject(registrationRequest, softwareStatementPayload);
        int appId;

        //create Api Store application
        try {
            APIConsumer apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);

            if (log.isDebugEnabled()) {
                log.debug(String.format("Creating API Store application for %s", clientName));
            }

            if (apiConsumer.getSubscriber(ADMIN_USER) == null) {
                apiConsumer.addSubscriber(ADMIN_USER, null);
            }

            appId = apiConsumer.addApplication(application, ADMIN_USER);

        } catch (APIManagementException e) {
            log.error(String.format("Error while creating API Store application for %s. %s", clientName, e));
            throw new DynamicClientRegistrationException(String.
                    format("Error while creating API Store application for %s", clientName), e);
        }
        return appId;
    }

    /**
     * Update an application in API Store.
     *
     * @param registrationRequest - DynamicClientRegistrationRequest
     * @param appId               - appId
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    public static void updateAPIStoreApp(OBClientRegistrationRequestDetail registrationRequest,
                                         OBSoftwareStatementPayload softwareStatement, int appId)
            throws DynamicClientRegistrationException {
        String clientName = softwareStatement.getSoftwareClientName();
        Application oldApplication;
        Application newApplication = getApplicationObject(registrationRequest, softwareStatement);
        String additionalParam = String.format("{\"username\":\"%s\"}", ADMIN_USER);

        //update Api Store application
        try {
            APIConsumer apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);
            oldApplication = apiConsumer.getApplicationById(appId);
            newApplication.setUUID(oldApplication.getUUID());

            if (log.isDebugEnabled()) {
                log.debug(String.format("Updating API Store application for %s", clientName));
            }
            updateSPMetadata(newApplication.getName(), newApplication.getKeyType(),
                    newApplication.getApplicationAttributes());
            apiConsumer.updateApplication(newApplication);
            apiConsumer.updateAuthClient(ADMIN_USER, oldApplication.getName(), newApplication.getKeyType() /* SANDBOX
            or PRODUCTION */, newApplication.getCallbackUrl(), null, null, null, null, additionalParam);


        } catch (APIManagementException e) {
            log.error(String.format("Error while updating API Store application for %s. %s", clientName, e));
            throw new DynamicClientRegistrationException(String.
                    format("Error while updating API Store application for %s", clientName), e);
        }
    }

    private static void updateSPMetadata(String originalApplicationName, String keyType,
                                         Map<String, String> appAttributes) throws DynamicClientRegistrationException {
        String userName = ADMIN_USER;
        String userStoreDomain = UserCoreUtil.extractDomainFromName(userName);
        int index = 0;
        String usernameWithoutDomain = userName;
        if ((index = userName.indexOf("@")) >= 0) {
            usernameWithoutDomain = userName.substring(0, index) + "-AT-" + userName.substring(index + 1);
        }
        String appNameWithoutUserStoreDomain = APIUtil.replaceEmailDomain(usernameWithoutDomain) + "_" +
                originalApplicationName + "_" + keyType;
        String applicationName =
                CommonConstants.PRIMARY_DOMAIN_NAME.equals(userStoreDomain) ||
                        StringUtils.isEmpty(userStoreDomain) ? appNameWithoutUserStoreDomain :
                        userStoreDomain + "_" + appNameWithoutUserStoreDomain;
        backendServerURL = apiManagerConfiguration.getFirstProperty(APIConstants.API_KEY_VALIDATOR_URL);
        String appMgtServiceURL = backendServerURL + ApplicationConstants.IDENTITY_APPLICATION_MGT_SERVICE;

        try {
            IdentityApplicationManagementServiceStub identityApplicationManagementServicesStub
                    = new IdentityApplicationManagementServiceStub(appMgtServiceURL);

            String authenticationServiceURL = backendServerURL + ApplicationConstants.AUTHENTICATION_ADMIN_SERVICE;
            AuthenticationAdminStub authenticationAdminStub = new AuthenticationAdminStub(authenticationServiceURL);
            ServiceProvider serviceProvider;
            String sessionCookie = null;
            if (authenticationAdminStub.login(adminUsername, adminPassword,
                    "localhost")) {
                ServiceContext serviceContext = authenticationAdminStub
                        ._getServiceClient().getLastOperationContext()
                        .getServiceContext();
                sessionCookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
            }
            ServiceClient appMgtClient =
                    identityApplicationManagementServicesStub._getServiceClient();
            Options userAdminOption = appMgtClient.getOptions();
            userAdminOption.setManageSession(true);
            userAdminOption.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, sessionCookie);
            serviceProvider =
                    identityApplicationManagementServicesStub.getApplication(applicationName);
            ArrayList<ServiceProviderProperty> spPropList =
                    new ArrayList<>(Arrays.asList(serviceProvider.getSpProperties()));

            for (Map.Entry<String, String> entry : appAttributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                ServiceProviderProperty serviceProviderproperty = new ServiceProviderProperty();

                //Removes older values and then adds new values to SP property list
                if (!StringUtils.isEmpty(value)) {
                    for (ServiceProviderProperty sp : spPropList) {
                        String spName = sp.getName();
                        if (spName.equals(key)) {
                            spPropList.remove(sp);
                            break;
                        }
                    }
                    serviceProviderproperty.setDisplayName(key);
                    serviceProviderproperty.setName(key);
                    serviceProviderproperty.setValue(value);
                    spPropList.add(serviceProviderproperty);
                }
            }

            ArrayList<ServiceProviderProperty> customProplist = new ArrayList<>();
            spPropList.addAll(customProplist);
            serviceProvider.setSpProperties(spPropList.toArray(new ServiceProviderProperty[0]));

            //Update Application with new values
            identityApplicationManagementServicesStub.updateApplication(serviceProvider);
            identityApplicationManagementServicesStub.getApplication(applicationName);
        } catch (RemoteException e) {
            throw new DynamicClientRegistrationException("Error occurred while making remote call.", e);
        } catch (IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
            throw new DynamicClientRegistrationException("Error occurred while updating the application", e);
        } catch (LoginAuthenticationExceptionException e) {
            throw new DynamicClientRegistrationException("Error occurred while authenticating user.", e);
        }
    }

    /**
     * Returns Application object when the DynamicClientRegistrationRequest is given.
     *
     * @param registrationRequest - DynamicClientRegistrationRequest
     * @return - Application object
     */
    private static Application getApplicationObject(OBClientRegistrationRequestDetail registrationRequest,
                                                    OBSoftwareStatementPayload softwareStatementPayload) {
        Map<String, String> appAttributes;

        /* Use Software Id as application name, if configured
           This is in place to bypass native naming limitations of APIM
           (UseSoftwareIdAsApplicationName property in open-banking.xml)
        */
        String appNameField = ApplicationNameUtils
                .getSafeApplicationName(softwareStatementPayload.getSoftwareClientName());

        if (UKSpecConfigParser.getInstance().isSoftwareIdAsApplicationNameUseEnabled()) {
            appNameField = softwareStatementPayload.getSoftwareId();
        }

        String softwareDescription = softwareStatementPayload.getSoftwareClientDescription();

        String callbackUris;
        if (CommonConstants.AU_SPEC_NAME.equalsIgnoreCase(
                CommonConfigParser.getInstance().getDeployedSpecification())) {
            if (registrationRequest.getRedirectUris().isEmpty()) {
                callbackUris = CommonUtil.getCallbackUriString(softwareStatementPayload.getRedirectUris());
            } else {
                callbackUris = CommonUtil.getCallbackUriString(registrationRequest.getRedirectUris());
            }
        } else {
             callbackUris = CommonUtil.getCallbackUriString(registrationRequest.getRedirectUris());
        }
        String softwareEnvironment = getSoftwareEnvironment(softwareStatementPayload);
        appAttributes = getApplicationAttributes(softwareStatementPayload, registrationRequest);

        //prepare application object
        Subscriber subscriber = new Subscriber(ADMIN_USER);
        Application application = new Application(appNameField, subscriber);
        application.setDescription(softwareDescription);
        application.setTier(UNLIMITED_TIER);
        application.setCallbackUrl(callbackUris);
        application.setTokenType(TOKEN_TYPE_OAUTH);
        application.setKeyType(softwareEnvironment);
        application.setOwner(ADMIN_USER);
        application.setApplicationAttributes(appAttributes);
        return application;
    }

    /**
     * Get application attributes with software environment.
     *
     * @param softwareStatementPayload - softwareStatementPayload
     * @return - application attribute map
     */
    private static Map<String, String> getApplicationAttributes(OBSoftwareStatementPayload softwareStatementPayload,
                                                                OBClientRegistrationRequestDetail registrationRequest) {

        String spec = CommonConfigParser.getInstance().getDeployedSpecification();
        Gson gson = new Gson();
        Map<String, String> alteredAppAttributeMap = new HashMap<>();
        String environment = StringUtils.lowerCase(getSoftwareEnvironment(softwareStatementPayload));
        String ssaString = gson.toJson(softwareStatementPayload);
        Map<String, String> appAttributeMap = gson.fromJson(ssaString, Map.class);

        //add id token encrypted response alg & enc details in AU
        if (CommonConstants.AU_SPEC_NAME.equalsIgnoreCase(spec)) {
            appAttributeMap.put(ApplicationConstants.ID_TOKEN_ENCRYPTED_RESPONSE_ALG,
                    registrationRequest.getIdTokenEncryptedResponseAlg());
            appAttributeMap.put(ApplicationConstants.ID_TOKEN_ENCRYPTED_RESPONSE_ENC,
                    registrationRequest.getIdTokenEncryptedResponseEnc());
            appAttributeMap.put(ApplicationConstants.ID_TOKEN_SIGNED_RESPONSE_ALG,
                    registrationRequest.getIdTokenSignedResponseAlg());
        }

        for (Map.Entry entry : appAttributeMap.entrySet()) {
            String newKey = entry.getKey().toString().concat("_" + environment);
            String value = gson.toJson(entry.getValue()).replaceAll("^\"|\"$", ""); //remove
            // unnecessary inverted commas.
            alteredAppAttributeMap.put(newKey, value);
        }

        //add the regulatory compliance attribute
        alteredAppAttributeMap.put(ApplicationConstants.REGULATORY, "true");

        //Change jwks_uri key to support "software_jwks_endpoint" sp attribute.
        if (!appAttributeMap.containsKey(ApplicationConstants.SOFTWARE_JWKS_ENDPOINT)) {
            alteredAppAttributeMap.put(ApplicationConstants.SOFTWARE_JWKS_ENDPOINT + "_" + environment,
                    alteredAppAttributeMap.remove(ApplicationConstants.JWKS_URI + "_" + environment));
        }
        return alteredAppAttributeMap;
    }

    /**
     * Generates keys for the API Store application with given appId.
     *
     * @param appId - application Id
     * @return - storeAppDetails
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    private static Map<String, Object> generateAppKeys(int appId, String environment)
            throws DynamicClientRegistrationException {
        Map<String, Object> storeAppDetails;
        String additionalParam = String.format("{\"username\":\"%s\"}", ADMIN_USER);
        try {
            APIConsumer apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);
            Application application = apiConsumer.getApplicationById(appId);
            storeAppDetails = apiConsumer.requestApprovalForApplicationRegistration(
                    ADMIN_USER, application.getName(), environment, application.getCallbackUrl(),
                    null, null, null, null, additionalParam);

            //add client_id_issued_at and client_secret_expires_at parameters
            long now = Instant.now().getEpochSecond();
            storeAppDetails.put("clientIdIssuedAt", now);
            storeAppDetails.put("clientSecretExpiresAt", 0);
        } catch (APIManagementException e) {
            log.error(String.format("Error while generating keys for the application with id %s. %s", appId, e));
            throw new DynamicClientRegistrationException(String.format(
                    "Error while generating keys for the application with id %s", appId), e);
        }
        return storeAppDetails;
    }

    /**
     * Get a <String, String> map of application details.
     *
     * @param appId            - application id
     * @param appDetailsObjMap - <String, Object> map of application details
     * @return - <String, String> map
     */
    private static Map<String, String> getAppDetailsMap(int appId, Map<String, Object> appDetailsObjMap) {
        return buildAppDetailMap(String.valueOf(appId), appDetailsObjMap.get("consumerKey").toString(),
                appDetailsObjMap.get("consumerSecret").toString(),
                appDetailsObjMap.get("keyState").toString(),
                appDetailsObjMap.get("clientIdIssuedAt").toString(),
                appDetailsObjMap.get("clientSecretExpiresAt").toString());
    }

    public static Map<String, String> buildAppDetailMap(String appId, String clientId, String clientSecret,
                                                         String keyState, String clientIdIssuedAt,
                                                         String clientSecretExpiresAt) {
        Map<String, String> appDetails = new HashMap<>();

        appDetails.put("appId", appId);
        appDetails.put("consumerKey", clientId);
        appDetails.put("consumerSecret", clientSecret);
        appDetails.put("keyState", keyState);
        appDetails.put("clientIdIssuedAt", clientIdIssuedAt);
        appDetails.put("clientSecretExpiresAt", clientSecretExpiresAt);

        return appDetails;
    }
    /**
     * Remove application mapping to the given clientId from API store.
     *
     * @param appId - applicationId
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    public static void removeApplication(int appId) throws DynamicClientRegistrationException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("About to remove application with appId %s from the API Store", appId));
        }
        try {
            APIConsumer apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);
            Application application = apiConsumer.getApplicationById(appId);
            apiConsumer.removeApplication(application, ADMIN_USER);
        } catch (APIManagementException e) {
            log.error(String.format("Error while removing application for appId %s. %s", appId, e));
            throw new DynamicClientRegistrationException(String.
                    format("Error while removing application for appId %s", appId), e);
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("Succesfully removed application with appId %s from the API Store", appId));
        }
    }

    /**
     * Return client's environment (SANDBOX/PRODUCTION) when the SSA is given.
     *
     * @param softwareStatementPayload - SSA
     * @return - softwareEnvironment
     */
    public static String getSoftwareEnvironment(OBSoftwareStatementPayload softwareStatementPayload) {
        if (ApplicationConstants.SANDBOX_ENV.equalsIgnoreCase(softwareStatementPayload.getSoftwareEnvironment())) {
            return ApplicationConstants.SANDBOX_ENV;
        } else {
            return ApplicationConstants.PRODUCTION_ENV;
        }
    }

    /**
     * Returns the set of published APIs.
     *
     * @return - Set<API>
     */
    public static Set<API> getPublishedApis() throws DynamicClientRegistrationException {
        Optional<Set<API>> apis;
        try {
            APIConsumer apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);
            apis = Optional.ofNullable(apiConsumer.getAllPublishedAPIs("carbon.super"));
        } catch (APIManagementException e) {
            log.error(String.format("Could not retrieve published api details. %s", e));
            throw new DynamicClientRegistrationException("Error while retrieving published APIs", e);
        }
        return apis.orElse(new HashSet<>());
    }

    /**
     * Subscribe to the published APIs if the requested scopes are matching with the API scope.
     *
     * @param applicationId - applicationId
     * @param scopes        - String containing scopes requested
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    public static void subscribeAPIs(int applicationId, String scopes) throws DynamicClientRegistrationException {

        log.debug("About to subscribe to the published APIs with the new client");
        APIConsumer apiConsumer;
        List<String> requestedScopes = new ArrayList<>(Arrays.asList(scopes.split(" ")));
        try {
            apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);
            ApiTypeWrapper apiTypeWrapper;
            APIIdentifier apiIdentifier;
            Set<API> apis = getPublishedApis();
            for (API api : apis) {
                if (isSubscriptionRestricted(api)) {
                    continue; //Skip subscribing to API if restricted
                }
                apiTypeWrapper = new ApiTypeWrapper(api);
                apiTypeWrapper.setTier(UNLIMITED_TIER);
                apiIdentifier = getAPIIdentifier(applicationId, api);
                Set<String> scopeSet = getApiScopeKeySet(apiIdentifier);
                if (!scopeSet.isEmpty()) {
                    //Subscribe to API if the scope is matching and not already subscribed
                    for (String apiScope : scopeSet) {
                        if (requestedScopes.contains(apiScope) &&
                                !apiConsumer.isSubscribedToApp(apiIdentifier, ADMIN_USER, applicationId)) {
                            try {
                                apiConsumer.addSubscription(apiTypeWrapper, ADMIN_USER, applicationId);
                            } catch (NullPointerException e) { //Catching NPE due to a known error in APIM. Try to
                                // subscribe again. git issue: https://github.com/wso2/product-apim/issues/6850
                                log.debug("Recursive call due to APIM NPE");
                                subscribeAPIs(applicationId, scopes);
                            }
                            if (log.isDebugEnabled()) {
                                log.debug(String.format("Successfully subscribed to %s", apiIdentifier.getApiName()));
                            }
                            break;
                        }
                    }
                } else if (!apiConsumer.isSubscribedToApp(apiIdentifier, ADMIN_USER, applicationId)) {
                    //Subscribe if API has no scopes and not already subscribed
                    try {
                        apiConsumer.addSubscription(apiTypeWrapper, ADMIN_USER, applicationId);
                    } catch (NullPointerException e) {
                        log.debug("Recursive call due to APIM NPE");
                        subscribeAPIs(applicationId, scopes);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Successfully subscribed to %s", apiIdentifier.getApiName()));
                    }
                }
            }
        } catch (APIManagementException e) {
            log.error(String.format("Error occurred while subscribing to the APIs. %s", e));
            throw new DynamicClientRegistrationException("Error occurred while subscribing to the APIs", e);
        }
    }

    /**
     * Check if subscribing to api is restricted.
     * Regex match is used to support all versions of the api.
     *
     * @param api - API
     * @return - boolean
     */
    private static boolean isSubscriptionRestricted(API api) {

        String apiContext = api.getContext();
        for (String contextRegex : APIDataConstants.RESTRICTED_API_LIST) {
            Pattern p = Pattern.compile(contextRegex);
            if (p.matcher(apiContext).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a distinct set of scope keys for the given API.
     * eg: accounts, payments
     *
     * @param apiIdentifier - APIIdentifier
     * @return - Set of scope keys
     * @throws APIManagementException - APIManagementException
     */
    private static Set<String> getApiScopeKeySet(APIIdentifier apiIdentifier) throws APIManagementException {

        Set<Scope> scopeSet = ApiMgtDAO.getInstance().getAPIScopes(apiIdentifier);
        Set<String> scopeKeySet = new HashSet<>();
        scopeSet.iterator().forEachRemaining((scope) -> scopeKeySet.add(scope.getKey()));
        return scopeKeySet;
    }

    /**
     * Get APIIdentifier when the api and applicationId is given.
     *
     * @param applicationId - application id (store)
     * @param api           - API object
     * @return - APIIdentifier
     */
    private static APIIdentifier getAPIIdentifier(int applicationId, API api) {

        APIIdentifier apiIdentifier;
        apiIdentifier = api.getId();
        apiIdentifier.setTier(UNLIMITED_TIER);
        apiIdentifier.setApplicationId(Integer.toString(applicationId));

        return apiIdentifier;
    }

    /**
     * Get client secret when the application id is given.
     *
     * @param appId - application id
     * @return - client secret string
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    public static String getClientSecret(int appId) throws DynamicClientRegistrationException {
        String clientSecret;
        try {
            APIConsumer apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);
            Application applicationWithKeys = apiConsumer.getApplicationsByName(
                    ADMIN_USER, apiConsumer.getApplicationById(appId).getName(), "");
            clientSecret = applicationWithKeys.getKeys().get(0).getConsumerSecret();
            return clientSecret;
        } catch (APIManagementException e) {
            log.error(String.format("Error while retrieving application details for appId %s. %s", appId, e));
            throw new DynamicClientRegistrationException(String.
                    format("Error while retrieving application details for appId %s", appId), e);
        }
    }

    /**
     * Revokes the given access token.
     *
     * @param authHeaderCode - accessTokenCode (eg:- Bearer <authHeaderCode>)
     * @param tokenClientId  - ClientId
     * @throws DynamicClientRegistrationException -  DynamicClientRegistrationException
     */
    public static void revokeAccessToken(String authHeaderCode, String tokenClientId)
            throws DynamicClientRegistrationException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Revoking the access token: %s", authHeaderCode));
        }
        String appName;
        try {
            appName = APIUtil.getApplicationByClientId(tokenClientId).getName();
            APIKeyMgtSubscriberService apiKeyMgtSubscriberService = new APIKeyMgtSubscriberService();
            apiKeyMgtSubscriberService.revokeTokensOfUserByApp(ADMIN_USER, appName, ADMIN_USER);
        } catch (APIManagementException e) {
            throw new DynamicClientRegistrationException(String.format("Error occurred while revoking the " +
                    "access token: %s", authHeaderCode), e);
        }
    }

    /**
     * Pass client id and get Application info
     * @param clientId client id
     * @return Application
     * @throws DynamicClientRegistrationException - DynamicClientRegistrationException
     */
    public static Application getApplicationbyClientId(String clientId) throws DynamicClientRegistrationException {

        try {
            return APIUtil.getApplicationByClientId(clientId);
        } catch (APIManagementException e) {
            throw new DynamicClientRegistrationException(String.format("Error occurred while retrieving the " +
                    "application information for client id: %s", clientId), e);
        }
    }

    /**
     * Convert the MCR application to nee DCR model
     * @param application
     * @throws DynamicClientRegistrationException
     */
    public static void convertToNewDcrModel(Application application,
                                            OBSoftwareStatementPayload softwareStatementPayload)
            throws DynamicClientRegistrationException {
        try {
            if (!(ADMIN_USER).equals(application.getOwner())) { // MCR app, if old DCR app no need to run below logic
                APIConsumer apiConsumer = API_MANAGER_FACTORY.getAPIConsumer(ADMIN_USER);
                Subscriber subscriber = apiConsumer.getSubscriber(application.getOwner());
                Application oldApplication = apiConsumer.getApplicationBySubscriberIdAndName(
                        subscriber.getId(), application.getName());

                // update application name to software id
                oldApplication.setName(softwareStatementPayload.getSoftwareId());
                apiConsumer.updateApplication(oldApplication);
                // update the application owner to admin
                apiConsumer.updateApplicationOwner(ADMIN_USER, oldApplication);
            }
        } catch (APIManagementException e) {
            log.error(String.format("Error while changing owner of the application %s. %s", application.getName(), e));
            throw new DynamicClientRegistrationException(String.
                    format("Error while changing owner of the application %s", application.getName()), e);
        }
    }
}
