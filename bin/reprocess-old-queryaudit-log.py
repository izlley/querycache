#!/usr/bin/python

import os
import sys
import time
import re
import socket
import json
import errno
from subprocess import Popen, PIPE, STDOUT

HDFS_BIN='/app/hdfs/bin/hdfs'
HDFS_LOG_BASE_DIR='/data/admin/tsv/qc-query-audit/'
TMP_DIR='tmp.qc-query-audit/'

files = {}
hostname = socket.gethostname()

def process_lines(filename, lines):
    global files
    global hostname

    if len(lines) == 0:
        return

    processed_lines = 0
    print "processing ..."

    for line in lines:
        writestr = ''
        jsonlog = re.search('{.*}',line).group(0)
        if len(jsonlog) == 0:
            print_log('ERROR: Incompatible format of line is received');
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
            client_version = jsondata['unknown']
        if 'server' in jsondata:
            server = jsondata['server']
        else:
            server = hostname

        date = line[:10]
        writestr = logdate + '\t' + server + '\t' + user + '\t' + queryid + '\t' + \
            query_type + '\t' + query_str + '\t' + stmt_state + '\t' + rowcnt + '\t' + \
            start_time + '\t' + end_time + '\t' + time_histogram + '\t' + total_elapsedtime + '\t' + \
            conn_type + '\t' + client_host + '\t' + client_version + '\n'

        if date in files:
            f = files[date]
        else:
            f = open(TMP_DIR + date, 'w')
            print_log('new date ' + date)
            files[date] = f

        f.write(writestr.encode('utf-8'))
        processed_lines += 1
        if processed_lines % 10000 == 0:
            print 'processed %d lines' % processed_lines

def write2HDFS(localpath, hdfspath):
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
    try:
        os.makedirs(TMP_DIR, 0700)
    except OSError as e:
        if e.errno != errno.EEXIST:
            raise

    for f in sys.argv[1:]:
        print 'processing %s...' % f
        fh = open(f, 'r')
        lines = []
        line = fh.readline()
        while line != '':
            lines = []
            while line != '' and len(lines) < 100000:
                lines.append(line)
            line = fh.readline()
            print 'read %d lines' % len(lines)
            process_lines(f, lines)

    for date in files:
        fpath = files[date].name
        files[date].close()
        print fpath + ' -> ' + HDFS_LOG_BASE_DIR + date.replace('-','/') + '/' + hostname + '.tsv'
        makeHDFSDir(HDFS_LOG_BASE_DIR + date.replace('-','/'))
        write2HDFS(fpath, HDFS_LOG_BASE_DIR + date.replace('-','/') + '/' + hostname + '.tsv')

