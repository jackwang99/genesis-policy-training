/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.crm.groupinfo;

import com.eisgroup.genesis.commands.services.CrmValidationService;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static com.eisgroup.genesis.test.matchers.HasPropertyWithValueMatcher.hasDefaultPropertyValue;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.eisgroup.genesis.commands.request.GroupInfoWriteRequest;
import com.eisgroup.genesis.crm.repository.CrmReadRepository;
import com.eisgroup.genesis.dimension.modeling.utils.EntityDimensionsUpdateValidator;
import com.eisgroup.genesis.dimension.search.error.DimensionFilteringErrorDefinition;
import com.eisgroup.genesis.exception.ErrorHolderBuilder;
import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.modeling.types.immutable.Dimension;
import com.eisgroup.genesis.factory.modeling.types.immutable.DimensionValue;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.generator.EntityNumberGenerator;
import com.eisgroup.genesis.factory.modeling.types.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.GroupInfo;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.security.dimension.AccessContextHolder;
import com.eisgroup.genesis.security.dimension.DimensionInfo;
import com.eisgroup.genesis.security.dimension.UserAccessContext;
import com.eisgroup.genesis.test.utils.JsonUtils;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import org.powermock.reflect.Whitebox;

/**
 * @author Dmitry Andronchik
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AccessContextHolder.class })
public class GroupInfoWriteHandlerTest {

    private static final String GROUP_INFO_WITH_DIMENSIONS_JSON = "groupInfoWithDimensions.json";
    
    private static final String QA_USER_NAME = "qa";
    private static final String MODEL_NAME = "GroupInfo";
    private static final String MODEL_VERSION = "1";
    
    private static final String AGENCY_CODE = "agency";
    private static final String AGENCY_FIELD = "agency";
    private static final String AGENCY_A = "agencyA";
    private static final String AGENCY_B = "agencyB";
    private static final String AGENCY_C = "agencyC";

    @InjectMocks
    private WriteHandler writeHandler;
    @Mock
    private CrmReadRepository groupInfoReadRepo;
    @Mock
    private ModelResolver modelResolver;
    @Mock
    private EntityDimensionsUpdateValidator entityDimensionsUpdateValidator;
    @Mock
    private EntityNumberGenerator groupInfoIdGenerator;
    @Mock
    protected AccessTrackInfoService accessTrackInfoService;    
    
    private CrmValidationService crmValidationService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        mockStatic(AccessContextHolder.class);
        
        ModelRepository<DomainModel> repo = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);
        when(modelResolver.resolveModel(DomainModel.class)).thenReturn(repo.getActiveModel(MODEL_NAME));
        when(modelResolver.getModelName()).thenReturn(MODEL_NAME);
        when(modelResolver.getModelVersion()).thenReturn(MODEL_VERSION);
        
        crmValidationService = mock(CrmValidationService.class);
        when(crmValidationService.validateKraken(Mockito.any(), Mockito.any())).thenReturn(Observable.empty());

        Whitebox.setInternalState(writeHandler, "validationService", crmValidationService);
        
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
              Object[] args = invocation.getArguments();
              AccessTrackInfo accessTrackInfo = (AccessTrackInfo) args[0];
              accessTrackInfo.setCreatedBy(QA_USER_NAME);
              accessTrackInfo.setCreatedOn(LocalDateTime.now());
              accessTrackInfo.setUpdatedBy(QA_USER_NAME);
              accessTrackInfo.setUpdatedOn(LocalDateTime.now());
              return null;
           }
          }).when(accessTrackInfoService).fillIn(Mockito.any());        
        
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
              Object[] args = invocation.getArguments();
              AccessTrackInfo accessTrackInfo = (AccessTrackInfo) args[0];
              accessTrackInfo.setUpdatedBy(QA_USER_NAME);
              accessTrackInfo.setUpdatedOn(LocalDateTime.now());
              return null;
           }
          }).when(accessTrackInfoService).update(Mockito.any());        
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteForNewGroupInfo() {

        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        GroupInfo prevVersionGroupInfo = (GroupInfo) ModelInstanceFactory.createRootInstance(MODEL_NAME, MODEL_VERSION);
        
        String groupInfoId = RandomStringUtils.randomAlphanumeric(10);
        when(groupInfoIdGenerator.generate(Mockito.anyString())).thenReturn(groupInfoId);        
        
        GroupInfo result = writeHandler.execute(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .assertComplete()
                .assertNoErrors()
                .values()
                .get(0);

        Assert.assertEquals(3, result.getDimensions().size());
        Assert.assertThat(result.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_A))),
                allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B))), allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_C)))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRemoveHiddenDimension() {

        GroupInfo prevVersionGroupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A, AGENCY_B))));

        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        groupInfo.getDimensions().removeIf(dim -> dim.getAgency().equals(AGENCY_C));

        GroupInfo result = writeHandler.execute(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .values()
                .get(0);

        Assert.assertEquals(3, result.getDimensions().size());
        Assert.assertThat(result.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_A))),
                allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B))), allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_C)))));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRemoveUserDimension() {

        GroupInfo prevVersionGroupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A, AGENCY_B))));

        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        groupInfo.getDimensions().removeIf(dim -> dim.getAgency().equals(AGENCY_A));

        GroupInfo result = writeHandler.execute(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .values()
                .get(0);

        Assert.assertEquals(2, result.getDimensions().size());
        Assert.assertThat(result.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B))),
                                                            allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_C)))));
    }    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRemoveHiddenAndUserDimension() {

        GroupInfo prevVersionGroupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A, AGENCY_B))));

        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        groupInfo.getDimensions().removeIf(dim -> dim.getAgency().equals(AGENCY_A) || dim.getAgency().equals(AGENCY_C));

        GroupInfo result = writeHandler.execute(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .values()
                .get(0);

        Assert.assertEquals(2, result.getDimensions().size());
        Assert.assertThat(result.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B))),
                                                            allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_C)))));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidateAsyncForNewGroupInfoDontHaveAllDimensions() {

        GroupInfo prevVersionGroupInfo = (GroupInfo) ModelInstanceFactory.createRootInstance(MODEL_NAME, MODEL_VERSION);
        
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A, AGENCY_B))));
        when(entityDimensionsUpdateValidator.validateDimensionsUpdate(Mockito.any(), Mockito.any()))
                .thenReturn(Observable.just(ErrorHolderBuilder.fromDefinition(DimensionFilteringErrorDefinition.ACCESS_DENIED).build()));        
        
        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(crmValidationService.validateEntity(groupInfo)).thenReturn(Observable.empty());
        when(crmValidationService.validateStateMachineValue(groupInfo)).thenReturn(Observable.empty());

        writeHandler.validateAsync(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .assertComplete()
                .assertNoErrors()                
                .assertValueCount(1);
        
        ArgumentCaptor<GroupInfo> argumentCaptor = ArgumentCaptor.forClass(GroupInfo.class);
        verify(entityDimensionsUpdateValidator).validateDimensionsUpdate(Mockito.any(), argumentCaptor.capture());
        GroupInfo capturedArgument = argumentCaptor.getValue();
        Assert.assertThat(capturedArgument.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_A))),
                                                                     allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B))),
                                                                     allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_C)))));
    }    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidateAsyncForNewGroupInfoHasAllDimensions() {

        GroupInfo prevVersionGroupInfo = (GroupInfo) ModelInstanceFactory.createRootInstance(MODEL_NAME, MODEL_VERSION);
        
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A, AGENCY_B, AGENCY_C))));
        when(entityDimensionsUpdateValidator.validateDimensionsUpdate(Mockito.any(), Mockito.any()))
                .thenReturn(Observable.empty());
        
        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(crmValidationService.validateEntity(groupInfo)).thenReturn(Observable.empty());
        when(crmValidationService.validateStateMachineValue(groupInfo)).thenReturn(Observable.empty());

        writeHandler.validateAsync(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .assertComplete()
                .assertNoErrors()                
                .assertNoValues();
        
        ArgumentCaptor<GroupInfo> argumentCaptor = ArgumentCaptor.forClass(GroupInfo.class);
        verify(entityDimensionsUpdateValidator).validateDimensionsUpdate(Mockito.any(), argumentCaptor.capture());
        GroupInfo capturedArgument = argumentCaptor.getValue();
        Assert.assertThat(capturedArgument.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_A))),
                                                                allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B))), 
                                                                allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_C)))));
    }    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidateAsyncForUpdateGroupInfoRemoveUserDimension() {

        GroupInfo prevVersionGroupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A, AGENCY_B))));
        when(entityDimensionsUpdateValidator.validateDimensionsUpdate(Mockito.any(), Mockito.any()))
                .thenReturn(Observable.empty());
        
        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(crmValidationService.validateEntity(groupInfo)).thenReturn(Observable.empty());
        groupInfo.getDimensions().removeIf(dim -> dim.getAgency().equals(AGENCY_A));
        when(crmValidationService.validateStateMachineValue(groupInfo)).thenReturn(Observable.empty());

        writeHandler.validateAsync(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .assertComplete()
                .assertNoErrors()                
                .assertNoValues();
        
        ArgumentCaptor<GroupInfo> argumentCaptor = ArgumentCaptor.forClass(GroupInfo.class);
        verify(entityDimensionsUpdateValidator).validateDimensionsUpdate(Mockito.any(), argumentCaptor.capture());
        GroupInfo capturedArgument = argumentCaptor.getValue();
        Assert.assertThat(capturedArgument.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B)))));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidateAsyncForUpdateGroupInfoRemoveHiddenDimension() {

        GroupInfo prevVersionGroupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A, AGENCY_B))));
        when(entityDimensionsUpdateValidator.validateDimensionsUpdate(Mockito.any(), Mockito.any()))
                .thenReturn(Observable.empty());
        
        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(crmValidationService.validateEntity(groupInfo)).thenReturn(Observable.empty());
        groupInfo.getDimensions().removeIf(dim -> dim.getAgency().equals(AGENCY_C));
        when(crmValidationService.validateStateMachineValue(groupInfo)).thenReturn(Observable.empty());

        writeHandler.validateAsync(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .assertComplete()
                .assertNoErrors()                
                .assertNoValues();
        
        ArgumentCaptor<GroupInfo> argumentCaptor = ArgumentCaptor.forClass(GroupInfo.class);
        verify(entityDimensionsUpdateValidator).validateDimensionsUpdate(Mockito.any(), argumentCaptor.capture());
        GroupInfo capturedArgument = argumentCaptor.getValue();
        Assert.assertThat(capturedArgument.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_A))),
                                                                     allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B)))));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidateAsyncForUpdateGroupInfoAddHiddenDimension() {

        GroupInfo prevVersionGroupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        prevVersionGroupInfo.getDimensions().removeIf(dim -> dim.getAgency().equals(AGENCY_B));
        
        when(AccessContextHolder.getUseAccessContext())
                .then(args -> userAccessContext(mockDimension(AGENCY_CODE, Arrays.asList(AGENCY_A))));
        when(entityDimensionsUpdateValidator.validateDimensionsUpdate(Mockito.any(), Mockito.any()))
                .thenReturn(Observable.just(ErrorHolderBuilder.fromDefinition(DimensionFilteringErrorDefinition.ACCESS_DENIED).build()));
        
        GroupInfo groupInfo = initGroupInfo(GROUP_INFO_WITH_DIMENSIONS_JSON);
        when(crmValidationService.validateEntity(groupInfo)).thenReturn(Observable.empty());
        when(crmValidationService.validateStateMachineValue(groupInfo)).thenReturn(Observable.empty());
        
        writeHandler.validateAsync(new GroupInfoWriteRequest(groupInfo), prevVersionGroupInfo)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(1);
        
        ArgumentCaptor<GroupInfo> argumentCaptor = ArgumentCaptor.forClass(GroupInfo.class);
        verify(entityDimensionsUpdateValidator).validateDimensionsUpdate(Mockito.any(), argumentCaptor.capture());
        GroupInfo capturedArgument = argumentCaptor.getValue();
        Assert.assertThat(capturedArgument.getDimensions(), contains(allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_A))),
                                                                     allOf(hasDefaultPropertyValue(AGENCY_FIELD, is(AGENCY_B)))));        
    }    
    

    private GroupInfo initGroupInfo(String jsonPath) {
        JsonObject json = JsonUtils.load(jsonPath);
        return (GroupInfo) ModelInstanceFactory.createInstance(json);
    }

    private UserAccessContext userAccessContext(Dimension... dimensions) {
        List<DimensionInfo> dimensionInfos = Arrays.stream(dimensions)
                .map(dim -> new DimensionInfo(dim.getDimensionCd(),
                        dim.getDimensionValues().stream().map(val -> val.getDimensionValue())
                                .collect(Collectors.toList()),
                        BooleanUtils.toBoolean(dim.getUnrestricted())))
                .collect(Collectors.toList());
        return new UserAccessContext(dimensionInfos);
    }

    private Dimension mockDimension(String dimensionCd, Collection<String> values) {
        Dimension dimension = Mockito.mock(Dimension.class);
        when(dimension.getDimensionCd()).thenReturn(dimensionCd);
        when(dimension.getUnrestricted()).thenReturn(false);
        List<DimensionValue> dimensionValues = mockDimensionValues(values);
        when(dimension.getDimensionValues()).thenReturn(dimensionValues);
        return dimension;
    }

    private static List<DimensionValue> mockDimensionValues(Collection<String> values) {
        return values.stream().map(stringValue -> {
            DimensionValue dimensionValue = Mockito.mock(DimensionValue.class);
            when(dimensionValue.getDimensionValue()).thenReturn(stringValue);
            return dimensionValue;
        }).collect(Collectors.toList());
    }
}
