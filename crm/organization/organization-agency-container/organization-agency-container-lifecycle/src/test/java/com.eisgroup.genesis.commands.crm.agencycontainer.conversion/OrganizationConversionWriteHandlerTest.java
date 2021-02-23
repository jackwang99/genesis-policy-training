/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.commands.crm.agencycontainer.conversion;

import com.eisgroup.genesis.factory.core.ModelInstanceFactory;
import com.eisgroup.genesis.factory.model.domain.DomainModel;
import com.eisgroup.genesis.factory.modeling.types.AccessTrackInfo;
import com.eisgroup.genesis.factory.modeling.types.AccessTrackableEntity;
import com.eisgroup.genesis.factory.services.AccessTrackInfoService;
import com.eisgroup.genesis.commands.crm.agencycontainer.conversion.OrganizationConversionWriteHandler;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.model.repo.ModelRepository;
import com.eisgroup.genesis.model.repo.ModelRepositoryFactory;
import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

/**
 * @author Vadim Koniukh
 */


public class OrganizationConversionWriteHandlerTest {


    @Mock
    private ModelResolver modelResolver;

    @Mock
    private AccessTrackInfoService accessTrackInfoService;

    @InjectMocks
    private OrganizationConversionWriteHandler writeHandler = new OrganizationConversionWriteHandler();

    private AccessTrackableEntity loaded;

    private AccessTrackableEntity current;

