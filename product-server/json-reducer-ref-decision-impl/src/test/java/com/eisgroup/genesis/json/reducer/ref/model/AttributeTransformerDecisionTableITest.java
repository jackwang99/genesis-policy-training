/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.json.reducer.ref.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.eisgroup.genesis.columnstore.ColumnStore;
import com.eisgroup.genesis.columnstore.InMemoryColumnStoreConfig;
import com.eisgroup.genesis.columnstore.statement.StatementBuilderFactory;
import com.eisgroup.genesis.columnstore.statement.WriteStatement;
import com.eisgroup.genesis.decision.DecisionService;
import com.eisgroup.genesis.decision.config.DecisionServiceConfig;
import com.eisgroup.genesis.decision.dimension.config.DecisionDimensionConfig;
import com.eisgroup.genesis.decision.dsl.model.DecisionModel;
import com.eisgroup.genesis.decision.engine.kel.KelExpressionLanguageConfig;
import com.eisgroup.genesis.decision.repository.config.DecisionTableRepositoryConfig;
import com.eisgroup.genesis.decision.repository.entity.DecisionTableDataEntity;
import com.eisgroup.genesis.decision.repository.entity.DecisionTableSchema;
import com.eisgroup.genesis.decision.repository.entity.RowEntity;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import com.eisgroup.genesis.product.specification.traverser.TransformationInput;
import com.eisgroup.genesis.repository.RepositoryItest;
import com.eisgroup.genesis.spring.config.SpringCommonsConfig;
import com.eisgroup.genesis.test.IntegrationTests;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


import static org.hamcrest.CoreMatchers.is;

/**
 * Integration test for {@link AttributeTransformerDecisionTable}
 *
 * @author ssauchuk
 * @since 10.2
 */
@ContextConfiguration(classes = {
        SpringCommonsConfig.class,
        DecisionTableRepositoryConfig.class,
        DecisionServiceConfig.class,
        DecisionDimensionConfig.class,
        KelExpressionLanguageConfig.class,
        InMemoryColumnStoreConfig.class,
        RepositoryItest.Config.class
})
@Category(IntegrationTests.class)
public class AttributeTransformerDecisionTableITest extends AbstractJUnit4SpringContextTests {

    private static final String ATTRIBUTE_NAME = "attributeName";

    private static final String COMPONENT_COLUMN_NAME = "Component";

    private static final String ATTRIBUTE_COLUMN_NAME = "Attribute";

    private static final String TAG_COLUMN_NAME = "Tag";

    private static final String ALIAS_COLUMN_NAME = "Alias";

    public static final String ENTITY_DESIGN_NAME = "PolicyAuto";

    @Autowired
    private DecisionService decisionService;

    @Autowired
    private RepositoryItest repositoryItest;

    @Autowired
    private ColumnStore columnStore;

    @Autowired
    private StatementBuilderFactory statementBuilderFactory;

    @Autowired
    private DecisionTableSchema decisionTableSchema;

    private ModelRepository<DecisionModel> decisionModelRepository = ModelRepositoryFactory
            .getRepositoryFor(DecisionModel.class);

    private DecisionModel decisionModel;

    private AttributeTransformerDecisionTable attributeTransformerDecisionTable;

    @Before
    public void setUp() {
        repositoryItest.setUp(DecisionTableDataEntity.class);
        decisionModel = decisionModelRepository.getModel(AttributeTransformerDecisionTable.DECISION_MODEL_NAME,
                AttributeTransformerDecisionTable.DECISION_MODEL_VERSION);

        attributeTransformerDecisionTable = new AttributeTransformerDecisionTable(decisionService);

        persistTableData(getAttributeNameTransformationTableData());
    }

    @After
    public void onTearDown() {
        repositoryItest.tearDown();
    }

    @Test
    public void shouldReturnSameAttributeNameIfContextWithoutBehaviour() {
        TransformationInput transformationInput = new TransformationInput(ENTITY_DESIGN_NAME, ATTRIBUTE_NAME);
        TransformationExecutionContext executionContext = new TransformationExecutionContext(null, null);

        String newAttrName = attributeTransformerDecisionTable.apply(executionContext, transformationInput);

        Assert.assertThat(newAttrName, is(ATTRIBUTE_NAME));
    }

    @Test
    public void shouldReturnAliasedAttributeName() {
        TransformationInput transformationInput = new TransformationInput(ENTITY_DESIGN_NAME, ATTRIBUTE_NAME);
        TransformationExecutionContext executionContext = new TransformationExecutionContext(null, "Rateable");

        String newAttrName = attributeTransformerDecisionTable.apply(executionContext, transformationInput);

        Assert.assertThat(newAttrName, is("newAttributeName"));
    }

    @Test
    public void shouldReturnSameAttributeNameIfAnyMatches() {
        TransformationInput transformationInput = new TransformationInput("PolicyCL", ATTRIBUTE_NAME);
        TransformationExecutionContext executionContext = new TransformationExecutionContext(null, "Rateable");

        String newAttrName = attributeTransformerDecisionTable.apply(executionContext, transformationInput);

        Assert.assertThat(newAttrName, is(ATTRIBUTE_NAME));
    }

    @Test
    public void shouldReturnAliasedAttributeNameIfAttributeApplicableForSeveralTags() {
        TransformationInput transformationInput = new TransformationInput("PolicyAuto", "attributeTwo");
        TransformationExecutionContext executionContext = new TransformationExecutionContext(null, "Finance");

        String newAttrName = attributeTransformerDecisionTable.apply(executionContext, transformationInput);

        Assert.assertThat(newAttrName, is("newAttributeTwo"));
    }

    private Collection<RowEntity> getAttributeNameTransformationTableData() {
        List<String> columns = ImmutableList
                .of(COMPONENT_COLUMN_NAME, ATTRIBUTE_COLUMN_NAME, TAG_COLUMN_NAME, ALIAS_COLUMN_NAME);

        List<List<String>> rows = ImmutableList
                .of(ImmutableList.of("'PolicyAuto'", "'attributeName'", "'Rateable'", "'newAttributeName'"),
                        ImmutableList.of("'PolicyAuto'", "'attributeTwo'", "'Rateable','Finance'", "'newAttributeTwo'"),
                        ImmutableList.of("'PolicyHome'", "'attributeName'", "'Rateable'", "'newAttributeName1'"));

        return buildTableData(columns, rows);
    }

    protected Collection<RowEntity> buildTableData(List<String> columns, List<List<String>> rows) {
        Collection<RowEntity> tableRows = new ArrayList<>();
        for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
            Map<String, String> columnData = new HashMap<>();
            List<String> row = rows.get(rowIdx);
            for (int columnIdx = 0; columnIdx < columns.size(); columnIdx++) {
                columnData.put(columns.get(columnIdx), row.get(columnIdx));
            }
            tableRows.add(new RowEntity(columnData));
        }
        return tableRows;
    }

    private void persistTableData(Collection<RowEntity> rows) {
        DecisionTableDataEntity dataEntity =
                new DecisionTableDataEntity(AttributeTransformerDecisionTable.TABLE_NAME, decisionModel.getName(), rows);
        WriteStatement<DecisionTableDataEntity> ws = statementBuilderFactory.write(DecisionTableDataEntity.class)
                .entity(dataEntity).build();
        columnStore.execute(decisionTableSchema.getSchemaName(), ws);
    }

}