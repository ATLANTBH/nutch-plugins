/**
 * Copyright 2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlantbh.nutch.filter.index.omit.config;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

@XmlRootElement(name="omitIndexingFilterConfiguration")
public class OmitIndexingFilterConfiguration {

	// Constants
	private static final String CONFIG_FILE_PATH_PROPERTY = "filter.index.omit.file";
	private static final Logger log = Logger.getLogger(OmitIndexingFilterConfiguration.class);
	
	private FilteringType filteringType;
	private List<OmitIndexingFilterConfigurationEntry> omitIndexingFilterConfigurationEntryList = new ArrayList<OmitIndexingFilterConfigurationEntry>(0);

	public static OmitIndexingFilterConfiguration getInstance(Configuration configuration) {
		try {
			
			// Get configuration from Nutch /conf folder
			Reader configReader = configuration.getConfResourceAsReader(configuration.get(CONFIG_FILE_PATH_PROPERTY));
						
			// Initialize JAXB
			JAXBContext context = JAXBContext.newInstance(new Class[] {OmitIndexingFilterConfiguration.class, OmitIndexingFilterConfigurationEntry.class, FilteringType.class, Target.class});
			Unmarshaller unmarshaller = context.createUnmarshaller();
						
			// Initialize configuration
			OmitIndexingFilterConfiguration xPathFilterConfiguration  = (OmitIndexingFilterConfiguration) unmarshaller.unmarshal(configReader);
			return xPathFilterConfiguration;
			
		} catch (JAXBException e) {
			log.error("Configuration initialization error!");
		}
		
		return null;
	}
	
	private OmitIndexingFilterConfiguration() {}

	public OmitIndexingFilterConfiguration(List<OmitIndexingFilterConfigurationEntry> omitIndexingFilterConfigurationEntryList) {
		this.omitIndexingFilterConfigurationEntryList = omitIndexingFilterConfigurationEntryList;
	}

	@XmlAttribute(name="filteringType", required=true)
	public FilteringType getFilteringType() {
		return filteringType;
	}

	public void setFilteringType(FilteringType filteringType) {
		this.filteringType = filteringType;
	}

	@XmlElement(name="omitIndexingFilterConfigurationEntry", nillable=false)
	public List<OmitIndexingFilterConfigurationEntry> getOmitIndexingFilterConfigurationEntryList() {
		return omitIndexingFilterConfigurationEntryList;
	}

	public void setOmitIndexingFilterConfigurationEntryList(List<OmitIndexingFilterConfigurationEntry> omitIndexingFilterConfigurationEntryList) {
		this.omitIndexingFilterConfigurationEntryList = omitIndexingFilterConfigurationEntryList;
	}
	
	
}
