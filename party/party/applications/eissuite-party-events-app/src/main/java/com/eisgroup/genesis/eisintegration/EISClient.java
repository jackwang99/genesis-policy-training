/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.eisintegration;

import java.io.IOException;
import com.eisgroup.genesis.eis.dto.CreatePartyRequest;
import com.eisgroup.genesis.http.factory.HttpClientFactory;
import com.eisgroup.genesis.json.wrapper.DefaultJsonWrapperFactory;
import com.eisgroup.genesis.json.wrapper.JsonWrapperFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Sends {@link CreatePartyRequest} to the configured EIS
 *
 * @author dlevchuk
 */
public class EISClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EISClient.class);
    private static final String BASE_URL = System.getProperty("EISCORE_BASE_URL", System.getenv()
            .getOrDefault("EISCORE_BASE_URL", "localhost"));

    @Value("${eis_base.createPartyUrl}")
    protected String createPartyUrl;

    private JsonWrapperFactory wrapperFactory = new DefaultJsonWrapperFactory();

    @Autowired
    private HttpClientFactory httpClientFactory;

    public void execute(CreatePartyRequest createPartyRequest) {
        try {
            sendHttpPostRequest(createPartyRequest);
        } catch (Exception e) {
            LOGGER.error("Can't create parties", e);
        }
    }

    private void sendHttpPostRequest(CreatePartyRequest createPartyRequest) throws IOException {
        try (CloseableHttpClient httpClient = httpClientFactory.createSecured()) {
            HttpPost request = new HttpPost(BASE_URL + createPartyUrl);
            request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

            LOGGER.info("Request to: {}", request.getURI());

            String jsonString = wrapperFactory.unwrap(createPartyRequest).toString();
            request.setEntity(new StringEntity(jsonString));

            LOGGER.info("Json to party: {}", jsonString);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    LOGGER.error("Can't create parties. Status: {}", response.getStatusLine());
                }
            }
        }
    }

}
