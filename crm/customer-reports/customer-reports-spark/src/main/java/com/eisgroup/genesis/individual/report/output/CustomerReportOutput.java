/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws. 
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.eisgroup.genesis.individual.report.output;

import static com.eisgroup.genesis.report.util.DateUtils.toSqlDate;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eisgroup.genesis.factory.model.individualcustomer.immutable.GenesisCrmEmploymentDetails;
import com.eisgroup.genesis.factory.model.individualcustomer.immutable.GenesisCrmIndividualDetails;
import com.eisgroup.genesis.factory.model.individualcustomer.immutable.GenesisCrmPerson;
import com.eisgroup.genesis.factory.model.individualcustomer.immutable.IndividualCustomer;
import com.eisgroup.genesis.factory.model.organizationcustomer.immutable.GenesisCrmBusinessDetails;
import com.eisgroup.genesis.factory.model.organizationcustomer.immutable.GenesisCrmLegalEntity;
import com.eisgroup.genesis.factory.model.organizationcustomer.immutable.OrganizationCustomer;
import com.eisgroup.genesis.factory.modeling.types.immutable.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.CrmCommunicationInfo;
import com.eisgroup.genesis.factory.modeling.types.immutable.ProductOwned;
import com.eisgroup.genesis.individual.report.input.AgencyContainerRow;
import com.eisgroup.genesis.individual.report.input.IndividualCustomerRow;
import com.eisgroup.genesis.individual.report.input.OrganizationCustomerRow;
import com.eisgroup.genesis.report.ReportOutput.ReportRoot;
import com.eisgroup.genesis.report.ReportOutput.TableName;
import com.eisgroup.genesis.report.json.JsonObjectDelegate;

/**
 * Hive structure for customer report output (root table)
 *
 * @author azukovskij
 */
@TableName("Customer")
public class CustomerReportOutput extends ReportRoot {

    private static final long serialVersionUID = -4747998699571186033L;

    private String state;
    private String customerNumber;
    private String preferredContactMethod;
    private String brandCd;
    private String preferredWrittenLanguage;
    private Boolean deathNotificationReceived;
    private String fullName;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String maritalStatus;
    private String genderCd;
    private String source;
    private Timestamp createdOn;

    private List<CrmPhoneOutput> phones = Collections.emptyList();
    private List<CrmEmailOutput> emails = Collections.emptyList();
    private List<CrmAddressOutput> adresses = Collections.emptyList();
    private List<CrmEmploymentDetailsOutput> employmentDetails = Collections.emptyList();
    private List<CrmProductsOwnedOutput> productsOwned = Collections.emptyList();
    private List<CrmAgencyContainer> agencyContainers = Collections.emptyList();

    public CustomerReportOutput() {
    }

    public CustomerReportOutput(IndividualCustomerRow row, List<AgencyContainerRow> containers) {
        this(row.getRootId(), row.getRevisionNo(),
                row.getState(), row.getCustomerNumber(), row.getBrandCd(), row.getSource(), row.getAccessTrackInfo(),
                row.getTimestamp(), row.getCommunicationInfo().getEntity(), row.getProductsOwned().getEntityList(),
                containers, row.getDetails().getEntity(),
                row.getEmploymentDetails().getEntityList());
    }

    public CustomerReportOutput(OrganizationCustomerRow row, List<AgencyContainerRow> containers) {
        this(row.getRootId(), row.getRevisionNo(),
                row.getState(), row.getCustomerNumber(), row.getBrandCd(), row.getSource(), row.getAccessTrackInfo(),
                row.getTimestamp(), row.getCommunicationInfo().getEntity(), row.getProductsOwned().getEntityList(),
                containers, row.getDetails().getEntity());
    }

    public CustomerReportOutput(IndividualCustomer entity) {
        this(String.valueOf(entity.getKey().getRootId()), entity.getKey().getRevisionNo(),
                entity.getState(), entity.getCustomerNumber(), entity.getBrandCd(), entity.getSource(),
                new JsonObjectDelegate<>(AccessTrackInfo.class, entity.getAccessTrackInfo()),
                getTimestamp(entity),
                entity.getCommunicationInfo(), entity.getProductsOwned(), Collections.emptyList(),
                entity.getDetails(),
                entity.getEmploymentDetails());
    }

    public CustomerReportOutput(OrganizationCustomer entity) {
        this(String.valueOf(entity.getKey().getRootId()), entity.getKey().getRevisionNo(),
                entity.getState(), entity.getCustomerNumber(), entity.getBrandCd(), entity.getSource(),
                new JsonObjectDelegate<>(AccessTrackInfo.class, entity.getAccessTrackInfo()), getTimestamp(entity),
                entity.getCommunicationInfo(), entity.getProductsOwned(), Collections.emptyList(),
                entity.getDetails());
    }

