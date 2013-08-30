package com.atlantbh.nutch.filter.xpath.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


public class XPathIndexerPropertiesField {
	
	private String name;
	private String xPath;
	private FieldType type;
	private String dateFormat;
	private Boolean trimXPathData;
	private Boolean concat;
	private String concatDelimiter;
	private String value;
	
	public XPathIndexerPropertiesField() {}

	public XPathIndexerPropertiesField(String name, String xPath, FieldType type) {
		this.name = name;
		this.xPath = xPath;
		this.type = type;
	}

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="xPath", required=false)
	public String getXPath() {
		return xPath;
	}

	public void setXPath(String xPath) {
		this.xPath = xPath;
	}

	@XmlAttribute(name="type", required=true)
	public FieldType getType() {
		return type;
	}
	public void setType(FieldType type) {
		this.type = type;
	}

	@XmlAttribute(name="dateFormat", required=false)
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@XmlAttribute(name="trimXPathData", required=false)
	public Boolean getTrimXPathData() {
		return trimXPathData;
	}

	public void setTrimXPathData(Boolean trimXPathData) {
		this.trimXPathData = trimXPathData;
	}

	@XmlAttribute(name="concat", required=false)
	public Boolean isConcat() {
		return concat;
	}

	public void setConcat(Boolean concat) {
		this.concat = concat;
	}

	@XmlAttribute(name="concatDelimiter", required=false)
	public String getConcatDelimiter() {
		return concatDelimiter;
	}

	public void setConcatDelimiter(String concatDelimiter) {
		this.concatDelimiter = concatDelimiter;
	}
	
	@XmlAttribute(name="value", required=false)
	public String getValue() {
	  return value;
	}
	
	public void setValue(String value) {
	  this.value = value;
	}
	
}
