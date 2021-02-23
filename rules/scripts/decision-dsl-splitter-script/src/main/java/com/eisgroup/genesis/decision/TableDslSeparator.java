/*
 *  Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.eisgroup.genesis.decision;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Separates .dtables DSL file string with multiple tables to separate dsl for each table
 *
 * @author psurinin
 */
public class TableDslSeparator {

    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile(".*Table\\s\\\"([\\w\\s]+).*");
    /**
     *  Method to separate .dtables DSL file string with multiple tables to separate dsl for each table
     *
     * @param dsl   .dtables DSL string containing multiple table definitions
     * @return      table DSL's with table name
     */
    public static List<TableDsl> separate(String dsl) {
        final ArrayList<TableDsl> tables = new ArrayList<>();
        for (String t : dsl.split("\\}")) {
            if(t.contains("Model")) {
                final String tableDSL = Stream.of(t.split(System.lineSeparator()))
                        .filter(line -> !line.contains("Model "))
                        .filter(line -> !line.isEmpty())
                        .collect(Collectors.joining(System.lineSeparator()));
                tables.add(new TableDsl(
                        getTableName(tableDSL),
                        tableDSL.concat(System.lineSeparator() + "}")
                ));
            } else {
                final String tableDsl = Stream.of(t.split(System.lineSeparator()))
                        .filter(line -> !line.isEmpty())
                        .collect(Collectors.joining(System.lineSeparator()));
                if(!tableDsl.isEmpty()) {
                    tables.add(new TableDsl(
                            getTableName(tableDsl),
                            tableDsl.concat(System.lineSeparator() + "}")
                    ));
                }
            }
        }
        return tables;
    }

    private static String getTableName(String tableDSL) {
        for(String l: tableDSL.split(System.lineSeparator())) {
            final Matcher matcher = TABLE_NAME_PATTERN.matcher(l);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        throw new IllegalArgumentException("Failed to extract table name from \n" + tableDSL);
    }

    public static class TableDsl {
        private final String name;
        private final String dsl;

        public TableDsl(String name, String dsl) {
            this.name = name;
            this.dsl = dsl;
        }

        public String getName() {
            return name;
        }

        public String getDsl() {
            return dsl;
        }
    }
}