    @SuppressWarnings("squid:S00107")
    private CustomerReportOutput(String rootId, int revisionNo,
                                 String state, String customerNumber, String brandCd, String source,
                                 JsonObjectDelegate<AccessTrackInfo> accessTrackInfo, java.util.Date timestamp,
                                 CrmCommunicationInfo communicationInfo,
                                 Collection<? extends ProductOwned> productsOwned,
                                 List<AgencyContainerRow> agencyContainers,
                                 GenesisCrmBusinessDetails details) {
        this(rootId, revisionNo,
                state, customerNumber, brandCd, source, accessTrackInfo, timestamp,
                communicationInfo, productsOwned, agencyContainers);
        if (details != null) {
            this.preferredWrittenLanguage = details.getPreferredWrittenLanguage();

            GenesisCrmLegalEntity legalEntity = details.getLegalEntity();
            if (legalEntity != null) {
                this.fullName = legalEntity.getLegalName();
            }
        }
    }

    @SuppressWarnings("squid:S00107")
    private CustomerReportOutput(String rootId, int revisionNo,
                                 String state, String customerNumber, String brandCd, String source,
                                 JsonObjectDelegate<? extends AccessTrackInfo> accessTrackInfo,
                                 java.util.Date timestamp,
                                 CrmCommunicationInfo communicationInfo,
                                 Collection<? extends ProductOwned> productsOwned,
                                 List<AgencyContainerRow> agencyContainers,
                                 GenesisCrmIndividualDetails details,
                                 Collection<? extends GenesisCrmEmploymentDetails> employmentDetails) {
        this(rootId, revisionNo,
                state, customerNumber, brandCd, source, accessTrackInfo, timestamp,
                communicationInfo, productsOwned, agencyContainers);

        if (details != null) {
            this.preferredWrittenLanguage = details.getPreferredWrittenLanguage();
            this.deathNotificationReceived = details.getDeathNotificationReceived();

            GenesisCrmPerson person = details.getPerson();
            if (person != null) {
                this.fullName = Stream.of(person.getFirstName(), person.getLastName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" "));
                this.firstName = person.getFirstName();
                this.lastName = person.getLastName();
                this.birthDate = toSqlDate(person.getBirthDate());
                this.maritalStatus = person.getMaritalStatus();
                this.genderCd = person.getGenderCd();
            }
        }

        this.employmentDetails = createAggregateList(employmentDetails,
                employmentDetail -> new CrmEmploymentDetailsOutput(this, employmentDetail));
    }

    @SuppressWarnings("squid:S00107")
    private CustomerReportOutput(String rootId, int revisionNo,
                                 String state, String customerNumber, String brandCd, String source,
                                 JsonObjectDelegate<? extends AccessTrackInfo> accessTrackInfo,
                                 java.util.Date timestamp,
                                 CrmCommunicationInfo communicationInfo,
                                 Collection<? extends ProductOwned> productsOwned,
                                 List<AgencyContainerRow> agencyContainers) {
        this.timestamp = Optional.ofNullable(timestamp)
                .map(java.util.Date::getTime)
                .map(Timestamp::new)
                .orElse(null);
        this.rootId = rootId;
        this.revisionNo = revisionNo;

        this.state = state;
        this.customerNumber = customerNumber;
        this.brandCd = brandCd;
        this.source = source;
        if (accessTrackInfo != null && accessTrackInfo.getEntity() != null) {
            this.createdOn = Timestamp.valueOf(accessTrackInfo.getEntity().getCreatedOn());
        }
        this.productsOwned = createAggregateList(productsOwned,
                productOwned -> new CrmProductsOwnedOutput(this, productOwned));
        this.agencyContainers = agencyContainers.stream()
                .map(container -> new CrmAgencyContainer(this, container))
                .collect(Collectors.toList());

        if (communicationInfo != null) {
            this.preferredContactMethod = communicationInfo.getPreferredContactMethod();
            this.phones = createAggregateList(communicationInfo.getPhones(),
                    phone -> new CrmPhoneOutput(this, phone));
            this.emails = createAggregateList(communicationInfo.getEmails(),
                    email -> new CrmEmailOutput(this, email));
            this.adresses = createAggregateList(communicationInfo.getAddresses(),
                    address -> new CrmAddressOutput(this, address));
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBrandCd() {
        return brandCd;
    }

    public String getSource() {
        return source;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getPreferredWrittenLanguage() {
        return preferredWrittenLanguage;
    }

    public Boolean getDeathNotificationReceived() {
        return deathNotificationReceived;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getGenderCd() {
        return genderCd;
    }

    @Override
    public <T extends ReportAggregate> List<T> getAggregates(Class<T> aggregateType) {
        if (CrmPhoneOutput.class.equals(aggregateType)) {
            return (List<T>) phones;
        }
        if (CrmEmailOutput.class.equals(aggregateType)) {
            return (List<T>) emails;
        }
        if (CrmAddressOutput.class.equals(aggregateType)) {
            return (List<T>) adresses;
        }
        if (CrmEmploymentDetailsOutput.class.equals(aggregateType)) {
            return (List<T>) employmentDetails;
        }
        if (CrmProductsOwnedOutput.class.equals(aggregateType)) {
            return (List<T>) productsOwned;
        }
        if (CrmAgencyContainer.class.equals(aggregateType)) {
            return (List<T>) agencyContainers;
        }
        throw new IllegalStateException("Unsupported aggregate type " + aggregateType);
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPreferredContactMethod() {
        return preferredContactMethod;
    }

    public void setPreferredContactMethod(String preferredContactMethod) {
        this.preferredContactMethod = preferredContactMethod;
    }


}
