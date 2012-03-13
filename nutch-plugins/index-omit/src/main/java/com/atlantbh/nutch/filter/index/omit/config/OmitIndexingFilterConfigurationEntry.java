package com.atlantbh.nutch.filter.index.omit.config;

import javax.xml.bind.annotation.XmlAttribute;

public class OmitIndexingFilterConfigurationEntry {

	private Target target;
	private String regex;
	private String name;
	
	public OmitIndexingFilterConfigurationEntry() {}

	public OmitIndexingFilterConfigurationEntry(Target target) {
		this.target = target;
	}

	@XmlAttribute(name="target", required=true)
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@XmlAttribute(name="regex", required=false)
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	@XmlAttribute(name="name", required=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
