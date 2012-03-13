package com.atlantbh.nutch.filter.xpath.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlEnum
@XmlRootElement(name="type")
public enum FieldType {
	
	@XmlEnumValue("BOOLEAN")
	BOOLEAN, 
	
	@XmlEnumValue("FLOAT")
	FLOAT, 
	
	@XmlEnumValue("DOUBLE")
	DOUBLE, 
	
	@XmlEnumValue("STRING")
	STRING, 
	
	@XmlEnumValue("INTEGER")
	INTEGER,
	
	@XmlEnumValue("LONG")
	LONG,
	
	@XmlEnumValue("DATE")
	DATE
}
