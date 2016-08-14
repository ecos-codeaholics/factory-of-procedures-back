# Project: FABRICA DE TRAMITES 
Sparkjava application for Project. This is the Backend repository

## Description

## Requirements

* Java JDK 1.8
* Sparkjava 2.5
* Freemarker
* MongoDB 3.2x
* Maven

## ¿How to use?

* Clone the repository in your projects directory, cd into it and run:

> mvn clean #optional
> mvn compile exec:java -Dexec.mainClass="edu.uniandes.ecos.codeaholics.main.App"

Note: on Windows machines, you need:

> mvn compile exec:java -D"exec.mainClass=edu.uniandes.ecos.codeaholics.main.App"

* Finally go to your browser and enter to the following URI:

> localhost:4567

Currently there is a configuration file located at src/main/resources/config.properties that you can edit to change the current configuration of the application.

## Tests

In order to run only the unitary tests, you can do:

> mvn test

* For testing use the "development" branch


