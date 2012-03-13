package com.atlantbh.nutch.index.alternativedataflow.flow;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.powermock.modules.junit4.legacy.PowerMockRunner;

import com.atlantbh.nutch.index.alternativedataflow.conf.Entry;
import com.atlantbh.nutch.index.alternativedataflow.conf.Field;

@RunWith(BlockJUnit4ClassRunner.class)
public class CsvDataFlowTest {

	private CsvDataFlow csvDataFlow;

	// Test data
	private static final String[] testStringArray = { "testđžš1", "te,,st2", "te\"st3" };
	private static final Float[] testFloatArray = { 1.2f, 2.3f };
	private static final String[] testDateArray = { "15.03.2001",  "21.08.2003"};
	private static String preCreatedCsv;
	
	// Need to run
	private static final List<Entry> entryList = new LinkedList<Entry>();
	private static final Metadata metadata1 = new Metadata();
	private static final Metadata metadata2 = new Metadata();
	private static final Metadata metadata3 = new Metadata();
	private static File tempDir;

	@Before
	public void init() throws IOException {
		csvDataFlow = new CsvDataFlow();
		
		// Initialize metadata
		metadata1.add("testString", testStringArray[0]);
		metadata1.add("testFloat", String.valueOf(testFloatArray[0]));
		metadata1.add("testDate", testDateArray[0]);
		metadata2.add("testString", testStringArray[1]);
		metadata2.add("testFloat", String.valueOf(testFloatArray[1]));
		metadata2.add("testDate", testDateArray[1]);
		metadata3.add("testString", testStringArray[2]);
		
		// Create temp directory
		tempDir = File.createTempFile("test", null);
		tempDir.delete();
		tempDir.mkdir();
		
		// Precreated CSV to match against
		InputStream preCreatedCsvInputStream = CsvDataFlowTest.class.getResourceAsStream("example.csv");
		byte[] preCreatedCsvData = new byte[preCreatedCsvInputStream.available()];
		preCreatedCsvInputStream.read(preCreatedCsvData);
		preCreatedCsv = new String(preCreatedCsvData);
		
		// Initialize entry list
		Map<QName, String> parameterMap = new HashMap<QName, String>();
		parameterMap.put(CsvDataFlow.NAME, "test");
		parameterMap.put(CsvDataFlow.DESTINATION, tempDir.getAbsolutePath());
		parameterMap.put(CsvDataFlow.ADD_FIELD_NAMES_TO_FIRST_LINE, "true");
		parameterMap.put(CsvDataFlow.APPEND_TIMESTAMP, "true");
		
		Entry entry = new Entry("CSV");
		entry.setParameterMap(parameterMap);
		entry.setFieldList(Arrays.asList(new Field[] {new Field("testString"), new Field("testFloat"), new Field("testDate")}));
		entryList.add(entry);
	}

	@Test
	public void testProcessData() throws IOException {

		// Prepare data
		NutchDocument nutchDocumentIn = new NutchDocument();
		CrawlDatum crawDatum = new CrawlDatum();
		Parse parse = mock(Parse.class);
		Configuration configuration = mock(Configuration.class);
		
		ParseData parseData = new ParseData();
		
		parseData.setParseMeta(metadata1);
		when(parse.getData()).thenReturn(parseData);
		
		csvDataFlow.init(configuration, entryList);
		csvDataFlow.processData(nutchDocumentIn, parse, null, crawDatum, null);
		
		parseData.setParseMeta(metadata2);
		csvDataFlow.processData(nutchDocumentIn, parse, null, crawDatum, null);
	
		parseData.setParseMeta(metadata3);
		csvDataFlow.processData(nutchDocumentIn, parse, null, crawDatum, null);
		
		File[] files = tempDir.listFiles();
		InputStream createdCsvInputStream = new FileInputStream(files[0]);
		byte[] createdCsvData = new byte[createdCsvInputStream.available()];
		createdCsvInputStream.read(createdCsvData);
		String createdCsv = new String(createdCsvData);
		
		assertEquals("Something went wrong with the creation of the csv file!", 1, files.length);
		assertEquals("CSV is not as predicted!", preCreatedCsv, createdCsv);
		
		csvDataFlow.destroy();
	}

}
