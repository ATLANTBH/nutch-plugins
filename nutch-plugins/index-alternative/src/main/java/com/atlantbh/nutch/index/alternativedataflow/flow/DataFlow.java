package com.atlantbh.nutch.index.alternativedataflow.flow;

import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;

import com.atlantbh.nutch.index.alternativedataflow.conf.Entry;

public interface DataFlow {

	/**
	 * It's only called the first time when this DataFlow is initialized.
	 * 
	 * @param configuration The reference to the hadoop configuration instance.
	 * @param entryList The list of entries that matches this data flow type.
	 */
	public void init(Configuration configuration, List<Entry> entryList);
	
	/**
	 * It's called only once. When the JVM shuts down. 
	 * It's called via a shutdown hook.
	 * 
	 */
	public void destroy();
	
	/**
	   * Redirect all data to this flow.
	   * 
	   * @param doc document instance for collecting fields
	   * @param parse parse data instance
	   * @param url page url
	   * @param datum crawl datum for the page
	   * @param inlinks page inlinks
	   * @throws IndexingException
	   */
	public void processData(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks) throws IndexingException;
}
