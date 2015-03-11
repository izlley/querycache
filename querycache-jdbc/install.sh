#!/bin/bash
version=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | egrep '^[0-9]*\.[0-9]*\.[0-9]'`
echo "$version"
mvn install:install-file \
    -DpomFile=querycache-jdbc.deploy.pom.xml \
    -Dfile=target/querycache-jdbc-${version}.jar
