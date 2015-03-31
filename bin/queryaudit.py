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
        try:
            writestr = ''
            jsonlog = re.search('{.*}',line).group(0)

            logdate = line[:23]
            #escaping backslash character
            jsondata = json.loads(jsonlog.replace('\\','\\\\\\\\'))

            if 'client_version' not in jsondata:
                jsondata['client_version'] = "unknown"
            if 'client_ip' not in jsondata:
                jsondata['client_ip'] = "unknown"
            if 'server_ip' not in jsondata:
                jsondata['server_ip'] = "unknown"

            writestr = logdate + '\t' + \
                       hostname + '\t' + \
                       jsondata['user'] + '\t' + \
                       jsondata['queryid'] + '\t' + \
                       jsondata['query_type'] + '\t' + \
                       jsondata['query_str'] + '\t' + \
                       jsondata['stmt_state'] + '\t' + \
                       jsondata['rowcnt'] + '\t' + \
                       jsondata['start_time'] + '\t' + \
                       jsondata['end_time'] + '\t' + \
                       '[' + ','.join(jsondata['time_histogram']) + ']' + '\t' + \
                       jsondata['total_elapsedtime'] + '\t' + \
                       jsondata['connect_type'] + '\t' + \
                       jsondata['client_host'] + '\t' + \
                       jsondata['client_version'] + '\t' + \
                       jsondata['client_ip'] + '\t' + \
                       jsondata['server_ip'] + '\n'

            if date1 == '':
                date1 = line[:10]

            if date1 == line[:10]:
                f.write(writestr.encode('utf-8'))
            else:
                if date2 == '':
                    date2 = line[:10]
                f2.write(writestr.encode('utf-8'))

        except (ValueError, AttributeError, KeyError) as e:
            print_log('ERROR: Can\'t parse line.\n%s\n%s' % (line, str(e)));
        except Error as e:
            print_log('ERROR: Can\'t parse line. (Unexpected Error)\n%s\n%s' % (line, str(e)));

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
