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

package com.atlantbh.nutch.index.alternativedataflow.conf;

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

@XmlRootElement(name="alternativeDataFlowIndexingFilterConfiguration")
public class AlternativeDataFlowIndexingFilterConfiguration {

	// Constants
	private static final String CONFIG_FILE_PATH_PROPERTY = "filter.index.alternativedataflow.file";
	private static final Logger log = Logger.getLogger(AlternativeDataFlowIndexingFilterConfiguration.class);
	
	private List<Entry> entryList = new ArrayList<Entry>(0);

	public static AlternativeDataFlowIndexingFilterConfiguration getInstance(Configuration configuration) {
		try {
			
			// Get configuration from Nutch /conf folder
			Reader configReader = configuration.getConfResourceAsReader(configuration.get(CONFIG_FILE_PATH_PROPERTY));
						
			// Initialize JAXB
			JAXBContext context = JAXBContext.newInstance(new Class[] {AlternativeDataFlowIndexingFilterConfiguration.class, Entry.class, Field.class});
			Unmarshaller unmarshaller = context.createUnmarshaller();
						
			// Initialize configuration
			AlternativeDataFlowIndexingFilterConfiguration xPathFilterConfiguration  = (AlternativeDataFlowIndexingFilterConfiguration) unmarshaller.unmarshal(configReader);
			return xPathFilterConfiguration;
			
		} catch (JAXBException e) {
			log.error("Configuration initialization error!");
		}
		
		return null;
	}
	
	private AlternativeDataFlowIndexingFilterConfiguration() {}

	public AlternativeDataFlowIndexingFilterConfiguration(List<Entry> entryList) {
		this.entryList = entryList;
	}

	@XmlElement(name="alternativeDataFlowIndexingFilterConfigurationEntry", nillable=false)
	public List<Entry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<Entry> entryList) {
		this.entryList = entryList;
	}
	
	
}
