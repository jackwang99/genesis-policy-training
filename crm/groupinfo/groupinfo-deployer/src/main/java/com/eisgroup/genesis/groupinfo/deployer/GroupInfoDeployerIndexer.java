/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.groupinfo.deployer;

import com.eisgroup.genesis.factory.json.IdentifiableEntity;
import com.eisgroup.genesis.factory.json.ModelRootEntity;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.repository.ModeledEntitySchemaResolver;
import com.eisgroup.genesis.factory.repository.links.VersionRoot;
import com.eisgroup.genesis.json.key.BaseKey;
import com.eisgroup.genesis.json.link.EntityLink;
import com.eisgroup.genesis.json.link.EntityLinkBuilderRegistry;
import com.eisgroup.genesis.json.link.LinkingParams;
import com.eisgroup.genesis.model.Variation;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.search.SearchIndex;
import com.eisgroup.genesis.search.SearchIndexDocument;
import com.eisgroup.genesis.search.SearchIndexQuery;
import com.eisgroup.genesis.search.SearchIndexStatementFactory;
import io.reactivex.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.eisgroup.genesis.search.ModeledEntitySearchConstants.FIELD_MODEL_NAME;
import static com.eisgroup.genesis.search.ModeledEntitySearchConstants.FIELD_URI;

/**
 * 
 * @author Dmitry Andronchik
 * @since 10.10
 */
public class GroupInfoDeployerIndexer {

    private final Logger logger = LoggerFactory.getLogger(GroupInfoDeployer.class);

    private final ModelRepository<DomainModel> domainModelRepo = ModelRepositoryFactory
            .getRepositoryFor(DomainModel.class);

    @Autowired
    private EntityLinkBuilderRegistry linkBuilderRegistry;

    @Autowired
    private SearchIndex searchIndex;

    @Autowired
    private SearchIndexStatementFactory statementFactory;

    public void updateIndex(ModelRootEntity entity) {
        DomainModel model = domainModelRepo.getModel(entity.getModelName(), entity.getModelVersion());
        BaseKey key = ((IdentifiableEntity) entity).getKey();

        String searchSchema = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);

        logger.info("Indexing {} {} rev. {}", entity.getClass().getSimpleName(), key.getRootId(), key.getRevisionNo());

        EntityLink<Object> link = linkBuilderRegistry.getByType(entity.getClass()).createLink(entity,
                LinkingParams.just(VersionRoot.class));
        SearchIndexDocument.Builder docBuilder = statementFactory.document(searchSchema).entity(entity).attr(FIELD_URI,
                link.getURIString());
        searchIndex.index(docBuilder.build());
    }

    public Completable deleteFromIndex(ModelRootEntity entity) {
        DomainModel model = domainModelRepo.getModel(entity.getModelName(), entity.getModelVersion());
        return Completable.fromRunnable(() -> {
            BaseKey key = ((IdentifiableEntity) entity).getKey();
            logger.debug("Indexing entity {} rev. {}", key.getRootId(), key.getRevisionNo());
            String searchSchema = ModeledEntitySchemaResolver.getSearchSchemaNameUsing(model, Variation.INVARIANT);

            logger.debug("Deleting {} {} from index rev. {}", entity.getClass().getSimpleName(), key.getRootId(),
                    key.getRevisionNo());
            SearchIndexQuery.Builder deleteBuilder = statementFactory.query(searchSchema)
                    .matches(FIELD_MODEL_NAME, model.getName()).matches(BaseKey.ROOT_ID, key.getRootId());
            searchIndex.delete(deleteBuilder.build());
        });
    }
}
