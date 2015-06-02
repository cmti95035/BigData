package com.cmti.analytics.hbase.domaingen;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Guobiao Mo
 *
 */
public class DomainField{
	String homePackage;
	public boolean isList;
	public String listType;//String in List<String>

	public String name;//event
	public String capName;//Event
	public String type;//String
	public String dbName;//"syn_event"
	
	public DomainField(){
		
	}
	
	public void setName(String name){
		this.name = name;
		capName = StringUtils.capitalize(name);
	}

	public String getHomePackage() {
		return homePackage;
	}

	public void setHomePackage(String homePackage) {
		this.homePackage = homePackage;
	}

	public String getCapName() {
		return capName;
	}

	public void setCapName(String capName) {
		this.capName = capName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getName() {
		return name;
	}

	public boolean isList() {
		return isList;
	}

	public void setList(boolean isList) {
		this.isList = isList;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}
	

}
