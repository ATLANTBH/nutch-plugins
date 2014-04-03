package com.atlantbh.nutch.filter.xpath;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.atlantbh.nutch.filter.xpath.XPathHtmlParserFilter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Content.class, ParseResult.class, ParseData.class})
public class XPathHtmlParserFilterTest {

	private XPathHtmlParserFilter xmlHtmlParser;
	private byte[] rawXmlContent;
	private byte[] rawHtmlContent;
	
	// Test data
	private static final String[] testStringArray = {"Harry Potter", "Learning XML"};
	private static final Float[] testFloatArray = {29.99f, 39.95f};
	
	@Before
	public void init() {
		xmlHtmlParser = new XPathHtmlParserFilter();	
	}
	
	@Test
	public void testGetParse() throws IOException {
		
		InputStream xmlInputStream = XPathHtmlParserFilterTest.class.getResourceAsStream("example-content.xml");
		rawXmlContent = new byte[xmlInputStream.available()];
		xmlInputStream.read(rawXmlContent);
		
		// Mock objects
		Content content = PowerMockito.mock(Content.class);	
		Parse parse = mock(Parse.class);
		ParseResult parseResult = mock(ParseResult.class);
		ParseData parseData = PowerMockito.mock(ParseData.class);	
		Configuration configuration = mock(Configuration.class);
		
		// Mock data
		when(content.getContent()).thenReturn(rawXmlContent);
		when(content.getContentType()).thenReturn("application/xml");
		when(content.getUrl()).thenReturn("http://www.test.com/");
		when(parseResult.get(anyString())).thenReturn(parse);
		when(parse.getData()).thenReturn(parseData);
		when(parseData.getParseMeta()).thenReturn(new Metadata());
		when(configuration.get(anyString())).thenReturn("");
		
		when(configuration.getConfResourceAsReader(anyString())).thenReturn(new InputStreamReader(XPathIndexingFilterTest.class.getResourceAsStream("example-xpathfilter-conf.xml")));
		
		xmlHtmlParser.setConf(configuration);
		ParseResult parseResultReturn = xmlHtmlParser.filter(content, parseResult, null, null);
		
		int stringValueIndexCount = 0;
		int floatValueIndexCount = 0;
		
		Metadata metadata = parseResultReturn.get("http://www.test.com/").getData().getParseMeta();
		for(String stringValue : metadata.getValues("testString")) {
			int index = Arrays.binarySearch(testStringArray, stringValue);
			stringValueIndexCount += index;
			assertTrue("String value not found!", stringValueIndexCount >= 0);
		}
		
		for(String floatValue : metadata.getValues("testFloat")) {
			int index = Arrays.binarySearch(testFloatArray, Float.valueOf(floatValue));
			floatValueIndexCount += index;
			assertTrue("Float value not found!", index >= 0);
		}
		
		assertEquals("Not all String values parsed!", 1, stringValueIndexCount);
		assertEquals("Not all Flaot values parsed!", 1, floatValueIndexCount);
	}
	
	@Test
	public void testHtmlCleanupAndParse() throws IOException {
		
		InputStream htmlInputStream = XPathHtmlParserFilterTest.class.getResourceAsStream("example-content.html");
		rawHtmlContent = new byte[htmlInputStream.available()];
		htmlInputStream.read(rawHtmlContent);
		
		// Mock objects
		Content content = PowerMockito.mock(Content.class);	
		Parse parse = mock(Parse.class);
		ParseResult parseResult = mock(ParseResult.class);
		ParseData parseData = PowerMockito.mock(ParseData.class);	
		Configuration configuration = mock(Configuration.class);
		
		// Mock data
		when(content.getContent()).thenReturn(rawHtmlContent);
		when(content.getContentType()).thenReturn("text/html");
		when(content.getUrl()).thenReturn("http://www.test.com/");
		when(parseResult.get(anyString())).thenReturn(parse);
		when(parse.getData()).thenReturn(parseData);
		when(parseData.getParseMeta()).thenReturn(new Metadata());
		when(configuration.get(anyString())).thenReturn("");
		when(configuration.get("parser.character.encoding.default", "UTF-8")).thenReturn("UTF-8");
		when(configuration.getConfResourceAsReader(anyString())).thenReturn(new InputStreamReader(XPathIndexingFilterTest.class.getResourceAsStream("example-xpathfilter-conf2.xml")));
		
		xmlHtmlParser.setConf(configuration);
		ParseResult parseResultReturn = xmlHtmlParser.filter(content, parseResult, null, null);
		Metadata metadata = parseResultReturn.get("http://www.test.com/").getData().getParseMeta();
		
		assertEquals("Error parsing html", "Samir ELJAZOVIĆ", metadata.getValues("articleAuthor")[0]);
		assertEquals("Error parsing html", "Amazon Elastic MapReduce – Part 2 (Amazon S3 Input Format)", metadata.getValues("articleTitle")[0]);
	}
	
}
