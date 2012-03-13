package com.atlantbh.nutch.filter.index.omit;

import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;

import com.atlantbh.nutch.filter.index.omit.config.FilteringType;
import com.atlantbh.nutch.filter.index.omit.config.OmitIndexingFilterConfiguration;
import com.atlantbh.nutch.filter.index.omit.config.OmitIndexingFilterConfigurationEntry;

public class OmitIndexingFilter implements IndexingFilter {

	// Constants
	private static Logger log = Logger.getLogger(OmitIndexingFilter.class);

	// Configuration
	private Configuration configuration;
	private OmitIndexingFilterConfiguration omitIndexingFilterConfiguration;

	public OmitIndexingFilter() {
	}

	private void initConfig() {

		// Initialize configuration
		omitIndexingFilterConfiguration = OmitIndexingFilterConfiguration.getInstance(configuration);
	}

	@Override
	public Configuration getConf() {
		return configuration;
	}

	@Override
	public void setConf(Configuration configuration) {
		this.configuration = configuration;
		initConfig();
	}

	@Override
	public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks) throws IndexingException {

		// Prepare data
		String realUrl = new String(url.getBytes()).substring(0, url.getLength());
		Metadata metadata = parse.getData().getParseMeta();

		if (omitIndexingFilterConfiguration.getFilteringType() == FilteringType.WHITELIST) {
			for (OmitIndexingFilterConfigurationEntry omitIndexingFilterConfigurationEntry : omitIndexingFilterConfiguration.getOmitIndexingFilterConfigurationEntryList()) {
					
				switch (omitIndexingFilterConfigurationEntry.getTarget()) {
					case URL:		
						
						Pattern pattern = Pattern.compile(omitIndexingFilterConfigurationEntry.getRegex());
						if (pattern.matcher(realUrl).matches()) {
							return doc;
						}
						
						break;
					case META_FIELD_PRESENT:
						
						if(metadata.get(FilterUtils.getNullSafe(omitIndexingFilterConfigurationEntry.getName(), "")) != null) {
							return doc;
						}
						
						break;
				}
			}
			
			return null;
		} else if (omitIndexingFilterConfiguration.getFilteringType() == FilteringType.BLACKLIST) {
			for (OmitIndexingFilterConfigurationEntry omitIndexingFilterConfigurationEntry : omitIndexingFilterConfiguration.getOmitIndexingFilterConfigurationEntryList()) {
					
				switch (omitIndexingFilterConfigurationEntry.getTarget()) {
					case URL:
						
						Pattern pattern = Pattern.compile(omitIndexingFilterConfigurationEntry.getRegex());
						if (pattern.matcher(realUrl).matches()) {
							return null;
						}

						break;
					case META_FIELD_PRESENT:
						
						if(metadata.get(FilterUtils.getNullSafe(omitIndexingFilterConfigurationEntry.getName(), "")) != null) {
							return null;
						}
						
						break;
				}
			}
			
			return doc;

		}

		return null;
	}

}
