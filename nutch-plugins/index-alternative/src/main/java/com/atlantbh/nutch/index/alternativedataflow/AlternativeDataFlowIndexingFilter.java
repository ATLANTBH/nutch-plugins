package com.atlantbh.nutch.index.alternativedataflow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;

import com.atlantbh.nutch.index.alternativedataflow.conf.AlternativeDataFlowIndexingFilterConfiguration;
import com.atlantbh.nutch.index.alternativedataflow.conf.Entry;
import com.atlantbh.nutch.index.alternativedataflow.flow.CsvDataFlow;
import com.atlantbh.nutch.index.alternativedataflow.flow.DataFlow;

public class AlternativeDataFlowIndexingFilter implements IndexingFilter {

	// Constants
	private static Logger log = Logger.getLogger(AlternativeDataFlowIndexingFilter.class);

	// Configuration
	private Configuration configuration;
	private AlternativeDataFlowIndexingFilterConfiguration alternativeDataFlowConfiguration;

	// Internal data
	private boolean initialized = false;

	// **********************************
	// TEMPORARY IDEA OF CONFIGURATION
	// **********************************
	private static final Map<String, DataFlow> dataFlowMap = new HashMap<String, DataFlow>();
	static {
		dataFlowMap.put("CSV", new CsvDataFlow());

		// Call the destroy method on JVM shutdown
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {

				// Iterate trough the dataFlowMap and call destroy method
				for (String dataFlowId : dataFlowMap.keySet()) {

					DataFlow dataFlow = dataFlowMap.get(dataFlowId);
					dataFlow.destroy();
				}
			}
		}));
	}

	public AlternativeDataFlowIndexingFilter() {
	}

	private void initConfig() {

		// Initialize configuration
		alternativeDataFlowConfiguration = AlternativeDataFlowIndexingFilterConfiguration.getInstance(configuration);
	}

	private void initDataFlows() {

		// Initialize only once
		if (!initialized) {

			// Maps the data flow identifers with it's configuration entries
			Map<String, List<Entry>> dataFlowEntryListMap = new HashMap<String, List<Entry>>();
			for (Entry entry : alternativeDataFlowConfiguration.getEntryList()) {

				// Get or create an entry list
				List<Entry> entryList;
				if (dataFlowEntryListMap.containsKey(entry.getDataFlow())) {
					entryList = dataFlowEntryListMap.get(entry.getDataFlow());
				} else {
					entryList = new LinkedList<Entry>();
					dataFlowEntryListMap.put(entry.getDataFlow(), entryList);
				}

				entryList.add(entry);
			}

			// Iterate trough the dataFlowMap and initialize it
			for (String dataFlowId : dataFlowMap.keySet()) {

				DataFlow dataFlow = dataFlowMap.get(dataFlowId);
				List<Entry> entryList = dataFlowEntryListMap.get(dataFlowId);
				dataFlow.init(configuration, entryList);
			}

			initialized = true;
		}
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

		// Initialize the data flows
		// Executed only the first time
		initDataFlows();

		// Iterate trough the dataFlowMap and redirect the data flow to them
		for (String dataFlowId : dataFlowMap.keySet()) {

			DataFlow dataFlow = dataFlowMap.get(dataFlowId);
			dataFlow.processData(doc, parse, url, datum, inlinks);
		}

		return doc;
	}

}
