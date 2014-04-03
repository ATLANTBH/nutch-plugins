package com.atlantbh.nutch.filter.xpath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

import com.atlantbh.nutch.filter.xpath.config.FieldType;
import com.atlantbh.nutch.filter.xpath.config.XPathFilterConfiguration;
import com.atlantbh.nutch.filter.xpath.config.XPathIndexerProperties;
import com.atlantbh.nutch.filter.xpath.config.XPathIndexerPropertiesField;

/**
 * Second stage of {@link XPathHtmlParserFilter} the IndexingFilter.
 * It takes the prepared data located in the metadata and indexes
 * it to solr.
 * 
 * 
 * @author Emir Dizdarevic
 * @version 1.4
 * @since Apache Nutch 1.4
 *
 */
public class XPathIndexingFilter implements IndexingFilter {

	// Constants
	private static final Logger log = Logger.getLogger(XPathIndexingFilter.class);
	
	// Configuration
	private Configuration configuration;
	private XPathFilterConfiguration xpathFilterConfiguration;
	
	public XPathIndexingFilter() {}
	
	private void initConfig() {
		
		// Initialize configuration
		xpathFilterConfiguration  = XPathFilterConfiguration.getInstance(configuration);
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
		Metadata metadata = parse.getData().getParseMeta();
		
		List<XPathIndexerProperties> xPathIndexerPropertiesList = xpathFilterConfiguration.getXPathIndexerPropertiesList();
		for(XPathIndexerProperties xPathIndexerProperties : xPathIndexerPropertiesList) {
			
			if(FilterUtils.isMatch(xPathIndexerProperties.getPageUrlFilterRegex(), new String(url.getBytes()).substring(0, url.getLength()))) {
				
				List<XPathIndexerPropertiesField> xPathIndexerPropertiesFieldList = xPathIndexerProperties.getXPathIndexerPropertiesFieldList();
				for(XPathIndexerPropertiesField xPathIndexerPropertiesField : xPathIndexerPropertiesFieldList) {
					
					FieldType type = xPathIndexerPropertiesField.getType();
					for(String stringValue : metadata.getValues(xPathIndexerPropertiesField.getName())) {
						
						Object value;
						switch(type) {
							case STRING:
								value = stringValue;
								break;
							case INTEGER:
								value = Integer.valueOf(stringValue);
								break;
							case LONG:
								value = Long.valueOf(stringValue);
								break;
							case DOUBLE:
								value = Double.valueOf(stringValue);
								break;
							case FLOAT:
								value = Float.valueOf(stringValue);
								break;
							case BOOLEAN:
								value = Boolean.valueOf(stringValue);
								break;
							case DATE:
								
								// Create SimpleDateFormat object to parse string
								String dateFormat = xPathIndexerPropertiesField.getDateFormat() == null?"dd.MM.yyyy":xPathIndexerPropertiesField.getDateFormat();
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
								
								// If not parseable set the date: 1. January 1970.
								try {
									value = simpleDateFormat.parseObject(stringValue);
								} catch (ParseException e) {
									value = new Date(0);
								} 
								
								break;
							default:
								log.warn(String.format("Type '%s' not supported, value will be interpreted as String", type));
								value = stringValue;
								break;
						} 
						
						// Add field
						doc.add(xPathIndexerPropertiesField.getName(), value);
						if (log.isDebugEnabled()) {
							log.debug(String.format("Added field with name %s and value %s", xPathIndexerPropertiesField.getName(), value));
						}
					}
				}
			}
		}
		
		return doc;
	}
}
