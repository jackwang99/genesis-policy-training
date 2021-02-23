/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;
import static com.eisgroup.genesis.report.util.CassandraQueries.*;
import static com.eisgroup.genesis.report.util.ScalaFuncs.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;
import com.eisgroup.genesis.factory.model.organizationcustomer.immutable.OrganizationCustomer;
import com.eisgroup.genesis.factory.modeling.types.immutable.OrganizationAgencyContainer;
import com.eisgroup.genesis.individual.report.input.AgencyContainerRow;
import com.eisgroup.genesis.individual.report.input.OrganizationCustomerRow;
import com.eisgroup.genesis.individual.report.output.CrmAddressOutput;
import com.eisgroup.genesis.individual.report.output.CrmAgencyContainer;
import com.eisgroup.genesis.individual.report.output.CrmEmailOutput;
import com.eisgroup.genesis.individual.report.output.CrmPhoneOutput;
import com.eisgroup.genesis.individual.report.output.CrmProductsOwnedOutput;
import com.eisgroup.genesis.individual.report.output.CustomerReportOutput;
import com.eisgroup.genesis.lifecycle.events.CommandExecutedEvent;
import com.eisgroup.genesis.report.ReportOutput.ReportAggregate;
import com.eisgroup.genesis.report.rdf.EntityLinkRow;
import com.eisgroup.genesis.report.rdf.EntityLinkRowResolver;

import scala.Tuple2;

/**
 * Report that extracts organization customer entries into hive
 * 
 * @author azukovskij
 *
 */
public class OrganizationCustomerReport extends AbstractCustomerReport {

    private static final long serialVersionUID = 7212358534664480844L;
    
    public static final String MODEL_NAME = "ORGANIZATIONCUSTOMER";
    private static final String AGENCY_CONTAINER_MODEL_NAME = "OrganizationAgencyContainer";
    
    private EntityLinkRowResolver<OrganizationCustomerRow> customerLinkResolver;
    
    @Override
    public void initialize(JavaSparkContext context) {
        super.initialize(context);
        customerLinkResolver = new EntityLinkRowResolver<>(OrganizationCustomerRow.class, keyspaceName, tableName);
    }

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    protected String getAgencyContainerModelName() {
        return AGENCY_CONTAINER_MODEL_NAME;
    }
    
    @Override
    public Class<? extends ReportAggregate>[] getReportAggregateTypes() {
        return new Class[] { CrmPhoneOutput.class, CrmEmailOutput.class, CrmAddressOutput.class, 
                CrmProductsOwnedOutput.class, CrmAgencyContainer.class };
    }
    
    @Override
    public JavaRDD<CustomerReportOutput> processBatch(JavaSparkContext context, LocalDateTime startingPoint, LocalDateTime endingPoint) {
        // select customers
        CassandraJavaRDD<OrganizationCustomerRow> customerSelect = javaFunctions(context)
                .cassandraTable(keyspaceName, tableName, mapRowTo(OrganizationCustomerRow.class, COLUMN_MAPPINGS))
                .select(OrganizationCustomerRow.COLUMN_NAMES);
        // where
        JavaRDD<OrganizationCustomerRow> customers = whereTimestamp(customerSelect, startingPoint, endingPoint);
        
        return buildReport(customers);
    }

    @Override
    public JavaDStream<CustomerReportOutput> processRealtime(JavaSparkContext context,
            JavaDStream<CommandExecutedEvent> stream) {
        return stream
            .map(CommandExecutedEvent::getOutput)
            .transform(JavaRDD::cache)
            .transform(entityRDD -> {
                // Organization customer created
                JavaRDD<CustomerReportOutput> byCustomerReport = buildReport(entityRDD
                    .filter(OrganizationCustomer.class::isInstance)
                    .map(OrganizationCustomer.class::cast)
                    .map(OrganizationCustomerRow::new));

                // Container created for customer
                JavaRDD<CustomerReportOutput> byContainerReport = buildReportByContainer(entityRDD
                        .filter(OrganizationAgencyContainer.class::isInstance)
                        .map(OrganizationAgencyContainer.class::cast)
                        .map(container -> new EntityLinkRow<>(container.getCustomer().getURIString(), new AgencyContainerRow(container))));
                
                return byCustomerReport.union(byContainerReport);
            });
    }
    
    private JavaRDD<CustomerReportOutput> buildReport(JavaRDD<OrganizationCustomerRow> customers) {
        return fetchAgencyContainerKeys(customers)
            .map(tuple -> new CustomerReportOutput(tuple._1, tuple._2));
    }

    private JavaRDD<CustomerReportOutput> buildReportByContainer(JavaRDD<EntityLinkRow<AgencyContainerRow>> customerLinks) {
        JavaPairRDD<OrganizationCustomerRow, List<AgencyContainerRow>> aggregateByKey = 
                customerLinkResolver.resolveLinks(customerLinks, OrganizationCustomerRow.COLUMN_NAMES)
                    .filter(tuple -> !tuple._2.isEmpty())
                    .mapToPair(tuple -> Tuple2.apply(latestRevision(tuple._2), tuple._1))
                    .aggregateByKey(new ArrayList<>(), consumer(List::add), consumer(List::addAll));
        return aggregateByKey
            .map(tuple -> new CustomerReportOutput(tuple._1, tuple._2));
    }

    
}
