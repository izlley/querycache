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

            date = line[:10]
            if date in files:
                f = files[date]
            else:
                f = open(TMP_DIR + date, 'w')
                print_log('new date ' + date)
                files[date] = f

            f.write(writestr.encode('utf-8'))
        except (ValueError, AttributeError, KeyError) as e:
            print_log('ERROR: Can\'t parse line.\n%s\n%s' % (line, str(e)));
        except Error as e:
            print_log('ERROR: Can\'t parse line. (Unexpected Error)\n%s\n%s' % (line, str(e)));

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

