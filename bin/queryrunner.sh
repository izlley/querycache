#!/bin/bash

# Source this file from the $QC_HOME directory to
# setup your environment. If $QC_HOME is undefined
# this script will set it to the current working directory.

export JAVA_HOME=${JAVA_HOME-/usr/java/default}
if [ ! -d $JAVA_HOME ] ; then
    echo "Error! JAVA_HOME must be set to the location of your JDK!"
    exit 1
fi

JAVA=${JAVA-'java'}

if [ -z $QC_HOME ]; then
    this=${0/-/} # login-shells often have leading '-' chars
    shell_exec=`basename $SHELL`
    if [ "$this" = "$shell_exec" ]; then
        # Assume we're already in QC_HOME
        interactive=1
        export QC_HOME="$(pwd)/.."
    else
        interactive=0
        while [ -h "$this" ]; do
            ls=`ls -ld "$this"`
            link=`expr "$ls" : '.*-> \(.*\)$'`
            if expr "$link" : '.*/.*' > /dev/null; then
                this="$link"
            else
                this=`dirname "$this"`/"$link"
            fi
        done

        # convert relative path to absolute path
        bin=`dirname "$this"`
        script=`basename "$this"`
        bin=`cd "$bin"; pwd`
        this="$bin/$script"

        export QC_HOME=`dirname "$bin"`
    fi
fi

if [ -z $DRIVER_DIR ]; then
    DRIVER_DIR=$QC_HOME/lib/driver
fi

JVMARGS=
export QC_CONF_DIR=$QC_HOME/conf
export QC_LIB_DIR=$QC_HOME/lib
export PATH=$QC_HOME/bin:$PATH

CLASSPATH=$QC_HOME/src/test/java:$CLASSPATH
CLASSPATH=${QC_LIB_DIR}/slf4j-api-1.7.10.jar:${QC_LIB_DIR}/commons-logging-1.0.4.jar:${QC_LIB_DIR}/slf4j-simple-1.7.10.jar:${CLASSPATH}
CLASSPATH=${QC_LIB_DIR}/commons-lang3-3.1.jar:${QC_LIB_DIR}/libthrift-0.9.1.jar:${CLASSPATH}

DRVPATH=
for jar in `ls ${DRIVER_DIR}/querycache-jdbc*.jar`; do
      DRVPATH=$jar
done
if [ -z "$DRVPATH" ]; then
    version=`mvn -o org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -pl querycache-jdbc -Dexpression=project.version | egrep '^[0-9]*\.[0-9]*\.[0-9]'`
    DRVPATH=${QC_HOME}/querycache-jdbc/target/querycache-jdbc-${version}.jar
fi

if [ ! -e $DRVPATH ]; then
    echo "$DRVPATH doesn't exist"
    exit 1
fi

export CLASSPATH=${DRVPATH}:${CLASSPATH}

echo "----------------ENV----------------" 
echo "QC_HOME                = $QC_HOME"
echo "JAVA_HOME              = $JAVA_HOME"
echo "CLASSPATH              = $CLASSPATH"
echo "-----------------------------------" 

exec $JAVA $JVMARGS -classpath "$CLASSPATH" QueryRunner "$@"

