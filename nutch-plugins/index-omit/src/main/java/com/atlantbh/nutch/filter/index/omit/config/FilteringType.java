package com.atlantbh.nutch.filter.index.omit.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlEnum
@XmlRootElement(name="filteringType")
public enum FilteringType {
	
	@XmlEnumValue("WHITELIST")
	WHITELIST,
	
	@XmlEnumValue("BLACKLIST")
	BLACKLIST
}
