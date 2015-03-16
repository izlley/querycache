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

JVMARGS=
export QC_LIB_DIR=$QC_HOME/lib
export PATH=$QC_HOME/bin:$PATH

shelljar=`find ${QC_LIB_DIR} -name 'querycache-shell*.jar'`
if [ -z "$shelljar" ]; then
    echo "ERROR: Can't find querycache-shell*.jar in ${QC_LIB_DIR}"
    exit 1
fi

# error if many first occurrance
count=0
for f in $shelljar; do
    count=$(($count + 1))
done
if [ $count -gt 1 ]; then
    echo "Multiple jar found:"
    for f in $shelljar; do
        echo -e "\t$f"
    done
    exit 1
fi

export CLASSPATH=$shelljar
USERNAME=$(id -un)

JVMARGS="-Dorg.slf4j.simpleLogger.defaultLogLevel=debug -Dorg.slf4j.simpleLogger.showShortLogName=true"
echo $JAVA $JVMARGS -classpath "$CLASSPATH" QueryCacheShell "$@"
exec $JAVA $JVMARGS -classpath "$CLASSPATH" com.skplanet.querycache.shell.QueryCacheShell -n ${USERNAME} "$@"
