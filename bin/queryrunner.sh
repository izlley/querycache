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
#${JVMARGS-"-enableassertions -enablesystemassertions -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -Xms1g -Xmx5g -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -verbose:gc -Xloggc:$QC_HOME/log/querycache-gc-$(date +%Y%m%d-%H%M%S).log"}
export QC_CONF_DIR=$QC_HOME/conf
export QC_LIB_DIR=$QC_HOME/lib
export PATH=$QC_HOME/bin:$PATH

for jar in `ls ${QC_LIB_DIR}/driver/*.jar`; do
      CLASSPATH=${CLASSPATH}:$jar
done

CLASSPATH=$QC_HOME/examples:$QC_HOME/lib:$CLASSPATH
CLASSPATH=${QC_LIB_DIR}/querycache-jdbc-0.10.0.jar:${QC_LIB_DIR}/slf4j-api-1.6.4.jar:${QC_LIB_DIR}/commons-logging-1.0.4.jar:${QC_LIB_DIR}/slf4j-simple-1.6.4.jar:${CLASSPATH}
export CLASSPATH

echo "----------------ENV----------------" 
echo "QC_HOME                = $QC_HOME"
echo "JAVA_HOME              = $JAVA_HOME"
echo "CLASSPATH              = $CLASSPATH"
echo "-----------------------------------" 

exec $JAVA $JVMARGS -classpath "$CLASSPATH" QueryRunner "$@"

