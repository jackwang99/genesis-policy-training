/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.product.specification.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;

import com.eisgroup.genesis.design.specification.ProductSpecificationRepository;
import com.eisgroup.genesis.design.specification.product.ProductSpecification;
import com.eisgroup.genesis.dsl.translator.GenerationResult;
import com.eisgroup.genesis.dsl.translator.XlsxDslTranslator;
import com.eisgroup.genesis.dsl.validate.DslValidator;
import com.eisgroup.genesis.dsl.validate.ValidationResult;
import com.eisgroup.genesis.dsl.validate.error.DslValidatorErrorDefinition;
import com.eisgroup.genesis.exception.BaseErrorException;
import com.eisgroup.genesis.exception.InvocationError;
import com.eisgroup.genesis.facade.EndpointFailure;
import com.eisgroup.genesis.product.specification.export.xlsx.XlsxProductSpecificationExporter;
import com.eisgroup.genesis.product.specification.general.ProductKey;
import com.eisgroup.genesis.product.specification.repo.ProductModelRepository;
import com.eisgroup.genesis.security.Privileges;
import com.eisgroup.genesis.utils.ResponseUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static com.eisgroup.genesis.design.specification.product.ProductSpecificationFormat.DSL;
import static com.eisgroup.genesis.design.specification.product.ProductSpecificationFormat.XLSX;
import static com.eisgroup.genesis.error.ProductServerRestApiErrorDefinition.CONVERTING_SPECIFICATION_IS_NOT_AVAILABLE;
import static com.eisgroup.genesis.error.ProductServerRestApiErrorDefinition.EXPORT_TO_SPECIFIED_FORMAT_IS_NOT_AVAILABLE;
import static com.eisgroup.genesis.error.ProductServerRestApiErrorDefinition.PRODUCT_SPECIFICATION_NOT_FOUND;
import static com.eisgroup.genesis.facade.FacadeErrorDefinition.CLIENT_ERROR;
import static com.eisgroup.genesis.facade.FacadeErrorDefinition.UNSUPPORTED_CONTENT_TYPE;
import static com.eisgroup.genesis.utils.ZipUtils.ZipEntryWithContent;
import static com.eisgroup.genesis.utils.ZipUtils.zip;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

/**
 * Design time REST API, to work with product specifications
 *
 * @author dlevchuk
 * @author azhakhavets
 */
@RestController
@RequestMapping("/api/v1/specifications")
public class ProductSpecificationController {

    public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String ZIP_CONTENT_TYPE = "application/zip";

    private static final String DEFAULT_PS_ZIP_FILE_NAME = "product_specification_dsl.zip";
    private static final String ZIP_FILE_EXTENSION = "zip";
    private static final String PRODUCT_CODE = "productCode";
    private static final String PRODUCT_VERSION = "productVersion";
    private static final String FORMAT_FROM = "formatFrom";
    private static final String FORMAT_TO = "formatTo";
    private static final String PRODUCT_SPECIFICATION = "productSpecification";

    @Autowired
    private ProductModelRepository productModelRepository;

    @Autowired
    private ProductSpecificationRepository designProductSpecificationRepository;

    @Autowired
    private XlsxDslTranslator xlsxDslTranslator;

    @Autowired
    private XlsxProductSpecificationExporter xlsxProductSpecificationExporter;

    @Autowired
    private DslValidator compositeDslValidator;

    // TODO: init from properties
    private String psZipFileName = DEFAULT_PS_ZIP_FILE_NAME;

    @RequestMapping(value = "products", method = RequestMethod.GET)
    public Collection<ProductKey> getProductKeys() {
        return productModelRepository.getProductKeys();
    }

