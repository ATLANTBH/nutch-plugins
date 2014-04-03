package com.atlantbh.nutch.filter.xpath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.protocol.Content;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.atlantbh.nutch.filter.xpath.config.XPathFilterConfiguration;
import com.atlantbh.nutch.filter.xpath.config.XPathIndexerProperties;
import com.atlantbh.nutch.filter.xpath.config.XPathIndexerPropertiesField;

/**
 * A Xml-Html xpath filter implementation that fetches data
 * from the content, depending on the supplied xpath,
 * and prepares it for the {@link XPathIndexingFilter} to
 * index it into solr.
 * 
 * @author Emir Dizdarevic
 * @version 1.4
 * @since Apache Nutch 1.4
 */
public class XPathHtmlParserFilter implements HtmlParseFilter {
	
	// Constants
	private static final Logger log = Logger.getLogger(XPathHtmlParserFilter.class);
	private static final List<String> htmlMimeTypes = Arrays.asList(new String[] {"text/html", "application/xhtml+xml"});
	
	// OLD WAY TO DETERMIN IF IT'S AN XML FORMAT
	//private static final List<String> xmlMimeTypes = Arrays.asList(new String[] {"text/xml", "application/xml"});
	
	// Configuration
	private Configuration configuration;
	private XPathFilterConfiguration xpathFilterConfiguration;
	private String defaultEncoding;
	
	// Internal data
	private HtmlCleaner cleaner;
	private DomSerializer domSerializer;
	private DocumentBuilder documentBuilder;

	public XPathHtmlParserFilter() {
		init();
	}

	private void init() {
		
		// Initialize HTMLCleaner
		cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);
		props.setNamespacesAware(false);
		
		// Initialize DomSerializer
		domSerializer = new DomSerializer(props);
		
