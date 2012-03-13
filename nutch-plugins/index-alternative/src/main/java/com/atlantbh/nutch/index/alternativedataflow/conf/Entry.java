package com.atlantbh.nutch.index.alternativedataflow.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

public class Entry {

	private String dataFlow;
//	private String destination;
//	private String name;
//	private Character delimiter;
//	private Character qualifier;
//	private Boolean appendTimestamp;
//	private Boolean addFieldNamesToFirstLine;
	private Map<QName, String> parameterMap;
	private List<Field> fieldList = new ArrayList<Field>(0);
	
	public Entry() {}

	public Entry(String dataFlow) {
		this.dataFlow = dataFlow;
	}

	@XmlAttribute(required=true)
	public String getDataFlow() {
		return dataFlow;
	}

	public void setDataFlow(String dataFlow) {
		this.dataFlow = dataFlow;
	}
	
	

//	@XmlAttribute(required=false)
//	public String getDestination() {
//		return destination;
//	}
//
//	public void setDestination(String destination) {
//		this.destination = destination;
//	}
//
//	@XmlAttribute(required=false)
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	@XmlAttribute(required=false)
//	public Character getDelimiter() {
//		return delimiter;
//	}
//
//	public void setDelimiter(Character delimiter) {
//		this.delimiter = delimiter;
//	}
//
//	@XmlAttribute(required=false)
//	public Character getQualifier() {
//		return qualifier;
//	}
//
//	public void setQualifier(Character qualifier) {
//		this.qualifier = qualifier;
//	}
//
//	@XmlAttribute(required=false)
//	public Boolean isAppendTimestamp() {
//		return appendTimestamp;
//	}
//
//	public void setAppendTimestamp(Boolean appendTimestamp) {
//		this.appendTimestamp = appendTimestamp;
//	}
//
//	@XmlAttribute(required=false)
//	public Boolean isAddFieldNamesToFirstLine() {
//		return addFieldNamesToFirstLine;
//	}
//
//	public void setAddFieldNamesToFirstLine(Boolean addFieldNamesToFirstLine) {
//		this.addFieldNamesToFirstLine = addFieldNamesToFirstLine;
//	}
	
	@XmlAnyAttribute
	public Map<QName, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<QName, String> parameterMap) {
		this.parameterMap = parameterMap;
	}

	@XmlElement(name="field", nillable=false)
	public List<Field> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
	}

}
