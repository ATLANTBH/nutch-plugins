#!/bin/bash
# A script which deletes al previous parsed data and reparses
# each segment again.

for i in $( ls -d crawldb/segments/* | sed -e 's/ /\\ /g' ); do
    rm -r $i/crawl_parse
	rm -r $i/parse_data
	rm -r $i/parse_text
	bin/nutch parse $i	
done

echo
echo "Done..."
echo