/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.security.impl;

import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.http.factory.HttpClientFactory;
import com.eisgroup.genesis.security.GenesisAuthenticationToken;
import com.eisgroup.genesis.security.MockAuthenticationProvider;
import com.eisgroup.genesis.security.TokenConfiguration;
import com.eisgroup.genesis.security.errors.AuthorizationServerNotAvailableException;
import com.eisgroup.genesis.security.repository.SecurityRepository;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;

/**
 * Spring security authentication provider for third-party JWT token with only
 * user ID claim.
 * 
 * @author dstulgis
 */
public class ThirdPartyTokenAuthenticationProvider extends MockAuthenticationProvider {

    private static final String CHECK_TOKEN_PATH = "/oauth/check_token";
    
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyTokenAuthenticationProvider.class);
    
    private static final Gson gson = new Gson();
    
    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private TokenConfiguration tokenConfiguration;
    
    @Value("${genesis.user.model}")
    private String userModelName;
    
    @Autowired
    private HttpClientFactory httpClientFactory;
    
    @Value("${security.thirdparty.client.url}")
    private String authenticationServerUrl;
    
    protected HttpClient httpClient;
    
    @Value("${security.thirdparty.client.id}")
    private String clientId;

    @Value("${security.thirdparty.client.secret}")
    private String clientSecret;
    
    @PostConstruct
    public void createClient() {
        this.httpClient = httpClientFactory.create();
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return ThirdPartyToken.class.isAssignableFrom(authentication);
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) {
        String token = ((ThirdPartyToken) authentication).getCredentials();
        Map<String, Object> parsedResults = checkToken(token);
        String subject = (String) parsedResults.get("sub");
        String foundClientId = (String) parsedResults.get("client_id");
        LocalDateTime expiration = LocalDateTime.ofEpochSecond(
                ((Double) parsedResults.get("exp")).longValue(), 0, ZoneOffset.UTC);
        return new GenesisAuthenticationToken(this.retrieveUser(subject), () -> token, expiration, foundClientId);
    }

    private Map<String, Object> checkToken(String token) {
        HttpPost request = new HttpPost(authenticationServerUrl + CHECK_TOKEN_PATH);

        request.setEntity(new UrlEncodedFormEntity(
                Collections.singletonList(
                        new BasicNameValuePair("token", token)
                ), StandardCharsets.ISO_8859_1));

        request.setHeader(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(clientId, clientSecret));

        Map<String, Object> map = parseResults(doExecute(request)); // AccessDeniedException in case check fails
        
        if (map.containsKey("error")) {
            if (log.isDebugEnabled()) {
                log.debug("check_token returned error: {}", map.get("error"));
            }
            throw new BadCredentialsException("Thirdparty token validation failed.");
        }

        if (!Boolean.TRUE.equals(map.get("active"))) {
            if (log.isDebugEnabled()) {
                log.debug("check_token returned active attribute: {}", map.get("active"));
            }
            throw new CredentialsExpiredException("Thirdparty token has expired.");
        }
        return map;
    }
    
    @SuppressWarnings("serial")
    private Map<String, Object> parseResults(String result) {
        return gson.fromJson(result, new TypeToken<Map<String, Object>>(){}.getType());
    }

    protected String doExecute(HttpUriRequest request) {
        try {
            HttpResponse response = httpClient.execute(request);
            int httpStatusCode = response.getStatusLine().getStatusCode();

            try (InputStream inputStream = response.getEntity().getContent()) {
                String body = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

                if (httpStatusCode >= 400) {
                    throw new AccessDeniedException("Authorization failed with error: " + body);
                }

                return body;
            }
        }
        catch (ClientProtocolException | UnknownHostException e) {
            throw new AuthorizationServerNotAvailableException("Authorization server not available", e);
        }
        catch (IOException | IllegalStateException e) {
            throw new InvocationError(e);
        }
    }

    @SuppressWarnings("squid:S1166")
    private String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String.");
        }
    }

}