		// Initialize xml parser		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// THIS CAN NEVER HAPPEN
		}
	}
	
	private void initConfig() {

		// Initialize configuration
		xpathFilterConfiguration  = XPathFilterConfiguration.getInstance(configuration);
		defaultEncoding = configuration.get("parser.character.encoding.default", "UTF-8");
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public ParseResult filter(Content content, ParseResult parseResult, HTMLMetaTags metaTags, DocumentFragment doc) {
		Metadata metadata = parseResult.get(content.getUrl()).getData().getParseMeta();
		byte[] rawContent = content.getContent();
		
		try {
			Document cleanedXmlHtml = documentBuilder.newDocument();
			if(htmlMimeTypes.contains(content.getContentType())) {
				
				// Create reader so the input can be read in UTF-8
				Reader rawContentReader = new InputStreamReader(new ByteArrayInputStream(rawContent), FilterUtils.getNullSafe(metadata.get(Metadata.ORIGINAL_CHAR_ENCODING), defaultEncoding));
				
				// Use the cleaner to "clean" the HTML and return it as a TagNode object
				TagNode tagNode = cleaner.clean(rawContentReader);
				cleanedXmlHtml = domSerializer.createDOM(tagNode);
			} else if(content.getContentType().contains(new StringBuilder("/xml")) || content.getContentType().contains(new StringBuilder("+xml"))) {
				
				// Parse as xml - don't clean
				cleanedXmlHtml = documentBuilder.parse(new InputSource(new ByteArrayInputStream(rawContent)));	
			} 
			
			// Once the HTML is cleaned, then you can run your XPATH expressions on the node, 
			// which will then return an array of TagNode objects 
			List<XPathIndexerProperties> xPathIndexerPropertiesList = xpathFilterConfiguration.getXPathIndexerPropertiesList();
			for(XPathIndexerProperties xPathIndexerProperties : xPathIndexerPropertiesList) {
				
				
				//****************************
				// CORE XPATH EVALUATION
				//****************************
				if(pageToProcess(xPathIndexerProperties, cleanedXmlHtml, content.getUrl())) {
					
					List<XPathIndexerPropertiesField> xPathIndexerPropertiesFieldList = xPathIndexerProperties.getXPathIndexerPropertiesFieldList();
					for(XPathIndexerPropertiesField xPathIndexerPropertiesField : xPathIndexerPropertiesFieldList) {
						
						// Evaluate xpath			
						XPath xPath = new DOMXPath(xPathIndexerPropertiesField.getXPath());
						List nodeList = xPath.selectNodes(cleanedXmlHtml);
						
						// Trim?
						boolean trim = FilterUtils.getNullSafe(xPathIndexerPropertiesField.getTrimXPathData(), true);
						
						if(FilterUtils.getNullSafe(xPathIndexerPropertiesField.isConcat(), false)) {
								
							// Iterate trough all found nodes
							String value = new String();
							String concatDelimiter = FilterUtils.getNullSafe(xPathIndexerPropertiesField.getConcatDelimiter(), "");
							for (Object node : nodeList) {

								// Extract data	
								String tempValue = FilterUtils.extractTextContentFromRawNode(node);
								tempValue = filterValue(tempValue, trim);
								
								// Concatenate tempValue to value
								if(tempValue != null) {
									if(value.isEmpty()) {
										value = tempValue;
									} else {
										value = value + concatDelimiter + tempValue;
									}
								}
							}
							
							// Add the extracted data to meta
							if(value != null) {
								metadata.add(xPathIndexerPropertiesField.getName(), value);
							}
							
						} else {
							
							// Iterate trough all found nodes
							for (Object node : nodeList) {

								// Add the extracted data to meta
								String value = FilterUtils.extractTextContentFromRawNode(node);					
								value = filterValue(value, trim);
								if(value != null) {
									metadata.add(xPathIndexerPropertiesField.getName(), value);	
								}
							}
						}
						
					}
				}
			}
			
		} catch (IOException e) {
			// This can never happen because it's an in memory stream
		} catch(PatternSyntaxException e) {
			System.err.println(e.getMessage());
			log.error("Error parsing urlRegex: " + e.getMessage());
			return new ParseStatus(ParseStatus.FAILED, "Error parsing urlRegex: " + e.getMessage()).getEmptyParseResult(content.getUrl(), configuration);
		} catch (ParserConfigurationException e) {
			System.err.println(e.getMessage());
			log.error("HTML Cleaning error: " + e.getMessage());
			return new ParseStatus(ParseStatus.FAILED, "HTML Cleaning error: " + e.getMessage()).getEmptyParseResult(content.getUrl(), configuration);
		} catch (SAXException e) {
			System.err.println(e.getMessage());
			log.error("XML parsing error: " + e.getMessage());
			return new ParseStatus(ParseStatus.FAILED, "XML parsing error: " + e.getMessage()).getEmptyParseResult(content.getUrl(), configuration);
		} catch (JaxenException e) {
			System.err.println(e.getMessage());
			log.error("XPath error: " + e.getMessage());
			return new ParseStatus(ParseStatus.FAILED, "XPath error: " + e.getMessage()).getEmptyParseResult(content.getUrl(), configuration);
		}
		
		return parseResult;
	}
	
	
	@SuppressWarnings("rawtypes")
	private boolean pageToProcess(XPathIndexerProperties xPathIndexerProperties, Document cleanedXmlHtml, String url) throws JaxenException {

		boolean processPage = true;

		// *************************************
		// URL REGEX CONTENT PAGE FILTERING
		// *************************************
		processPage = processPage && FilterUtils.isMatch(xPathIndexerProperties.getPageUrlFilterRegex(), url);

		// Check return status
		if (!processPage) {
			return false;
		}

		// *************************************
		// XPATH CONTENT PAGE FILTERING
		// *************************************

		if (xPathIndexerProperties.getPageContentFilterXPath() != null) {
			XPath xPathPageContentFilter = new DOMXPath(xPathIndexerProperties.getPageContentFilterXPath());
			List pageContentFilterNodeList = xPathPageContentFilter.selectNodes(cleanedXmlHtml);
			boolean trim = FilterUtils.getNullSafe(xPathIndexerProperties.isTrimPageContentFilterXPathData(), true);
			
			if (FilterUtils.getNullSafe(xPathIndexerProperties.isConcatPageContentFilterXPathData(), false)) {

				// Iterate trough all found nodes
				String value = new String();
				String concatDelimiter = FilterUtils.getNullSafe(xPathIndexerProperties.getConcatPageContentFilterXPathDataDelimiter(), "");

				for (Object node : pageContentFilterNodeList) {

					// Extract data
					String tempValue = FilterUtils.extractTextContentFromRawNode(node);
					tempValue = filterValue(tempValue, trim);

					// Concatenate tempValue to value
					if(tempValue != null) {
						if (value.isEmpty()) {
							value = tempValue;
						} else {
							value = value + concatDelimiter + tempValue;
						}
					}
				}

				processPage = processPage && FilterUtils.isMatch(xPathIndexerProperties.getPageContentFilterRegex(), value);
			} else {
				for (Object node : pageContentFilterNodeList) {

					// Add the extracted data to meta
					String value = FilterUtils.extractTextContentFromRawNode(node);
					value = filterValue(value, trim);
					if(value != null) {
						processPage = processPage && FilterUtils.isMatch(xPathIndexerProperties.getPageContentFilterRegex(), value);
					}
				}
			}
		}

		return processPage;
	}
	
	private String filterValue(String value, boolean trim) {

		String returnValue = null;
		
		// Filter out empty strings and strings made of space, carriage return and tab characters
		if(!value.isEmpty() && !FilterUtils.isMadeOf(value, " \n\t")) {
			
			// Trim data?
			returnValue = trimValue(value, trim);
		}
		
		return returnValue == null ? null : StringEscapeUtils.unescapeHtml(returnValue);
	}
	
	private String trimValue(String value, boolean trim) {
		
		String returnValue;
		if (trim) {
			returnValue = value.trim();
		} else {
			returnValue = value;
		}
		
		return returnValue;
	}
}
