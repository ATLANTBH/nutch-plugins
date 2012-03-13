package com.atlantbh.nutch.filter.index.omit;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.atlantbh.nutch.filter.index.omit.OmitIndexingFilter;

@RunWith(BlockJUnit4ClassRunner.class)
public class OmitIndexingFilterTest {

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	private OmitIndexingFilter omitIndexingFilter;
	private NutchDocument nutchDocumentIn = new NutchDocument();
	private Metadata metadata = new Metadata();
	
	private static final String[] testStringArray = {"test1", "test2"};
	private static final Float[] testFloatArray = {1.2f, 2.3f};
	private static final Date[] testDateArray = {new Date(2001, 3, 15), new Date(2003, 8, 21)};
	
	@Before
	public void init() {
		
		metadata.add("testString", testStringArray[0]);
		metadata.add("testString", testStringArray[1]);
		metadata.add("testFloat", String.valueOf(testFloatArray[0]));
		metadata.add("testFloat", String.valueOf(testFloatArray[1]));
		metadata.add("testDate", simpleDateFormat.format(testDateArray[0]));
		metadata.add("testDate", simpleDateFormat.format(testDateArray[1]));
		
		nutchDocumentIn.add("testString", testStringArray[0]);
		nutchDocumentIn.add("testString", testStringArray[1]);		
		nutchDocumentIn.add("testFloat", testFloatArray[0]);
		nutchDocumentIn.add("testFloat", testFloatArray[1]);
		nutchDocumentIn.add("testDate", testDateArray[0]);
		nutchDocumentIn.add("testDate", testDateArray[1]);
		
		omitIndexingFilter = new OmitIndexingFilter();
	}
	
	@Test
	public void testFilter() throws IndexingException {
		
		// Prepare data
		Parse parse = mock(Parse.class);
		Configuration configuration = mock(Configuration.class);
		ParseData parseData = new ParseData();
		parseData.setParseMeta(metadata);

		// Mock data
		when(parse.getData()).thenReturn(parseData);
		when(configuration.get(anyString())).thenReturn("");
		when(configuration.getConfResourceAsReader(anyString())).thenReturn(new InputStreamReader(OmitIndexingFilterTest.class.getResourceAsStream("example-omit-indexfilter-conf.xml")));
		
		omitIndexingFilter.setConf(configuration);
		NutchDocument nutchDocumentOut = omitIndexingFilter.filter(nutchDocumentIn, parse, new Text("http://www.test.ba/"), null, null);
		
		assertNull("Document unsuccessfuly filtered!", nutchDocumentOut);
		
		nutchDocumentOut = omitIndexingFilter.filter(nutchDocumentIn, parse, new Text("http://www.test.com/"), null, null);
		assertSame("Document unsuccessfuly filtered!", nutchDocumentIn, nutchDocumentOut);
		
	}
	
}
