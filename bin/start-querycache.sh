#!/bin/bash

for i in "$@"
do
case $i in
    -storage_driver_dir=*)
    DRIVER_DIR="${i#*=}"
    ;;
    -h|--help|*)
    echo "Usage: `basename $0` -storage_driver_dir=<dir_path>"
    exit 1
    ;;
esac
done

# Source this file from the $QC_HOME directory to
# setup your environment. If $QC_HOME is undefined
# this script will set it to the current working directory.

export JAVA_HOME=${JAVA_HOME-/app/jdk}
if [ ! -d $JAVA_HOME ] ; then
    echo "Error! JAVA_HOME must be set to the location of your JDK!"
    exit 1
fi

JAVA=${JAVA-${JAVA_HOME}/bin/java}

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

JVMARGS=${JVMARGS-"-enableassertions -enablesystemassertions -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -Xms5g -Xmx5g -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -verbose:gc -Xloggc:$QC_HOME/log/querycache-gc-$(date +%Y%m%d-%H%M%S).log"}
export QC_CONF_DIR=$QC_HOME/conf
export QC_LIB_DIR=$QC_HOME/lib
export PATH=$QC_HOME/bin:$PATH

echo "$QC_LIB_DIR"

CLASSPATH=$QC_HOME/lib:$CLASSPATH
CLASSPATH=$QC_HOME/conf:$CLASSPATH
for jar in `ls ${QC_LIB_DIR}/*.jar`; do
  CLASSPATH=${CLASSPATH}:$jar
done
for jar in `ls ${DRIVER_DIR}/*.jar`; do
  CLASSPATH=${CLASSPATH}:$jar
done
export CLASSPATH

echo "QC_HOME                = $QC_HOME"
echo "JAVA_HOME              = $JAVA_HOME"
echo "DRIVER_DIR             = $DRIVER_DIR"
echo "CLASSPATH              = $CLASSPATH"
echo "JVMARGS                = $JVMARGS"

echo "start Querycache..."
exec nohup $JAVA $JVMARGS -classpath "$CLASSPATH" com.skplanet.querycache.server.QueryCacheServer "$@" > /dev/null 2>&1 &
