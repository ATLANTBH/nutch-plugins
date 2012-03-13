#!/bin/bash
# A script that automates the process of
# GENERTING - FETCHING - PARSING - UPDATING DB

# Get arguments
TOPN="$1"
REPEATTIMES="$2"

if [ $# = 0 ]; then
	echo "Usage: fetch topN repeatTimes"
	exit 1
fi

for (( i=1; i<=$REPEATTIMES; i++ ))
do
	echo "ITERATION: $i"
	echo "GENERATING"
	
	bin/nutch generate crawldb crawldb/segments -topN $TOPN
	seg=`ls -d crawldb/segments/* | tail -1`

	echo "FETCHING"
	bin/nutch fetch $seg
	
	echo "PARSING"
	bin/nutch parse $seg
	
	echo "UPDATING DB"
	bin/nutch updatedb crawldb $seg
	
	# BUG - DIRECTORY NEEDS TO BE MOVED
	cur=`ls -d crawldb/current/* | tail -1`
	if [ "$cur" != "crawldb/current/part-00000" ]
	then
		shopt -s dotglob
		mv $cur/part-00000/* $cur
		shopt -u dotglob
		rm -f -r $cur/part-00000
	fi
	
done