#!/bin/bash
set -e
distdir=querycache-server-dist
rm -rf ${distdir} ${distdir}.tgz
mkdir ${distdir}
mkdir ${distdir}/logs
mkdir -p ${distdir}/lib/driver
cp -rLv bin src www ${distdir}/
cp -rLv conf-dist ${distdir}/conf
cp querycache-server/target/querycache-server-*-jar-with-dependencies.jar ${distdir}/lib/
#cp -rLv `find lib/driver -name *.jar -a ! -name querycache-jdbc*.jar` ${distdir}/lib/driver
cp -rLv lib/driver/*.jar ${distdir}/lib/driver

tar cvzf ${distdir}.tgz ${distdir}
