/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.sso.config.web;

import com.eisgroup.genesis.spring.properties.YamlPropertySourceFactory;
import com.eisgroup.genesis.sso.security.ForceApprovalHandler;
import com.eisgroup.genesis.sso.security.GenesisAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Authorization server configuration for issuing OAuth2 tokens.
 *
 * @author gvisokinskas
 */
@Configuration
@ConfigurationProperties
@EnableAuthorizationServer
@EnableConfigurationProperties
@PropertySource(value = "classpath:authorization-server.yml", factory = YamlPropertySourceFactory.class)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final int UNEXPIRING_TOKEN = 0;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;

    private List<OAuthClient> clients = new ArrayList<>();

    private int accessTokenValiditySeconds;

    private KeystoreConfig keystore;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(this.authenticationManager)
                .tokenServices(tokenServices())
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .redirectResolver(redirectResolver())
                .userApprovalHandler(userApprovalHandler());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
        InMemoryClientDetailsServiceBuilder detailsBuilder = configurer.inMemory();
        for (OAuthClient clientDetails : clients) {
            detailsBuilder.withClient(clientDetails.getClientId())
                    .secret(clientDetails.getSecret())
                    .authorizedGrantTypes(clientDetails.getGrantTypes().toArray(new String[]{}))
                    .scopes("read", "write")
                    .redirectUris(clientDetails.getRedirects().toArray(new String[]{}))
                    .authorities(clientDetails.getAuthorities().toArray(new String[]{}))
                    .accessTokenValiditySeconds(accessTokenValiditySeconds)
                    .refreshTokenValiditySeconds(UNEXPIRING_TOKEN);
        }
    }

    @Bean
    public RedirectResolver redirectResolver() {
        DefaultRedirectResolver redirectResolver = new DefaultRedirectResolver();
        redirectResolver.setMatchPorts(false);
        return redirectResolver;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(ssoKeyFactory().getKeyPair(keystore.getAlias()));
        converter.setAccessTokenConverter(defaultAccessTokenConverter());
        return converter;
    }

    @Bean
    public DefaultAccessTokenConverter defaultAccessTokenConverter() {
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        tokenConverter.setUserTokenConverter(userAuthenticationConverter());
        return tokenConverter;
    }

    @Bean
    public UserAuthenticationConverter userAuthenticationConverter() {
        return new GenesisAuthenticationConverter();
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        return defaultTokenServices;
    }

    @Bean
    public KeyStoreKeyFactory ssoKeyFactory() {
        return new KeyStoreKeyFactory(keystore.getPath(), keystore.getPassword().toCharArray());
    }

    @Bean
    public UserApprovalHandler userApprovalHandler() {
        return new ForceApprovalHandler();
    }

    @Bean
    @SuppressWarnings("squid:S2070")
    public PasswordEncoder noOpPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    public List<OAuthClient> getClients() {
        return clients;
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public KeystoreConfig getKeystore() {
        return keystore;
    }

    public void setKeystore(KeystoreConfig keystore) {
        this.keystore = keystore;
    }

    public static class OAuthClient {
        private String clientId;
        private String secret;
        private List<String> grantTypes = new ArrayList<>();
        private List<String> authorities = new ArrayList<>();
        private List<String> redirects = new ArrayList<>();

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public List<String> getGrantTypes() {
            return grantTypes;
        }

        public void setGrantTypes(List<String> grantTypes) {
            this.grantTypes = grantTypes;
        }

        public List<String> getAuthorities() {
            return authorities;
        }

        public void setAuthorities(List<String> authorities) {
            this.authorities = authorities;
        }

        public List<String> getRedirects() {
            return redirects;
        }

        public void setRedirects(List<String> redirects) {
            this.redirects = redirects;
        }
    }

    public static class KeystoreConfig {
        private ClassPathResource path;
        private String alias;
        private String password;

        public ClassPathResource getPath() {
            return path;
        }

        public void setPath(ClassPathResource path) {
            this.path = path;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}