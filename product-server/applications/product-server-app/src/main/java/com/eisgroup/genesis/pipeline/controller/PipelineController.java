/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.pipeline.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.eisgroup.genesis.communication.protocol.ResponseEnvelope;
import com.eisgroup.genesis.exception.ErrorHolder;
import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.product.specification.pipeline.FileContent;
import com.eisgroup.genesis.product.specification.pipeline.Pipeline;
import com.eisgroup.genesis.product.specification.pipeline.PipelineExecutionResult;
import com.eisgroup.genesis.product.specification.pipeline.PipelineExecutor;
import com.eisgroup.genesis.product.specification.pipeline.PipelineFactory;
import com.eisgroup.genesis.product.specification.pipeline.domain.PipelineInstance;
import com.eisgroup.genesis.product.specification.pipeline.dto.PipelineDTO;
import com.eisgroup.genesis.product.specification.pipeline.dto.PipelineVariableDTO;
import com.eisgroup.genesis.product.specification.pipeline.dto.PipelineVariablesDTO;
import com.eisgroup.genesis.product.specification.pipeline.dto.PipelinesDTO;
import com.eisgroup.genesis.product.specification.pipeline.repository.api.PipelineInstanceRepository;
import com.eisgroup.genesis.security.Privileges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import static com.eisgroup.genesis.communication.ResponseMessage.getSuccessInstance;
import static com.eisgroup.genesis.product.specification.pipeline.error.PipelineErrorDefinition.NO_PIPELINE;
import static com.eisgroup.genesis.product.specification.pipeline.error.PipelineErrorDefinition.NO_PIPELINE_INSTANCE;
import static com.eisgroup.genesis.utils.ResponseUtils.createResponseEnvelopeBodyAsJson;

/**
 * REST API to work with pipelines
 *
 * @author dlevchuk
 * @since 10.9
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {

    private static final String PIPELINE = "pipeline";
    private static final String PIPELINE_INSTANCE_ID ="pipelineInstanceId";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @Autowired
    private PipelineFactory pipelineFactory;

    @Autowired
    private PipelineExecutor<Pipeline> pipelineExecutor;

    @Autowired
    private PipelineInstanceRepository pipelineInstanceRepository;

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getPipelines() {
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ResponseEnvelope<>(null, getSuccessInstance(
                        new PipelinesDTO(pipelineFactory.getPipelines().stream()
                                .map(pipeline -> new PipelinesDTO.PipelineInfoDTO(
                                        pipeline.getName(), pipeline.getDescription()))
                                .collect(Collectors.toList())),
                        HttpStatus.OK.toString()), true).toJsonString());
    }

    @GetMapping(value = "/{pipeline}",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getPipeline(@PathVariable(PIPELINE) String pipelineName) {
        Pipeline pipeline = pipelineFactory.getPipeline(pipelineName).orElse(null);
        if (pipeline == null) {
            return composeNoPipelineResponse(pipelineName);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ResponseEnvelope<>(null, getSuccessInstance(new PipelineDTO(pipeline),
                        HttpStatus.OK.toString()), true).toJsonString());
    }

    @GetMapping(value = "/{pipeline}/variables",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getPipelineVariables(@PathVariable(PIPELINE) String pipelineName) {
        Pipeline pipeline = pipelineFactory.getPipeline(pipelineName).orElse(null);
        if (pipeline == null) {
            return composeNoPipelineResponse(pipelineName);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ResponseEnvelope<>(null, getSuccessInstance(
                        getPipelineVariablesDTO(pipeline),
                        HttpStatus.OK.toString()), true).toJsonString());
    }

    @Secured(Privileges.PIPELINE_EXECUTION)
    @PostMapping(value = "/execute/{pipeline}",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> executePipeline(
            HttpServletRequest httpRequest,
            @PathVariable(PIPELINE) String pipelineName) {

        Pipeline pipeline = pipelineFactory.getPipeline(pipelineName).orElse(null);
        if (pipeline == null) {
            return composeNoPipelineResponse(pipelineName);
        }

        Map<String, Object> inputParamsValues = new HashMap<>(httpRequest.getParameterMap());
        if (httpRequest.getContentType() != null && httpRequest.getContentType().startsWith(MULTIPART_FORM_DATA)) {
            inputParamsValues.putAll(((MultipartHttpServletRequest) httpRequest).getFileMap());
        }

        Optional<ErrorHolder> errorHolder = pipeline.validateInputParameters(inputParamsValues);
        if (errorHolder.isPresent()) {
            return composeResponseWithError(HttpStatus.BAD_REQUEST, errorHolder.get());
        }
        //remap MultipartFile to FileContent to use in multiple threads
        Map<String, Object> executionParameters = inputParamsValues.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::getExecutionParameter));

        PipelineExecutionResult pipelineResult = pipelineExecutor.execute(pipeline, executionParameters);

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ResponseEnvelope<>(null,
                        getSuccessInstance(pipelineResult, HttpStatus.OK.toString())).toJsonString());
    }

    @GetMapping(value = "/load/{pipelineInstanceId}")
    public ResponseEntity<String> getPipelineInstance(@PathVariable(PIPELINE_INSTANCE_ID) String pipelineInstanceId) {

        PipelineInstance pipelineInstance = pipelineInstanceRepository.load(UUID.fromString(pipelineInstanceId));

        if (pipelineInstance == null) {
            return composeResponseWithError(HttpStatus.NOT_FOUND,
                    NO_PIPELINE_INSTANCE.builder().params(pipelineInstanceId).build());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ResponseEnvelope<>(null,
                        getSuccessInstance(pipelineInstance.toJson(), HttpStatus.OK.toString()),
                        true).toJsonString());
    }

    private PipelineVariablesDTO getPipelineVariablesDTO(Pipeline pipeline) {
        return new PipelineVariablesDTO(pipeline.getVariables().stream()
                .map(PipelineVariableDTO::new)
                .collect(Collectors.toList()));
    }

    private ResponseEntity<String> composeResponseWithError(HttpStatus httpStatus, ErrorHolder errorHolder) {
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON)
                .body(createResponseEnvelopeBodyAsJson(errorHolder, httpStatus.value()));
    }

    private ResponseEntity<String> composeNoPipelineResponse(String pipelineName) {
        return composeResponseWithError(HttpStatus.NOT_FOUND,
                NO_PIPELINE.builder().params(pipelineName).build());
    }

    /**
     * Remap MultipartFile to FileContent to use in multiple threads
     *
     * @param entryParameter parameter {@link Map.Entry}, name to value
     * @return parameter value
     */
    private Object getExecutionParameter(Map.Entry<String, Object> entryParameter) {
        if (entryParameter.getValue() instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) entryParameter.getValue();
            try {
                return new FileContent(multipartFile.getOriginalFilename(), multipartFile.getBytes());
            } catch (IOException e) {
                throw new InvocationError(e);
            }
        } else {
            return entryParameter.getValue();
        }
    }

}
