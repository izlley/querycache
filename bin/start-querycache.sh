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
        QC_HOME="$(pwd)/.."
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

        QC_HOME=`dirname "$bin"`
    fi
fi
export QC_HOME

# explicitly change working directory to $QC_HOME
cd $QC_HOME

if [ -z $DRIVER_DIR ]; then
    DRIVER_DIR=$QC_HOME/lib/driver
fi

GCLOGFILE=querycache-gc-$(date +%Y%m%d-%H%M%S).log
JVMARGS=${JVMARGS-"-enableassertions -enablesystemassertions -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly -Xms5g -Xmx5g -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -verbose:gc -Xloggc:$QC_HOME/logs/${GCLOGFILE} -DQC_HOME=${QC_HOME}"}
export QC_CONF_DIR=$QC_HOME/conf
export QC_LIB_DIR=$QC_HOME/lib
export PATH=$QC_HOME/bin:$PATH

echo "$QC_LIB_DIR"

CLASSPATH=$QC_HOME/lib:$CLASSPATH
CLASSPATH=$QC_HOME/conf:$CLASSPATH
if [ -e ${QC_HOME}/pom.xml ]; then
  version=`mvn -o org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -pl querycache-server | egrep '^[0-9]*\.[0-9]*\.[0-9]'`
  builtjar=`find ${QC_HOME}/querycache-server/target -regextype sed -regex ".*/querycache-server-${version}.jar" 2>/dev/null`
  if [ ! -z "$builtjar" ]; then
    CLASSPATH=${CLASSPATH}:$builtjar
    for jar in `find ${QC_LIB_DIR} -name "*.jar" -a ! -name "querycache-*.jar"`; do
      CLASSPATH=${CLASSPATH}:$jar
    done
  fi
else
  for jar in `ls ${QC_LIB_DIR}/*.jar`; do
    CLASSPATH=${CLASSPATH}:$jar
  done
fi
for jar in `find ${DRIVER_DIR} -name "*.jar" -a ! -name "querycache-jdbc*.jar"`; do
  CLASSPATH=${CLASSPATH}:$jar
done
export CLASSPATH
export HOSTNAME=`hostname`

echo "QC_HOME                = $QC_HOME"
echo "JAVA_HOME              = $JAVA_HOME"
echo "DRIVER_DIR             = $DRIVER_DIR"
echo "CLASSPATH              = $CLASSPATH"
echo "JVMARGS                = $JVMARGS"
echo "HOSTNAME               = $HOSTNAME"

rm -f $QC_HOME/logs/querycache-gc.log
ln -s ${GCLOGFILE} $QC_HOME/logs/querycache-gc.log

echo "start Querycache..."
if [ x${QC_FOREGROUND} = x ]; then
exec nohup $JAVA $JVMARGS -DHOSTNAME="${HOSTNAME}" -classpath "$CLASSPATH" com.skplanet.querycache.server.QueryCacheServer -DHOSTNAME="${HOSTNAME}" "$@" > /dev/null 2>&1 &
else
$JAVA $JVMARGS -DHOSTNAME="${HOSTNAME}" -classpath "$CLASSPATH" com.skplanet.querycache.server.QueryCacheServer -DHOSTNAME="${HOSTNAME}" "$@"
fi
