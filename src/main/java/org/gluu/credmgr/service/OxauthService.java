package org.gluu.credmgr.service;

import org.gluu.credmgr.service.error.OPException;
import org.springframework.stereotype.Service;
import org.xdi.oxauth.client.*;
import org.xdi.oxauth.model.common.AuthorizationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.ResponseType;
import org.xdi.oxauth.model.register.ApplicationType;

import java.util.List;

@Service
public class OxauthService {

    private static final String OPEN_ID_CONFIGURATION = "/.well-known/openid-configuration";

    public OpenIdConfigurationResponse getOpenIdConfiguration(String gluuHost) throws OPException {
        try {
            OpenIdConnectDiscoveryClient openIdConnectDiscoveryClient = new OpenIdConnectDiscoveryClient(gluuHost);
            OpenIdConnectDiscoveryResponse openIdConnectDiscoveryResponse = openIdConnectDiscoveryClient.exec();

            if (openIdConnectDiscoveryResponse.getStatus() == 200) {
                String openIdConfigurationUrl = openIdConnectDiscoveryResponse.getLinks().get(0).getHref()
                    + OPEN_ID_CONFIGURATION;
                OpenIdConfigurationClient openIdConfigurationClient = new OpenIdConfigurationClient(
                    openIdConfigurationUrl);
                return openIdConfigurationClient.execOpenIdConfiguration();
            }
        } catch (Exception e) {
            throw new OPException(OPException.CAN_NOT_RETRIEVE_OPEN_ID_CONFIGURATION, e);
        }
        throw new OPException(OPException.CAN_NOT_RETRIEVE_OPEN_ID_CONFIGURATION);
    }

    public ClientInfoResponse getClientInfo(String gluuHost, String accessToken) throws OPException {
        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        ClientInfoClient clientInfoClient = new ClientInfoClient(openIdConfiguration.getClientInfoEndpoint());
        return clientInfoClient.execClientInfo(accessToken);
    }

    public RegisterResponse registerClient(String gluuHost, ApplicationType applicationType, String clientName,
                                           List<String> redirectUris) throws OPException {
        RegisterRequest request = new RegisterRequest(applicationType, clientName, redirectUris);

        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        RegisterClient client = new RegisterClient(openIdConfiguration.getRegistrationEndpoint());
        client.setRequest(request);
        return client.exec();
    }

    public String getAuthorizationUrl(String gluuHost, String clientId, List<ResponseType> responseTypes,
                                      List<String> scopes, String redirectUri) throws OPException {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest(responseTypes, clientId, scopes,
            redirectUri, null);

        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        return openIdConfiguration.getAuthorizationEndpoint() + "?" + authorizationRequest.getQueryString();
    }

    public TokenResponse getToken(String gluuHost, GrantType grantType, String clientId, String clientSecret,
                                  String code, String redirectUri, String scope) throws OPException {
        TokenRequest request = new TokenRequest(grantType);
        request.setAuthUsername(clientId);
        request.setAuthPassword(clientSecret);
        request.setCode(code);
        request.setRedirectUri(redirectUri);
        request.setScope(scope);

        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        TokenClient client = new TokenClient(openIdConfiguration.getTokenEndpoint());
        client.setRequest(request);
        return client.exec();
    }

    public UserInfoResponse getUserInfo(String gluuHost, String accessToken, AuthorizationMethod authorizationMethod)
        throws OPException {
        UserInfoRequest request = new UserInfoRequest(accessToken);
        request.setAuthorizationMethod(authorizationMethod);

        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        UserInfoClient client = new UserInfoClient(openIdConfiguration.getUserInfoEndpoint());
        client.setRequest(request);
        return client.exec();
    }

    public EndSessionResponse logout(String gluuHost, String idToken, String logoutRedirectUri) throws OPException {
        EndSessionRequest endSessionRequest = new EndSessionRequest(idToken, logoutRedirectUri, null);

        OpenIdConfigurationResponse openIdConfiguration = getOpenIdConfiguration(gluuHost);
        EndSessionClient endSessionClient = new EndSessionClient(openIdConfiguration.getEndSessionEndpoint());
        endSessionClient.setRequest(endSessionRequest);
        return endSessionClient.exec();
    }
}
