/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthClientCredentialsGrantRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthClientCredentialsGrantRequestAuthenticator {

    final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultOAuthClientCredentialsGrantRequestAuthenticator(Application application, DataStore dataStore) {
        super(application, dataStore);
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthClientCredentialsGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of ClientCredentialsGrantRequest.");
        OAuthClientCredentialsGrantRequestAuthentication oauthClientCredentialsGrantRequestAuthentication = (OAuthClientCredentialsGrantRequestAuthentication) authenticationRequest;

        OAuthClientCredentialsAuthenticationAttempt authAttempt = new DefaultOAuthClientCredentialsGrantAuthenticationAttempt(dataStore);
        authAttempt.setApiKeyId(oauthClientCredentialsGrantRequestAuthentication.getApiKeyId());
        authAttempt.setApiKeySecret(oauthClientCredentialsGrantRequestAuthentication.getApiKeySecret());
        authAttempt.setGrantType(oauthClientCredentialsGrantRequestAuthentication.getGrantType());
        if (oauthClientCredentialsGrantRequestAuthentication.getAccountStore() != null){
            authAttempt.setAccountStore(oauthClientCredentialsGrantRequestAuthentication.getAccountStore());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, authAttempt, GrantAuthenticationToken.class, httpHeaders);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);

        return builder.build();
    }
}
