/*
 *  Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

import com.eisgroup.genesis.decision.TableDslSeparator;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author psurinin
 */
public class TableDslSeparatorTest {

    @Test
    public void shouldExtractInTwoSeparateDSLs() {
        final String separator = System.lineSeparator();
        final List<TableDslSeparator.TableDsl> tables = TableDslSeparator.separate("" +
                "Model FleetAuto" + separator +
                separator +
                "@EntryPoint(\"Apply DT\", \"Quote Policy Validation\", \"Update dimension\", \"Harmonize\", \"Default apply\")" + separator +
                "@CrossTab" + separator +
                "@Versioned(\"Effective Date\", \"Request Date\")" + separator +
                "Table \"Entity Applicability\" {" + separator +
                "  InputColumn \"Grouped AutoVehicle\" as Boolean : dimensions.grouped" + separator +
                "  InputColumn \"Coverage Type\" as String : dimensions.coverageType" + separator +
                "  AspectColumn \"Entity\" : entity" + separator +
                "  AspectColumn \"Applicability\" : applicability" + separator +
                "}" + separator +
                separator +
                "@EntryPoint(\"Apply DT\", \"Quote Policy Validation\", \"Update dimension\", \"Harmonize\", \"Default apply\")" + separator +
                "@CrossTab" + separator +
                "@Overridable" + separator +
                "@Category(\"KrakenRules\")" + separator +
                "@Versioned(\"Effective Date\", \"Request Date\")" + separator +
                "Table \"Min Max\" {" + separator +
                "  InputColumn \"Authority\" as Integer : securityContext.authorityLevel" + separator +
                "  AspectColumn \"Entity\" : entity" + separator +
                "  AspectColumn \"Attribute\" : attribute" + separator +
                "  AspectColumn \"Min\" : min" + separator +
                "  AspectColumn \"Max\" : max" + separator +
                "}");
        assertThat(tables, hasSize(2));
        assertThat(tables.get(0).getName(), CoreMatchers.is("Entity Applicability"));
        assertThat(tables.get(1).getName(), CoreMatchers.is("Min Max"));
    }

}