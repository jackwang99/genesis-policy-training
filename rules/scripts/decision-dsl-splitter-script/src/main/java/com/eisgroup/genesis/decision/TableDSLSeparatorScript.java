/*
 *  Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */

package com.eisgroup.genesis.decision;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Entry point class with main method to make executable jar,
 * that will separate .dtables DSL file string with multiple tables to separate dsl for each table
 *
 * @author psurinin
 */
public class TableDSLSeparatorScript implements Runnable{

    @Option(names = {"-i", "--input"}, description = "Input directory", defaultValue = "./", converter = DirConverter.class)
    private File inputDir;

    @Option(names = {"-o", "--output"}, description = "Output directory", defaultValue = "./", converter = DirConverter.class)
    private File outputDir;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Print usage help and exit.")
    private boolean usageHelpRequested;

    public static void main(String[] args) {
        new CommandLine(new TableDSLSeparatorScript()).execute(args);
    }

    private static String read(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        if(usageHelpRequested) {
            new CommandLine(this).usage(System.out);
        } else {
            separate();
        }
    }

    private void separate() {
        System.out.println("Searching *.dtables files in " + inputDir.getAbsolutePath() + " folder.");
        Stream.of(inputDir.listFiles())
                .filter(File::isFile)
                .filter(file -> file.getAbsolutePath().endsWith(".dtables"))
                .map(TableDSLSeparatorScript::read)
                .filter(Objects::nonNull)
                .map(TableDslSeparator::separate)
                .flatMap(Collection::stream)
                .peek(table -> System.out.println("Writing table dsl to file: '" + table.getName() + ".dtable'"))
                .forEach(this::write);
    }

    private void write(TableDslSeparator.TableDsl table) {
        outputDir.mkdir();
        try {
            Files.write(Paths.get(outputDir.getAbsolutePath() + "/" + table.getName() + ".dtable"), table.getDsl().getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class DirConverter implements CommandLine.ITypeConverter<File> {

        @Override
        public File convert(String s) throws Exception {
            final File dir = new File(s);
            if (!dir.exists()) {
                throw new IllegalArgumentException("Provided path '" + dir.getAbsolutePath() + "' does not exist");
            }
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("Provided path '" + dir.getAbsolutePath() + "' is not directory");
            }
            return dir;
        }
    }

}



