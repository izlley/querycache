#!/usr/bin/python

import os
import time
import re
import socket
import json
from subprocess import Popen, PIPE, STDOUT
from logwatcher import LogWatcher

TIME_CYCLE_SEC = 30
LOG_DIR='/app/bdb/querycache/logs/'
HDFS_BIN='/app/hdfs/bin/hdfs'
HDFS_LOG_BASE_DIR='/data/admin/tsv/qc-query-audit/'

LOG_FILE='queryaudit.log'
TMP_LOG_FILE='queryaudit.tmp'
TMP_LOG_FILE2='queryaudit.tmp2'

def callback(filename, lines):
    if lines != '':
        writestr1 = ''
        writestr2 = ''
        date1 = ''
        date2 = ''
        jsonlog = ''
        for line in lines:
            jsonlog = re.search('{.*}',line).group(0)
            if jsonlog != '':
                logdate = line[:23]
                #escaping backslash character
                jsondata = json.loads(jsonlog.replace('\\','\\\\\\\\'))
                hostname = socket.gethostname()
                queryid = jsondata['queryid']
                conn_type = jsondata['connect_type']
                user = jsondata['user']
                query_type = jsondata['query_type']
                query_str = jsondata['query_str']
                stmt_state = jsondata['stmt_state']
                rowcnt = jsondata['rowcnt']
                end_time = jsondata['end_time']
                start_time = jsondata['start_time']
                time_histogram = '[' + ','.join(jsondata['time_histogram']) + ']'
                total_elapsedtime = jsondata['total_elapsedtime']

                if not date1:
                    date1 = line[:10]
                    writestr1 += logdate + '\t' + hostname + '\t' + user + '\t' + queryid + '\t' + \
                        query_type + '\t' + query_str + '\t' + stmt_state + '\t' + rowcnt + '\t' + \
                        start_time + '\t' + end_time + '\t' + time_histogram + '\t' + total_elapsedtime + '\t' + \
                        conn_type + '\n'
		else:
		    if date1 == line[:10]:
                        writestr1 += logdate + '\t' + hostname + '\t' + user + '\t' + queryid + '\t' + \
                            query_type + '\t' + query_str + '\t' + stmt_state + '\t' + rowcnt + '\t' + \
                            start_time + '\t' + end_time + '\t' + time_histogram + '\t' + total_elapsedtime + '\t' + \
                            conn_type + '\n'
                    else:
                        if date2 == '':
                            date2 = line[:10]
                        writestr2 += logdate + '\t' + hostname + '\t' + user + '\t' + queryid + '\t' + \
                            query_type + '\t' + query_str + '\t' + stmt_state + '\t' + rowcnt + '\t' + \
                            start_time + '\t' + end_time + '\t' + time_histogram + '\t' + total_elapsedtime + '\t' + \
                            conn_type + '\n'
            else:
                print_log('ERROR: Incompatible format of line is received');
        if len(writestr1) > 0:
            f = open(LOG_DIR + TMP_LOG_FILE, 'w')
	    print_log('INFO: write query audit log.');
            f.write(writestr1.encode('utf-8')) 
            f.close()
            makeHDFSDir(HDFS_LOG_BASE_DIR + date1.replace('-','/'))
            write2HDFS(writestr1, LOG_DIR + TMP_LOG_FILE,
                HDFS_LOG_BASE_DIR + date1.replace('-','/') + '/' + hostname + '.tsv')
            os.remove(LOG_DIR + TMP_LOG_FILE)
        if len(writestr2) > 0:
            f = open(LOG_DIR + TMP_LOG_FILE2, 'w')
            print_log('INFO: write next day query audit log.');
            f.write(writestr2.encode('utf-8')) 
            f.close()
            makeHDFSDir(HDFS_LOG_BASE_DIR + date2.replace('-','/'))
            write2HDFS(writestr2, LOG_DIR + TMP_LOG_FILE2,
                HDFS_LOG_BASE_DIR + date2.replace('-','/') + '/' + hostname + '.tsv')
            os.remove(LOG_DIR + TMP_LOG_FILE2)
        print_log('INFO: Total write lines = ' + str(len(lines)))

def write2HDFS(data, localpath, hdfspath):
    print_log('INFO: Append local file to hdfs. ' + localpath + '-> hdfs://' + hdfspath );
    p = Popen([HDFS_BIN, 'dfs', '-appendToFile', localpath, hdfspath], stdout=PIPE, stderr=PIPE)
    result = p.stdout.read()
    if result:
        print result
    result_err = p.stderr.read()
    if result_err:
        print result_err

def makeHDFSDir(newpath):
    print_log('INFO: Make the HDFS dir = hdfs://' + newpath);
    p = Popen([HDFS_BIN, 'dfs', '-mkdir', newpath], stdout=PIPE, stderr=PIPE)
    result = p.stdout.read()
    if result:
        print result
    result_err = p.stderr.read()
    if result_err:
        print result_err

def print_log(msg):
    print time.strftime("%Y/%m/%d %H:%M:%S") + '\t' + msg

if __name__ == '__main__':
    watcher = LogWatcher(LOG_DIR + LOG_FILE, callback, True)
    watcher.loop(TIME_CYCLE_SEC)
    print "Exiting Main Thread"
