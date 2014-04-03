package com.atlantbh.nutch.filter.xpath.config;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

@XmlRootElement(name="xpathFilterConfiguration")
public class XPathFilterConfiguration {

	// Constants
	private static final String CONFIG_FILE_PATH_PROPERTY = "filter.xpath.file";
	private static final Logger log = Logger.getLogger(XPathFilterConfiguration.class);
	
	private List<XPathIndexerProperties> xPathIndexerPropertiesList = new ArrayList<XPathIndexerProperties>(0);

	public static XPathFilterConfiguration getInstance(Configuration configuration) {
		try {
			
			// Get configuration from Nutch /conf folder
			Reader configReader = configuration.getConfResourceAsReader(configuration.get(CONFIG_FILE_PATH_PROPERTY));
						
			// Initialize JAXB
			JAXBContext context = JAXBContext.newInstance(new Class[] {XPathFilterConfiguration.class, XPathIndexerProperties.class, XPathIndexerPropertiesField.class, FieldType.class});
			Unmarshaller unmarshaller = context.createUnmarshaller();
						
			// Initialize configuration
			XPathFilterConfiguration xPathFilterConfiguration  = (XPathFilterConfiguration) unmarshaller.unmarshal(configReader);
			return xPathFilterConfiguration;
			
		} catch (JAXBException e) {
			log.error("Configuration initialization error!");
		}
		
		return null;
	}
	
	// needed for XML binding
	@SuppressWarnings("unused")
	private XPathFilterConfiguration() {}
	
	public XPathFilterConfiguration(List<XPathIndexerProperties> xPathIndexerPropertiesList) {		
		this.xPathIndexerPropertiesList = xPathIndexerPropertiesList;
	}

	@XmlElement(name="xpathIndexerProperties", nillable=false)
	public List<XPathIndexerProperties> getXPathIndexerPropertiesList() {
		return xPathIndexerPropertiesList;
	}

	public void setXPathIndexerPropertiesList(List<XPathIndexerProperties> xPathIndexerPropertiesList) {
		this.xPathIndexerPropertiesList = xPathIndexerPropertiesList;
	}
}
