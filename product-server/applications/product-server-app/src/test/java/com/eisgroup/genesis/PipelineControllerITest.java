/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.eisgroup.genesis.columnstore.config.InMemoryColumnStoreConfig;
import com.eisgroup.genesis.columnstore.config.InMemoryColumnStoreDDLConfig;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.pipeline.controller.PipelineController;
import com.eisgroup.genesis.product.specification.pipeline.Pipeline;
import com.eisgroup.genesis.product.specification.pipeline.PipelineExecutionResult;
import com.eisgroup.genesis.product.specification.pipeline.PipelineExecutionSuccessResult;
import com.eisgroup.genesis.product.specification.pipeline.PipelineFactory;
import com.eisgroup.genesis.product.specification.pipeline.PipelineVariable;
import com.eisgroup.genesis.product.specification.pipeline.config.AsyncPipelineExecutorConfig;
import com.eisgroup.genesis.product.specification.pipeline.domain.PipelineInstance;
import com.eisgroup.genesis.product.specification.pipeline.domain.PipelineStatus;
import com.eisgroup.genesis.product.specification.pipeline.repository.api.PipelineInstanceRepository;
import com.eisgroup.genesis.product.specification.pipeline.repository.config.PipelineRepositoryConfiguration;
import com.eisgroup.genesis.repository.RepositoryItest;
import com.eisgroup.genesis.test.IntegrationTests;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author azhakhavets
 * @since 10.10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTests.class)
@ContextConfiguration(classes = {PipelineControllerITest.Config.class,
        AsyncPipelineExecutorConfig.class, PipelineRepositoryConfiguration.class,
        InMemoryColumnStoreConfig.class,
        InMemoryColumnStoreDDLConfig.class,
        RepositoryItest.Config.class})
public class PipelineControllerITest {

    private static final String USER_NAME = "userName";

    @Autowired
    private PipelineFactory pipelineFactory;

    @Autowired
    private PipelineController pipelineController;

    @Autowired
    private RepositoryItest repositoryItest;

    @Autowired
    private PipelineInstanceRepository pipelineInstanceRepository;

    private static final PipelineVariable variable1 =
            new PipelineVariable<>("variable1", InputStreamSource.class, true);

    @Before
    public void setUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken(new User(USER_NAME, "null", List.of()), null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        repositoryItest.setUp(PipelineInstance.class);
        pipelineFactory.register(new Pipeline() {
            @Override
            public String getName() {
                return "testPipeline";
            }

            @Override
            public String getDescription() {
                return "test pipeline";
            }

            @Override
            public Collection<PipelineVariable> getVariables() {
                return List.of(variable1);
            }

            @Override
            public Optional<ErrorHolder> validateInputParameters(Map<String, Object> parametersMap) {
                return Optional.empty();
            }

            @Override
            public PipelineExecutionResult execute(Map<String, Object> parametersMap) {
                return new PipelineExecutionSuccessResult(new JsonObject());
            }
        });
    }

    @After
    public void onTearDown() {
        repositoryItest.tearDown();
    }

    @Test
    public void testGetPipelines() {
        ResponseEntity<String> result = pipelineController.getPipelines();
        assertThat(result.getStatusCodeValue(), is(HttpStatus.OK.value()));
        assertThat(result.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
        assertThat(result.getBody(), containsString("\"name\":\"testPipeline\""));
    }

    @Test
    public void testExecutePipelineBadRequest() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        ResponseEntity<String> resultBadContentType = pipelineController.executePipeline(httpRequest,
                "wrongName");
        assertThat(resultBadContentType.getStatusCodeValue(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void testPipelineExecutionRequest() {
        MockMultipartHttpServletRequest httpRequest = new MockMultipartHttpServletRequest();
        httpRequest.addFile(new MockMultipartFile(variable1.getName(), new byte[]{1}));
        ResponseEntity<String> result = pipelineController.executePipeline(httpRequest, "testPipeline");
        assertThat(result.getStatusCodeValue(), is(HttpStatus.OK.value()));
        assertThat(result.getBody(), containsString("\"pipelineInstanceId\":"));
    }

    @Test
    public void testPipelineVariablesRequest() {
        ResponseEntity<String> result = pipelineController.getPipelineVariables("testPipeline");
        assertThat(result.getStatusCodeValue(), is(HttpStatus.OK.value()));
        assertThat(result.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
        assertThat(result.getBody(), containsString("\"name\":\"variable1\""));
    }

    @Test
    public void testGetPipeline() {
        ResponseEntity<String> result = pipelineController.getPipeline("testPipeline");
        assertThat(result.getStatusCodeValue(), is(HttpStatus.OK.value()));
        assertThat(result.getHeaders().getContentType(), is(MediaType.APPLICATION_JSON));
        assertThat(result.getBody(), containsString("\"name\":\"testPipeline\""));
        assertThat(result.getBody(), containsString("\"name\":\"variable1\""));
    }

    @Test
    public void shouldLoadSavedPipelineInstance() {
        final JsonParser jsonParser = new JsonParser();

        final PipelineInstance pipelineInstance = PipelineInstance.builder()
                .id(UUID.randomUUID())
                .status(PipelineStatus.COMPLETED)
                .pipelineName("pipelineName")
                .startedBy(USER_NAME)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .variables(jsonParser.parse("{\"productSpecification\":\"PS_v1.xlsx\"}"))
                .result(jsonParser.parse("{\"productCd\":\"PS\"}"))
                .build();

        pipelineInstanceRepository.save(pipelineInstance);

        ResponseEntity<String> result = pipelineController.getPipelineInstance(pipelineInstance.getId().toString());
        assertThat(result.getStatusCodeValue(), is(HttpStatus.OK.value()));
        assertThat(result.getBody(), not(nullValue()));

        JsonElement jsonBody = jsonParser.parse(result.getBody());
        PipelineInstance actualPipelineInstance =
                new PipelineInstance(getBodySuccessPartAsJsonObject(jsonBody));

        assertThat(actualPipelineInstance.getId(), is(pipelineInstance.getId()));
        assertThat(actualPipelineInstance.getPipelineName(), is(pipelineInstance.getPipelineName()));
        assertThat(actualPipelineInstance.getStartedBy(), is(pipelineInstance.getStartedBy()));
        assertThat(actualPipelineInstance.getStartedAt(), is(pipelineInstance.getStartedAt()));
        assertThat(actualPipelineInstance.getEndedAt(), is(pipelineInstance.getEndedAt()));
        assertThat(actualPipelineInstance.getStatus(), is(pipelineInstance.getStatus()));
        assertThat(actualPipelineInstance.getVariables(), is(pipelineInstance.getVariables()));
        assertThat(actualPipelineInstance.getResult(), is(pipelineInstance.getResult()));
    }

    @Test
    public void shouldBeNotFountIfPipelineInstanceNotExist() {
        ResponseEntity<String> result = pipelineController.getPipelineInstance(UUID.randomUUID().toString());
        assertThat(result.getStatusCodeValue(), is(HttpStatus.NOT_FOUND.value()));
    }

    private JsonObject getBodySuccessPartAsJsonObject(JsonElement jsonBody) {
        return jsonBody.getAsJsonObject().getAsJsonObject("body").getAsJsonObject("success");
    }

    @Configuration
    public static class Config {

        @Bean
        public PipelineController pipelineController() {
            return new PipelineController();
        }

        @Bean
        @ConditionalOnMissingBean(PipelineFactory.class)
        public PipelineFactory pipelineFactory() {
            return new PipelineFactory();
        }
    }
}
