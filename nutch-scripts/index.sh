#!/bin/bash
# A simple script to automate the process of inverting links and indexing


echo "INVERTING LINKS"
bin/nutch invertlinks crawldb/linkdb -dir crawldb/segments

# BUG - DIRECTORY NEEDS TO BE MOVED
ldb=`ls -d crawldb/linkdb/* | tail -1`
if [ "$ldb" != "crawldb/linkdb/part-00000" ]
then
	shopt -s dotglob
	mv $ldb/part-00000/* $ldb
	shopt -u dotglob
	rm -f -r $ldb/part-00000
fi

echo "SOLR INDEXING"
bin/nutch solrindex http://127.0.0.1:8983/solr/ crawldb -linkdb crawldb/linkdb crawldb/segments/*