    private LocalDateTime dateTime = LocalDateTime.now();

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        loaded = (AccessTrackableEntity) ModelInstanceFactory.createRootInstance(getModelName(), getModelVersion());
        current = (AccessTrackableEntity) ModelInstanceFactory.createRootInstance(getModelName(), getModelVersion());

        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                AccessTrackInfo accessTrackInfo = (AccessTrackInfo) args[0];
                accessTrackInfo.setCreatedBy("qa");
                accessTrackInfo.setCreatedOn(dateTime);
                accessTrackInfo.setUpdatedBy("qa");
                accessTrackInfo.setUpdatedOn(dateTime);
                return null;
            }
        }).when(accessTrackInfoService).fillIn(Mockito.any());

        ModelRepository<DomainModel> repo = ModelRepositoryFactory.getRepositoryFor(DomainModel.class);
        when(modelResolver.resolveModel(DomainModel.class)).thenReturn(repo.getActiveModel(getModelName()));
        when(modelResolver.getModelName()).thenReturn(getModelName());
        when(modelResolver.getModelVersion()).thenReturn(getModelVersion());
    }

    private String getModelName() {
        return "OrganizationAgencyContainer";
    }

    private String getModelVersion() {
        return "1";
    }

    @Test
    public void accessTrackInfoLoadedEmptyCreatedOnNullUpdatedOnNull(){

        AccessTrackInfo accessTrackInfo = null;
        loaded.setAccessTrackInfo(accessTrackInfo);
        current.setAccessTrackInfo(accessTrackInfo);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
    }

    @Test
    public void accessTrackInfoLoadedEmptyCreatedOnNullUpdatedOnExist(){

        AccessTrackInfo currentAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);
        LocalDateTime dateTime = LocalDateTime.now();
        currentAccessTrackInfo.setUpdatedOn(dateTime.plusDays(1));
        current.setAccessTrackInfo(currentAccessTrackInfo);
        loaded.setAccessTrackInfo(null);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedOn().getDayOfYear(), dateTime.plusDays(1).getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedOn().getDayOfYear(), dateTime.getDayOfYear());
    }

    @Test
    public void accessTrackInfoLoadedEmptyCreatedOnExistUpdatedOnNull(){

        AccessTrackInfo currentAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);
        currentAccessTrackInfo.setCreatedOn(dateTime.minusDays(1));
        current.setAccessTrackInfo(currentAccessTrackInfo);
        loaded.setAccessTrackInfo(null);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedOn().getDayOfYear(), dateTime.minusDays(1).getDayOfYear());
    }

    @Test
    public void accessTrackInfoLoadedEmptyCreatedOnExistUpdatedOnExist(){

        AccessTrackInfo currentAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);

        currentAccessTrackInfo.setCreatedOn(dateTime.plusDays(1));
        currentAccessTrackInfo.setUpdatedOn(dateTime.plusDays(1));
        current.setAccessTrackInfo(currentAccessTrackInfo);
        loaded.setAccessTrackInfo(null);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedOn().getDayOfYear(), dateTime.plusDays(1).getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedOn().getDayOfYear(), dateTime.plusDays(1).getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedBy(), "qa");
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedBy(), "qa");
    }


    @Test
    public void accessTrackInfoLoadedExistCreatedOnNullUpdatedOnNull(){

        AccessTrackInfo loadedAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);
        loadedAccessTrackInfo.setCreatedOn(dateTime);
        loadedAccessTrackInfo.setUpdatedOn(dateTime);
        loadedAccessTrackInfo.setUpdatedBy("qa");
        loadedAccessTrackInfo.setCreatedBy("qa");

        loaded.setAccessTrackInfo(loadedAccessTrackInfo);
        current.setAccessTrackInfo(null);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedOn(), loaded.getAccessTrackInfo().getUpdatedOn());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedOn(), loaded.getAccessTrackInfo().getCreatedOn());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedBy(), loaded.getAccessTrackInfo().getCreatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedBy(), loaded.getAccessTrackInfo().getUpdatedBy());
    }

    @Test
    public void accessTrackInfoLoadedExistCreatedOnNullUpdatedOnExist(){

        AccessTrackInfo loadedAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);
        AccessTrackInfo currentAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);

        loadedAccessTrackInfo.setCreatedOn(dateTime);
        loadedAccessTrackInfo.setUpdatedOn(dateTime);
        loadedAccessTrackInfo.setCreatedBy("qa");
        loadedAccessTrackInfo.setUpdatedBy("qa");

        currentAccessTrackInfo.setCreatedOn(null);
        currentAccessTrackInfo.setUpdatedOn(dateTime.plusDays(1));

        loaded.setAccessTrackInfo(loadedAccessTrackInfo);
        current.setAccessTrackInfo(currentAccessTrackInfo);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedOn().getDayOfYear(), dateTime.plusDays(1).getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedOn(), loaded.getAccessTrackInfo().getCreatedOn());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedBy(), loaded.getAccessTrackInfo().getCreatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedBy(), loaded.getAccessTrackInfo().getUpdatedBy());
    }

    @Test
    public void accessTrackInfoLoadedExistCreatedOnExistUpdatedOnNull(){

        AccessTrackInfo loadedAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);
        AccessTrackInfo currentAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);

        loadedAccessTrackInfo.setCreatedOn(dateTime);
        loadedAccessTrackInfo.setUpdatedOn(dateTime);
        loadedAccessTrackInfo.setCreatedBy("qa");
        loadedAccessTrackInfo.setUpdatedBy("qa");
        currentAccessTrackInfo.setCreatedOn(dateTime.plusDays(1));

        loaded.setAccessTrackInfo(loadedAccessTrackInfo);
        current.setAccessTrackInfo(currentAccessTrackInfo);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedOn().getDayOfYear(), dateTime.plusDays(1).getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedOn().getDayOfYear(), dateTime.getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedBy(), loaded.getAccessTrackInfo().getCreatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedBy(), loaded.getAccessTrackInfo().getUpdatedBy());
    }

    @Test
    public void accessTrackInfoLoadedExistCreatedOnExistUpdatedOnExist(){

        AccessTrackInfo loadedAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);
        AccessTrackInfo currentAccessTrackInfo = ModelInstanceFactory.createInstanceByBusinessType(getModelName(), getModelVersion(), AccessTrackInfo.NAME);

        loadedAccessTrackInfo.setCreatedOn(dateTime);
        loadedAccessTrackInfo.setUpdatedOn(dateTime);
        loadedAccessTrackInfo.setCreatedBy("qa");
        loadedAccessTrackInfo.setUpdatedBy("qa");
        currentAccessTrackInfo.setCreatedOn(dateTime.plusDays(1));
        currentAccessTrackInfo.setUpdatedOn(dateTime.plusDays(1));

        loaded.setAccessTrackInfo(loadedAccessTrackInfo);
        current.setAccessTrackInfo(currentAccessTrackInfo);

        writeHandler.copyAcessTrackInfo(loaded, current);

        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedOn());
        Assert.assertNotNull(current.getAccessTrackInfo().getCreatedBy());
        Assert.assertNotNull(current.getAccessTrackInfo().getUpdatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedOn().getDayOfYear(), dateTime.plusDays(1).getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedOn().getDayOfYear(), dateTime.plusDays(1).getDayOfYear());
        Assert.assertEquals(current.getAccessTrackInfo().getCreatedBy(), loaded.getAccessTrackInfo().getCreatedBy());
        Assert.assertEquals(current.getAccessTrackInfo().getUpdatedBy(), loaded.getAccessTrackInfo().getUpdatedBy());
    }

}
