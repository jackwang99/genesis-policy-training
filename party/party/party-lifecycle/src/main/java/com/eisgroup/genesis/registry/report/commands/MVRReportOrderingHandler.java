/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.registry.report.commands;

import com.eisgroup.genesis.commands.ProductCommandHandler;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.MVRReport;
import com.eisgroup.genesis.factory.modeling.types.Person;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.registry.core.common.link.RegistryLink;
import com.eisgroup.genesis.registry.core.common.link.RegistryLinkResolver;
import com.eisgroup.genesis.registry.core.common.repository.RegistryTypeReadRepository;
import com.eisgroup.genesis.registry.core.common.repository.RegistryTypeWriteRepository;
import com.eisgroup.genesis.registry.core.search.api.schema.builder.RegistrySearchIndexDocumentBuilder;
import com.eisgroup.genesis.proto.report.ordering.MVRReportOrderingData;
import com.eisgroup.genesis.registry.report.commands.request.ReportOrderRequest;
import com.eisgroup.genesis.registry.report.ordering.ReportOrderingService;
import com.eisgroup.genesis.repository.ReadContext;
import com.eisgroup.genesis.repository.TargetEntityNotFoundException;
import com.eisgroup.genesis.search.SearchIndex;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Command handler for ordering MVR Report.
 *
 * @author Tomas Dapkunas
 * @since 1.0
 */
public class MVRReportOrderingHandler implements ProductCommandHandler<ReportOrderRequest, MVRReport> {

    public static final String NAME = "orderMVRReport";

    @Autowired
    @Qualifier("mvrReportOrderingService")
    private ReportOrderingService<MVRReport, MVRReportOrderingData> reportOrderingService;

    @Autowired
    private RegistryTypeWriteRepository writeRepository;

    @Autowired
    private RegistryTypeReadRepository readRepository;

    @Autowired
    private RegistrySearchIndexDocumentBuilder registrySearchIndexDocumentBuilder;

    @Autowired
    private SearchIndex searchIndex;

    @Autowired
    private RegistryLinkResolver registryLinkResolver;

    @Autowired
    protected ModelResolver modelResolver;

    @Nonnull
    @Override
    public Single<MVRReport> load(@Nonnull ReportOrderRequest mvrReportOrderRequest) {
        final DomainModel model = modelResolver.resolveModel(DomainModel.class);
        return Single.just((MVRReport) ModelInstanceFactory.createRootInstance(model.getName(), model.getVersion()));
    }

    @Nonnull
    @Override
    public Single<MVRReport> execute(@Nonnull ReportOrderRequest request, @Nonnull MVRReport entity) {
        RegistryLink registryLink = registryLinkResolver
                .resolve(new EntityLink<>(com.eisgroup.genesis.factory.modeling.types.immutable.RegistryType.class, request.getRegistryTypeId()));

        return readRepository.loadCurrent(registryLink.getModelName(),
                registryLink.getRootId(), new ReadContext.Builder().build())
                .toMaybe()
                .switchIfEmpty(Maybe.error(TargetEntityNotFoundException::new))
                .flatMap(person -> orderReport(request, (Person) person))
                .toSingle();
    }

    private Maybe<MVRReport> orderReport(ReportOrderRequest request, Person person) {
        return reportOrderingService.orderReport(new MVRReportOrderingData(person.getFirstName(), person.getLastName()))
                .map(mvrReport -> {
                    mvrReport.setRelatedTypeId(request.getRegistryTypeId());
                    mvrReport.setEffectiveDate(LocalDate.now());

                    return mvrReport;
                });
    }

    @Nonnull
    @Override
    public Single<MVRReport> save(@Nonnull ReportOrderRequest mvrReportOrderRequest, @Nonnull MVRReport entity) {
        return writeRepository.write(entity, modelResolver.getModelName())
                .toSingleDefault(entity)
                .flatMap(mvrReport -> registrySearchIndexDocumentBuilder.createDocument(entity)
                        .flatMapCompletable(searchIndexDocument -> Completable.fromAction(() -> searchIndex.index(searchIndexDocument)))
                                .toSingleDefault(entity));
    }

    @Override
    public String getName() {
        return NAME;
    }

}
