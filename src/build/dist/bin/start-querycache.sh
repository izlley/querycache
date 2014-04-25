#!/usr/bin/env bash

# This script runs the querycache core commands. 

bin=`which $0`
bin=`dirname ${bin}`
bin=`cd "$bin"; pwd`
 
DEFAULT_LIBEXEC_DIR="$bin"/../lib
QC_LIBEXEC_DIR=${QC_LIBEXEC_DIR:-$DEFAULT_LIBEXEC_DIR}
#. ${bin}/querycache-config.sh

function print_usage(){
  echo "Usage: hadoop [--config confdir] COMMAND"
  echo "       where COMMAND is one of:"
  echo "  fs                   run a generic filesystem user client"
  echo "  version              print the version"
  echo "  jar <jar>            run a jar file"
  echo "  checknative [-a|-h]  check native hadoop and compression libraries availability"
  echo "  distcp <srcurl> <desturl> copy file or directories recursively"
  echo "  archive -archiveName NAME -p <parent path> <src>* <dest> create a hadoop archive"
  echo "  classpath            prints the class path needed to get the"
  echo "                       Hadoop jar and the required libraries"
  echo "  daemonlog            get/set the log level for each daemon"
  echo " or"
  echo "  CLASSNAME            run the class named CLASSNAME"
  echo ""
  echo "Most commands print help when invoked w/o parameters."
}

if [ $# = 0 ]; then
  print_usage
  exit
fi

COMMAND=$1
case $COMMAND in
  # usage flags
  --help|-help|-h)
    print_usage
    exit
    ;;

  #hdfs commands
  namenode|secondarynamenode|datanode|dfs|dfsadmin|fsck|balancer|fetchdt|oiv|dfsgroups)
    echo "DEPRECATED: Use of this script to execute hdfs command is deprecated." 1>&2
    echo "Instead use the hdfs command for it." 1>&2
    echo "" 1>&2
    #try to locate hdfs and if present, delegate to it.  
    shift
    if [ -f "${HADOOP_HDFS_HOME}"/bin/hdfs ]; then
      exec "${HADOOP_HDFS_HOME}"/bin/hdfs ${COMMAND/dfsgroups/groups}  "$@"
    elif [ -f "${HADOOP_PREFIX}"/bin/hdfs ]; then
      exec "${HADOOP_PREFIX}"/bin/hdfs ${COMMAND/dfsgroups/groups} "$@"
    else
      echo "HADOOP_HDFS_HOME not found!"
      exit 1
    fi
    ;;

  #mapred commands for backwards compatibility
  pipes|job|queue|mrgroups|mradmin|jobtracker|tasktracker|mrhaadmin|mrzkfc|jobtrackerha)
    echo "DEPRECATED: Use of this script to execute mapred command is deprecated." 1>&2
    echo "Instead use the mapred command for it." 1>&2
    echo "" 1>&2
    #try to locate mapred and if present, delegate to it.
    shift
    if [ -f "${HADOOP_MAPRED_HOME}"/bin/mapred ]; then
      exec "${HADOOP_MAPRED_HOME}"/bin/mapred ${COMMAND/mrgroups/groups} "$@"
    elif [ -f "${HADOOP_PREFIX}"/bin/mapred ]; then
      exec "${HADOOP_PREFIX}"/bin/mapred ${COMMAND/mrgroups/groups} "$@"
    else
      echo "HADOOP_MAPRED_HOME not found!"
      exit 1
    fi
    ;;

  classpath)
    if $cygwin; then
      CLASSPATH=`cygpath -p -w "$CLASSPATH"`
    fi
    echo $CLASSPATH
    exit
    ;;

  #core commands  
  *)
    # the core commands
    if [ "$COMMAND" = "fs" ] ; then
      CLASS=org.apache.hadoop.fs.FsShell
    elif [ "$COMMAND" = "version" ] ; then
      CLASS=org.apache.hadoop.util.VersionInfo
    elif [ "$COMMAND" = "jar" ] ; then
      CLASS=org.apache.hadoop.util.RunJar
    elif [ "$COMMAND" = "checknative" ] ; then
      CLASS=org.apache.hadoop.util.NativeLibraryChecker
    elif [ "$COMMAND" = "distcp" ] ; then
      CLASS=org.apache.hadoop.tools.DistCp
      CLASSPATH=${CLASSPATH}:${TOOL_PATH}
    elif [ "$COMMAND" = "daemonlog" ] ; then
      CLASS=org.apache.hadoop.log.LogLevel
    elif [ "$COMMAND" = "archive" ] ; then
      CLASS=org.apache.hadoop.tools.HadoopArchives
      CLASSPATH=${CLASSPATH}:${TOOL_PATH}
    elif [[ "$COMMAND" = -*  ]] ; then
        # class and package names cannot begin with a -
        echo "Error: No command named \`$COMMAND' was found. Perhaps you meant \`hadoop ${COMMAND#-}'"
        exit 1
    else
      CLASS=$COMMAND
    fi
    shift
    
    # Always respect HADOOP_OPTS and HADOOP_CLIENT_OPTS
    HADOOP_OPTS="$HADOOP_OPTS $HADOOP_CLIENT_OPTS"

    #make sure security appender is turned off
    HADOOP_OPTS="$HADOOP_OPTS -Dhadoop.security.logger=${HADOOP_SECURITY_LOGGER:-INFO,NullAppender}"

    if $cygwin; then
      CLASSPATH=`cygpath -p -w "$CLASSPATH"`
    fi
    export CLASSPATH=$CLASSPATH
    exec "$JAVA" $JAVA_HEAP_MAX $HADOOP_OPTS $CLASS "$@"
    ;;

esac
