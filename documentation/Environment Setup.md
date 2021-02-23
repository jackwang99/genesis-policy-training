# How to setup your environment

## Install:

* Java OpenJDK 11.0.1(https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_windows-x64_bin.zip)
* Maven 3.5.2 (https://archive.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.zip) with config file: ../config/m2_settings.xml
* Docker Toolbox: https://docs.docker.com/toolbox/toolbox_install_windows/

## Build everything from source root:

```
mvn clean install -DskipTests=true
```

## Setup Docker

* Run ./scripts/genesis-trainig-shell.cmd
* Increase memory of Virtual machine up to 16Gb and usage CPUs
* Login into docker repository

```
docker login -u username -p password sfoeisnexus03.exigengroup.com/genesis-docker
``` 

* Inside docker console switch directory to 
```
../docker/dev
```

* Build docker images

```
docker-compose build
``` 

* Start infrastructure services
```
docker-compose up -d dse
docker-compose up -d zookeeper kafka jaeger
docker-compose up -d postgres
```

* Deploy DB schema

```
docker-compose up infra_deployer 
docker-compose up sec_deployer 
docker-compose up party_deployer 
docker-compose up policy_generic_deployer 
docker-compose up policy_auto_deployer 
```

* Start necessary services
```
docker-compose up -d sec_commands sec_events sec_facade infra_events
docker-compose up -d policy_auto_commands policy_auto_facade policy_generic_events policy_generic_commands policy_generic_facade
```

## Check that Policy micro service works

* Obtain IP address

``` 
docker-machine ip genesis-training
``` 

Note: login/password for all REST requests - qa/qa

* Install Postman (https://www.getpostman.com/)
* Import Policy schema into Postman from http://<APP_DEPLOYMENT_IP>:8084/api/common/schema/v1
* Execute Init command:

```
POST {{url}}/policy/PersonalAuto/v1/command/quote/init
```

Should be returned initial Policy Personal Auto image
