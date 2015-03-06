#!/bin/bash
set -e

version=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -pl querycache-server | egrep '^[0-9]*\.[0-9]*\.[0-9]'`
# server
distdir=querycache-${version}
rm -rf ${distdir} ${distdir}.tgz
mkdir ${distdir}
mkdir ${distdir}/logs
mkdir -p ${distdir}/lib/driver
cp -rLv bin www ${distdir}/
cp -rLv conf-dist ${distdir}/conf
cp querycache-server/target/querycache-server-*-jar-with-dependencies.jar ${distdir}/lib/
cp -rLv `find lib/driver -name *.jar -a ! -name querycache-jdbc*.jar` ${distdir}/lib/driver
rm -f ${distdir}/bin/queryrunner.sh

# jdbc + test
jdbcdistdir=${distdir}/jdbc
deps=`mvn dependency:build-classpath -pl querycache-jdbc | egrep "/.*\.jar" | awk -F':' '{ for (i = 1; i <= NF; i++) { print $i }}'`
rm -rf ${jdbcdistdir} ${jdbcdistdir}.tgz
mkdir -p ${jdbcdistdir}/bin
mkdir -p ${jdbcdistdir}/lib/driver
cp -rLv bin/queryrunner.sh ${jdbcdistdir}/bin/
cp ${deps} ${jdbcdistdir}/lib/
cp `find querycache-jdbc -regextype sed -regex ".*/querycache-jdbc-[0-9\.]*.jar"` ${jdbcdistdir}/lib/driver/
cp -rLv src ${jdbcdistdir}/

tar cvzf ${distdir}.tgz ${distdir}
