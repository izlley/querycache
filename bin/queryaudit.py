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

hostname = socket.gethostname()

def callback(filename, lines):
    global hostname
    if len(lines) == 0:
        return

    f = open(LOG_DIR + TMP_LOG_FILE, 'w')
    f2 = open(LOG_DIR + TMP_LOG_FILE2, 'w')

    date1 = ''
    date2 = ''

    processed_lines = 0
    print "processing ..."
    for line in lines:
        writestr = ''
        jsonlog = re.search('{.*}',line).group(0)
        if len(jsonlog) == 0:
            print_log('ERROR: Incompatible format of line is received')
            continue

        logdate = line[:23]
        #escaping backslash character
        jsondata = json.loads(jsonlog.replace('\\','\\\\\\\\'))
        queryid = jsondata['queryid']
        conn_type = jsondata['connect_type']
        client_host = jsondata['client_host']
        user = jsondata['user']
        query_type = jsondata['query_type']
        query_str = jsondata['query_str']
        stmt_state = jsondata['stmt_state']
        rowcnt = jsondata['rowcnt']
        end_time = jsondata['end_time']
        start_time = jsondata['start_time']
        time_histogram = '[' + ','.join(jsondata['time_histogram']) + ']'
        total_elapsedtime = jsondata['total_elapsedtime']
        if 'client_version' in jsondata:
            client_version = jsondata['client_version']
        else:
            client_version = 'unknown'
        if 'server' in jsondata:
            server = jsondata['server']
        else:
            server = hostname

        writestr = logdate + '\t' + server + '\t' + user + '\t' + queryid + '\t' + \
            query_type + '\t' + query_str + '\t' + stmt_state + '\t' + rowcnt + '\t' + \
            start_time + '\t' + end_time + '\t' + time_histogram + '\t' + total_elapsedtime + '\t' + \
            conn_type + '\t' + client_host + '\t' + client_version + '\n'
        if date1 == '':
            date1 = line[:10]

        if date1 == line[:10]:
            f.write(writestr.encode('utf-8'))
        else:
            if date2 == '':
                date2 = line[:10]
            f2.write(writestr.encode('utf-8'))

        processed_lines += 1
        if processed_lines % 1000 == 0:
            print 'processed %d lines' % processed_lines

    f.close()
    f2.close()

    if len(date1) > 0:
        print_log('INFO: write query audit log.');
        makeHDFSDir(HDFS_LOG_BASE_DIR + date1.replace('-','/'))
        write2HDFS(LOG_DIR + TMP_LOG_FILE,
                   HDFS_LOG_BASE_DIR + date1.replace('-','/') + '/' + hostname + '.tsv')
        os.remove(LOG_DIR + TMP_LOG_FILE)

    if len(date2) > 0:
        print_log('INFO: write next day query audit log.');
        makeHDFSDir(HDFS_LOG_BASE_DIR + date2.replace('-','/'))
        write2HDFS(LOG_DIR + TMP_LOG_FILE2,
                   HDFS_LOG_BASE_DIR + date2.replace('-','/') + '/' + hostname + '.tsv')
        os.remove(LOG_DIR + TMP_LOG_FILE2)

    print_log('INFO: Total write lines = ' + str(len(lines)))

def write2HDFS(localpath, hdfspath):
    print_log('INFO: Append local file to hdfs. ' + localpath + '-> hdfs://' + hdfspath )
    p = Popen([HDFS_BIN, 'dfs', '-appendToFile', localpath, hdfspath], stdout=PIPE, stderr=PIPE)
    result = p.stdout.read()
    if result:
        print result
    result_err = p.stderr.read()
    if result_err:
        print result_err

def makeHDFSDir(newpath):
    print_log('INFO: Make the HDFS dir = hdfs://' + newpath)
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
