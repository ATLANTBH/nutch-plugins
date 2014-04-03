package com.atlantbh.nutch.filter.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import com.atlantbh.nutch.filter.xpath.XPathIndexingFilter;
import com.ibm.icu.util.GregorianCalendar;

@RunWith(BlockJUnit4ClassRunner.class)
public class XPathIndexingFilterTest {
	
	private XPathIndexingFilter xmlHtmlIndexingFilter;
	private Metadata metadata;
	
	private static final String[] testStringArray = {"test1", "test2", "test3"};
	private static final Float[] testFloatArray = {1.2f, 2.3f};
	private static final Date[] testDateArray = {new GregorianCalendar(2001, 3, 15).getTime(), new GregorianCalendar(2003, 8, 21).getTime()};
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	@Before
	public void init() {
		metadata = new Metadata();
	
		metadata.add("testString", testStringArray[0]);
		metadata.add("testString", testStringArray[1]);
		metadata.add("testString", testStringArray[2]);
		metadata.add("testFloat", String.valueOf(testFloatArray[0]));
		metadata.add("testFloat", String.valueOf(testFloatArray[1]));
		metadata.add("testDate", simpleDateFormat.format(testDateArray[0]));
		metadata.add("testDate", simpleDateFormat.format(testDateArray[1]));
		
		xmlHtmlIndexingFilter = new XPathIndexingFilter();
	}
	
	@Test
	public void testFilter() throws IndexingException {
		
		// Prepare data
		NutchDocument nutchDocumentIn = new NutchDocument();
		Parse parse = mock(Parse.class);
		ParseData parseData = new ParseData();
		parseData.setParseMeta(metadata);
		Configuration configuration = mock(Configuration.class);
		
		// Mock data
		when(parse.getData()).thenReturn(parseData);
		when(configuration.get(anyString())).thenReturn("");
		when(configuration.getConfResourceAsReader(anyString())).thenReturn(new InputStreamReader(XPathIndexingFilterTest.class.getResourceAsStream("example-xpathfilter-conf.xml")));
		
		xmlHtmlIndexingFilter.setConf(configuration);
		NutchDocument nutchDocumentOut = xmlHtmlIndexingFilter.filter(nutchDocumentIn, parse, new Text("www.test.com"), null, null);
		
		int stringValueIndexCount = 0;
		int floatValueIndexCount = 0;
		int dateValueIndexCount = 0;
		
		for(String fieldName : nutchDocumentOut.getFieldNames()) {
			
			for(Object value : nutchDocumentOut.getField(fieldName).getValues()) {
				
				if(fieldName.equals("testString")) {
					int index = Arrays.binarySearch(testStringArray, value);
					stringValueIndexCount += index;
					
					assertTrue(index >= 0);
					assertTrue(value instanceof String);
				} else if(fieldName.equals("testFloat"))  {
					int index = Arrays.binarySearch(testFloatArray, value);
					floatValueIndexCount += index;
					
					assertTrue(index >= 0);
					assertTrue(value instanceof Float);
				} else if(fieldName.equals("testDate"))  {	
					int index = Arrays.binarySearch(testDateArray, value);	
					dateValueIndexCount += index;
					
					assertTrue(index >= 0);
					assertTrue(value instanceof Date);
				}
			}		
		}
		
		assertEquals("Not all String values parsed!", 3, stringValueIndexCount);
		assertEquals("Not all Flaot values parsed!", 1, floatValueIndexCount);
		assertEquals("Not all Flaot values parsed!", 1, dateValueIndexCount);
	}
}
