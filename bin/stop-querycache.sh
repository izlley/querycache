#!/bin/bash
pid=$(ps aux | grep com.skplanet.querycache.server.QueryCacheServer | grep -v grep | awk '{ print $2 }')

echo "killing pid $pid"
kill $pid

