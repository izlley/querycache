#!/bin/bash
set -e

version=`mvn -o org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -pl querycache-server | egrep '^[0-9]*\.[0-9]*\.[0-9]'`
# server
distdir=querycache-${version}
rm -rf ${distdir} ${distdir}.tgz
mkdir ${distdir}
mkdir ${distdir}/logs
mkdir -p ${distdir}/lib/driver
cp -rLv bin www ${distdir}/
cp -rLv conf-dist ${distdir}/conf
cp querycache-server/target/querycache-server-${version}-jar-with-dependencies.jar ${distdir}/lib/
cp -rLv `find lib/driver -name *.jar -a ! -name querycache-jdbc*.jar` ${distdir}/lib/driver
rm -f ${distdir}/bin/queryrunner.sh
cp -rLv lib/QueryCacheAuditSentinelShuttle-0.1.4.jar ${distdir}/lib/

# jdbc + test
jdbcdistdir=${distdir}/jdbc
deps=`mvn -o dependency:build-classpath -pl querycache-jdbc | egrep "/.*\.jar" | awk -F':' '{ for (i = 1; i <= NF; i++) { print $i }}'`
rm -rf ${jdbcdistdir} ${jdbcdistdir}.tgz
mkdir -p ${jdbcdistdir}/bin
mkdir -p ${jdbcdistdir}/lib/driver
cp -rLv bin/queryrunner.sh ${jdbcdistdir}/bin/
cp ${deps} ${jdbcdistdir}/lib/
cp `find querycache-jdbc/target -regextype sed -regex ".*/querycache-jdbc-${version}.jar"` ${jdbcdistdir}/lib/driver/
cp -rLv src ${jdbcdistdir}/

# qcshell
qcshelldistdir=${distdir}/qcshell
mkdir -p ${qcshelldistdir}/bin
mkdir -p ${qcshelldistdir}/lib
cp -apv bin/qcshell ${qcshelldistdir}/bin
cp `find querycache-shell/target -regextype sed -regex ".*/querycache-shell-${version}-jar-with-dependencies.jar"` ${qcshelldistdir}/lib/

tar cvzf ${distdir}.tgz ${distdir}

