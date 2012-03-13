package com.atlantbh.nutch.filter.index.omit.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlEnum
@XmlRootElement(name="target")
public enum Target {
	
	@XmlEnumValue("URL")
	URL,
	
	@XmlEnumValue("META_FIELD_PRESENT")
	META_FIELD_PRESENT
}
