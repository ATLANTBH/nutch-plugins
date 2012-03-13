package com.atlantbh.nutch.index.alternativedataflow.flow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;
import com.Ostermiller.util.ExcelCSVPrinter;
import com.atlantbh.nutch.index.alternativedataflow.FilterUtils;
import com.atlantbh.nutch.index.alternativedataflow.conf.Entry;
import com.atlantbh.nutch.index.alternativedataflow.conf.Field;

public class CsvDataFlow implements DataFlow {
	
	// Constants
	private static final Logger log = Logger.getLogger(CsvDataFlow.class);
	
	// Configuration constants
	public static final QName DESTINATION = new QName("destination");
	public static final QName NAME = new QName("name");
	public static final QName SEPERATOR = new QName("seperator");
	public static final QName QUOTE_CHARACTER = new QName("quoteCharacter");
	public static final QName APPEND_TIMESTAMP = new QName("appendTimestamp");
	public static final QName ADD_FIELD_NAMES_TO_FIRST_LINE = new QName("addFieldNamesToFirstLine");
	public static final QName STYLE = new QName("style");
	
	// Init data
	private Configuration configuration;
	private List<Entry> entryList;

	// Internal data
	private Map<String, CSVPrint> nameCsvPrintMap = new HashMap<String, CSVPrint>();
	private boolean initialized = false;
	
	public CsvDataFlow() {
	}

	private void initWriters() throws IOException {

		// Check if it already initialized
		// because this function is called everytime a NutchDocument is being
		// processed
		if (!initialized) {

			// Initialize timestamp
			long timestamp = new Date().getTime();
			
			// Iterate trough the entries
			for (Entry entry : entryList) {
				char seperator = FilterUtils.getNullSafe(entry.getParameterMap().get(SEPERATOR), ",").charAt(0);
				char quoteCharacter = FilterUtils.getNullSafe(entry.getParameterMap().get(QUOTE_CHARACTER), "\"").charAt(0);
				String style = FilterUtils.getNullSafe(entry.getParameterMap().get(STYLE), "UNIX");

				// Check if the file needs a timestamp
				File csvFile;
				if (Boolean.valueOf(FilterUtils.getNullSafe(entry.getParameterMap().get(APPEND_TIMESTAMP), "false"))) {
					csvFile = new File(entry.getParameterMap().get(DESTINATION) + File.separator + entry.getParameterMap().get(NAME) + timestamp + ".csv");
				} else {
					csvFile = new File(entry.getParameterMap().get(DESTINATION) + File.separator + entry.getParameterMap().get(NAME) + ".csv");
				}

				// If the file already exists delete it
				if (csvFile.exists()) {
					csvFile.delete();
				}

				// Create the new cvs file
				csvFile.createNewFile();

				// Create the printer
				CSVPrint csvPrint = null;
				if("UNIX".equals(style)) {
					csvPrint = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), '#', quoteCharacter, seperator, false, true);
				} else if("EXCEL".equals(style)) {
					csvPrint = new ExcelCSVPrinter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), quoteCharacter, seperator, false, true);
				}
				nameCsvPrintMap.put(entry.getParameterMap().get(NAME), csvPrint);

				// If this flag is set add the first
				if (Boolean.valueOf(FilterUtils.getNullSafe(entry.getParameterMap().get(ADD_FIELD_NAMES_TO_FIRST_LINE), "false"))) {

					String[] fieldNames = new String[entry.getFieldList().size()];
					List<Field> fieldList = entry.getFieldList();
					for (int i = 0; i < fieldList.size(); i++) {
						if(fieldList.get(i).getAlias() != null) {
							fieldNames[i] = fieldList.get(i).getAlias();
						} else {
							fieldNames[i] = fieldList.get(i).getName();
						}
						
					}

					// Write field names to CSV file
					csvPrint.println(fieldNames);
				}

			}

			// Set the flag to initialized
			initialized = true;
		}
	}

	@Override
	public void init(Configuration configuration, List<Entry> entryList) {
		this.configuration = configuration;
		this.entryList = entryList;
	}
	
	@Override
	public void destroy() {
		for (String name : nameCsvPrintMap.keySet()) {
			try {
				
				CSVPrint csvPrint = nameCsvPrintMap.get(name);
				csvPrint.close();
			} catch (IOException e) {
				// DOESN'T MATTER IF IT THROWS AN EXCEPTION
			}
		}
	}

	@Override
	public void processData(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks) {
		
		// Get metadata
		Metadata metadata = parse.getData().getParseMeta();
		
		try {
			
			// Initialize the writers
			// Only on the first execution
			initWriters();
			
			for(Entry entry : entryList) {
				
				CSVPrint csvPrint = nameCsvPrintMap.get(entry.getParameterMap().get(NAME));
				
				String[] fieldValues = new String[entry.getFieldList().size()];
				List<Field> fieldList = entry.getFieldList();
				for (int i = 0; i < fieldList.size(); i++) {
					fieldValues[i] = FilterUtils.getNullSafe(metadata.get(fieldList.get(i).getName()), "");
				}
				
				// Write field values to CSV file
				csvPrint.println(fieldValues);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
}
