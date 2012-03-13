package com.atlantbh.nutch.index.alternativedataflow.conf;

import javax.xml.bind.annotation.XmlAttribute;

public class Field {

	private String name;
	private String alias;
	
	public Field() {}
	
	public Field(String name) {
		this.name = name;
	}

	@XmlAttribute(required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required=false)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}	
}
