package com.eisgroup.genesis.policy.auto.lifecycle.commands.itests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.eisgroup.genesis.json.JsonEntity;
import com.eisgroup.genesis.policy.core.model.PolicyVariations;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eisgroup.genesis.entity.Stateful;
import com.eisgroup.genesis.lifecycle.statemachine.StateHolder;
import com.eisgroup.genesis.factory.model.personalauto.impl.PersonalAutoFactory;
import com.eisgroup.genesis.factory.modeling.types.RootEntity;
import com.eisgroup.genesis.model.ModelResolver;
import com.eisgroup.genesis.lifecycle.statemachine.TransitionManager;
import com.eisgroup.genesis.lifecycle.statemachine.config.StateMachineTransitionConfig;
import com.eisgroup.genesis.modules.PersonalAutoLifecycleModule;
import com.eisgroup.genesis.streams.publisher.MessagePublisher;
import com.eisgroup.genesis.test.IntegrationTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= { StateMachineConfigurationTest.Config.class, StateMachineTransitionConfig.class })
@Category(IntegrationTests.class)
public class StateMachineConfigurationTest {

    private static final String MODEL_NAME = new PersonalAutoLifecycleModule().getModelName();

    private static final String UNINITIALIZED_STATE = "uninitialized";
    private static final String INITIALIZED_STATE = "initialized";
    private static final String DATA_GATHER_STATE = "dataGather";
    private static final String CUSTOMER_DECLINED_STATE = "customerDeclined";
    private static final String COMPANY_DECLINED_STATE = "companyDeclined";
    private static final String RATED_STATE = "rated";
    private static final String PROPOSED_STATE = "proposed";
    private static final String BOUND_STATE = "bound";
    private static final String SUSPENDED_STATE = "suspended";
    private static final String ISSUED_STATE = "issued";

    private static final String INIT_COMMAND = "init";
    private static final String WRITE_COMMAND = "write";
    private static final String CUSTOMER_DECLINE_COMMAND = "customerDecline";
    private static final String COMPANY_DECLINE_COMMAND = "companyDecline";
    private static final String RATE_COMMAND = "rate";
    private static final String QUOTE_COPY_COMMAND = "quoteCopy";
    private static final String ENDORSE_COMMAND = "endorse";


    @Autowired
    private TransitionManager transitionManager;
    private final RootEntity rootEntity = new PersonalAutoFactory().createPersonalAutoPolicySummary(PolicyVariations.QUOTE);

    @Test
    public void verifiesTransitions() throws Exception {
        verifyTransition(UNINITIALIZED_STATE, INIT_COMMAND, INITIALIZED_STATE);

        verifyTransition(INITIALIZED_STATE, WRITE_COMMAND, DATA_GATHER_STATE);

        verifyTransition(DATA_GATHER_STATE, WRITE_COMMAND, DATA_GATHER_STATE);
        verifyTransition(DATA_GATHER_STATE, CUSTOMER_DECLINE_COMMAND, CUSTOMER_DECLINED_STATE);
        verifyTransition(DATA_GATHER_STATE, COMPANY_DECLINE_COMMAND, COMPANY_DECLINED_STATE);
        verifyTransition(DATA_GATHER_STATE, RATE_COMMAND, RATED_STATE);
        verifyTransition(DATA_GATHER_STATE, QUOTE_COPY_COMMAND, DATA_GATHER_STATE);

        verifyTransition(RATED_STATE, QUOTE_COPY_COMMAND, DATA_GATHER_STATE);

        verifyTransition(CUSTOMER_DECLINED_STATE, QUOTE_COPY_COMMAND, DATA_GATHER_STATE);

        verifyTransition(COMPANY_DECLINED_STATE, QUOTE_COPY_COMMAND, DATA_GATHER_STATE);

        verifyTransition(PROPOSED_STATE, QUOTE_COPY_COMMAND, DATA_GATHER_STATE);

        verifyTransition(BOUND_STATE, QUOTE_COPY_COMMAND, DATA_GATHER_STATE);

        verifyTransition(SUSPENDED_STATE, QUOTE_COPY_COMMAND, DATA_GATHER_STATE);

        verifyTransition(ISSUED_STATE, ENDORSE_COMMAND, DATA_GATHER_STATE);
    }

    private void verifyTransition(String fromState, String usingCommand, String toState) {
        ((StateHolder)rootEntity).setState(fromState);
        transitionManager.transit(MODEL_NAME, usingCommand, (StateHolder)rootEntity, Mockito.mock(JsonEntity.class)).test();
        assertThat("Destination state differs to expected.", ((Stateful)rootEntity).getState(), equalTo(toState));
    }

    @Configuration
    public static class Config {
        
        @Bean
        public ModelResolver ModelResolver() {
            return new ModelResolver(MODEL_NAME, "1");
        }
        
        @Bean
        public MessagePublisher messagePublisher() {
            return Mockito.mock(MessagePublisher.class);
        }
        
    }

}
