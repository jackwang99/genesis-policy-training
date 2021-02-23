# Migration script for decision tables

## description

This script separates decision `*.dtables` file to separate `*.dtable` files.
Run `mvn clean install` to create executable `jar`. In folder `target/` found executable `jar`
named `decision-dsl-splitter-script-X.X-SNAPSHOT-jar-with-dependencies.jar` which you can run.

## options

 - -i --input   input directory, default current directory
 - -o --output  input directory, default current directory
 - -h --help    help message