    @GetMapping(value = "/{productCode}/v{productVersion}/export/{formatTo}",
            produces = { XLSX_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE })
    @Secured(Privileges.EXPORT_PRODUCT)
    public ResponseEntity<?> exportProductSpecification(@PathVariable(PRODUCT_CODE) String productCode,
                                                        @PathVariable(value = PRODUCT_VERSION) String productVersion,
                                                        @PathVariable(value = FORMAT_TO) String formatTo) throws IOException {

        if (StringUtils.isBlank(productCode) || StringUtils.isBlank(productVersion) || StringUtils.isBlank(formatTo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing required attributes.");
        }

        if (!XLSX.formatName().equals(formatTo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseUtils.createResponseEnvelopeBodyAsJson(EXPORT_TO_SPECIFIED_FORMAT_IS_NOT_AVAILABLE, formatTo));
        }

        ProductSpecification productSpecification = designProductSpecificationRepository
                .getProductSpecification(productCode, productVersion);
        if (productSpecification == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseUtils.createResponseEnvelopeBodyAsJson(PRODUCT_SPECIFICATION_NOT_FOUND.builder().build(),
                            HttpStatus.UNPROCESSABLE_ENTITY.value()));
        }

        byte[] xlsxBytes = xlsxProductSpecificationExporter.export(productSpecification);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, ZIP_CONTENT_TYPE)
                .header(CONTENT_DISPOSITION, "attachment; filename=" +
                        getSpecificationFileName(productCode, productVersion, ZIP_FILE_EXTENSION))
                .body(zip((Supplier<ZipEntryWithContent>) () -> new ZipEntryWithContent() {
                    @Override
                    public ZipEntry getZipEntry() {
                        return new ZipEntry(getSpecificationFileName(productCode, productVersion, XLSX.formatName()));
                    }

                    @Override
                    public InputStream getContent() {
                        return new ByteArrayInputStream(xlsxBytes);
                    }
                }));
    }

    @PostMapping(value = "/convert/{formatFrom}/{formatTo}",
            produces = { ZIP_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE })
    @Secured(Privileges.CONVERT_PRODUCT)
    public ResponseEntity<?> convertSpecification(
            @RequestParam(PRODUCT_SPECIFICATION) MultipartFile multipartFile,
            @PathVariable(value = FORMAT_FROM) String formatFrom,
            @PathVariable(value = FORMAT_TO) String formatTo) {

        if (StringUtils.isBlank(formatFrom) || StringUtils.isBlank(formatTo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing required attributes.");
        }

        if (!XLSX_CONTENT_TYPE.equals(multipartFile.getContentType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                    .body(ResponseUtils.createResponseEnvelopeBodyAsJson(UNSUPPORTED_CONTENT_TYPE, multipartFile.getContentType()));
        }

        if (XLSX.formatName().equals(formatFrom) && DSL.formatName().equals(formatTo)) {
            try {
                List<GenerationResult> generationResults = xlsxDslTranslator
                        .translateXlsxToDsl(multipartFile.getInputStream());

                ValidationResult validationResult = compositeDslValidator.validate(generationResults.toArray(GenerationResult[]::new));
                if (validationResult.hasErrors()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(ResponseUtils.createResponseEnvelopeBodyAsJson(
                                    DslValidatorErrorDefinition.INVALID_DSL.builder(validationResult).build(),
                                    EndpointFailure.DEFAULT_ERROR_CODE));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .header(HttpHeaders.CONTENT_TYPE, ZIP_CONTENT_TYPE)
                        .header(CONTENT_DISPOSITION, "attachment; filename=" + psZipFileName)
                        .body(zip(generationResults.stream()
                                .map(result -> (Supplier<ZipEntryWithContent>) () -> new ZipEntryWithContent() {
                                    @Override
                                    public ZipEntry getZipEntry() {
                                        return new ZipEntry(result.getRelativePath());
                                    }

                                    @Override
                                    public InputStream getContent() {
                                        return IOUtils.toInputStream(result.getContent(), StandardCharsets.UTF_8);
                                    }
                                }).toArray(Supplier[]::new)));
            }
            catch (BaseErrorException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                        .body(ResponseUtils.createResponseEnvelopeBodyAsJson(ex.getErrorHolder(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
            catch (IOException | InvocationError ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                        .body(ResponseUtils.createResponseEnvelopeBodyAsJson(CLIENT_ERROR, ex.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(
                    ResponseUtils.createResponseEnvelopeBodyAsJson(CONVERTING_SPECIFICATION_IS_NOT_AVAILABLE, formatFrom, formatTo));
        }
    }

    /**
     * <Product code>_v<Product version>.<File extension>
     *
     * @param productCode   the product code
     * @param version       the product version
     * @param fileExtension the file extension
     * @return file name
     */
    private String getSpecificationFileName(String productCode, String version, String fileExtension) {
        return String.format("%s_v%s.%s",
                productCode, version,
                fileExtension);
    }
}
