package com.atlantbh.nutch.index.alternativedataflow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.atlantbh.nutch.index.alternativedataflow.conf.Entry;
import com.atlantbh.nutch.index.alternativedataflow.conf.Field;
import com.atlantbh.nutch.index.alternativedataflow.flow.CsvDataFlow;
import com.atlantbh.nutch.index.alternativedataflow.flow.CsvDataFlowTest;

@RunWith(BlockJUnit4ClassRunner.class)
public class AlternativeDataFlowIndexingFilterTest {

	
	private AlternativeDataFlowIndexingFilter alternativeDataFlowIndexingFilter;

	// Test data
	private static final String[] testStringArray = { "test1", "te,,st2", "te\"st3" };
	private static final Float[] testFloatArray = { 1.2f, 2.3f };
	private static final String[] testDateArray = { "15.03.2001",  "21.08.2003"};
	private static String preCreatedCsv;
	
	// Need to run
	private static final Metadata metadata1 = new Metadata();
	private static final Metadata metadata2 = new Metadata();
	private static final Metadata metadata3 = new Metadata();
	private static File tempDir;

	@Before
	public void init() throws IOException {
		alternativeDataFlowIndexingFilter = new AlternativeDataFlowIndexingFilter();
		
		// Initialize metadata
		metadata1.add("testString", testStringArray[0]);
		metadata1.add("testFloat", String.valueOf(testFloatArray[0]));
		metadata1.add("testDate", testDateArray[0]);
		metadata2.add("testString", testStringArray[1]);
		metadata2.add("testFloat", String.valueOf(testFloatArray[1]));
		metadata2.add("testDate", testDateArray[1]);
		metadata3.add("testString", testStringArray[2]);
		
		tempDir = new File("C:\\csvs");
		
		// Precreated CSV to match against
		InputStream preCreatedCsvInputStream = CsvDataFlowTest.class.getResourceAsStream("example.csv");
		byte[] preCreatedCsvData = new byte[preCreatedCsvInputStream.available()];
		preCreatedCsvInputStream.read(preCreatedCsvData);
		preCreatedCsv = new String(preCreatedCsvData);
	}

	@Test
	@Ignore("It works but it can be tested only manually :(")
	public void testProcessData() throws IOException, IndexingException {

		// Prepare data
		NutchDocument nutchDocumentIn = new NutchDocument();
		ParseData parseData = new ParseData();
		CrawlDatum crawDatum = new CrawlDatum();
		Parse parse = mock(Parse.class);
		Configuration configuration = mock(Configuration.class);
		
		// Mock objects
		when(configuration.get(anyString())).thenReturn("");
		when(configuration.getConfResourceAsReader(anyString())).thenReturn(new InputStreamReader(AlternativeDataFlowIndexingFilterTest.class.getResourceAsStream("example.alternativedataflow-indexfilter-conf.xml")));
			
		parseData.setParseMeta(metadata1);
		when(parse.getData()).thenReturn(parseData);
		
		alternativeDataFlowIndexingFilter.setConf(configuration);
		alternativeDataFlowIndexingFilter.filter(nutchDocumentIn, parse, null, crawDatum, null);
		
		parseData.setParseMeta(metadata2);
		alternativeDataFlowIndexingFilter.filter(nutchDocumentIn, parse, null, crawDatum, null);
	
		parseData.setParseMeta(metadata3);
		alternativeDataFlowIndexingFilter.filter(nutchDocumentIn, parse, null, crawDatum, null);
		
		File[] files = tempDir.listFiles();
		InputStream createdCsvInputStream = new FileInputStream(files[0]);
		byte[] createdCsvData = new byte[createdCsvInputStream.available()];
		createdCsvInputStream.read(createdCsvData);
		String createdCsv = new String(createdCsvData);
		
		assertEquals("Something went wrong with the creation of the csv file!", 1, files.length);
		assertEquals("CSV is not as predicted!", preCreatedCsv, createdCsv);
	}
	
}
