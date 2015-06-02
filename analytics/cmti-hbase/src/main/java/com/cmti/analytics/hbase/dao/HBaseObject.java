package com.cmti.analytics.hbase.dao;

import java.util.*;

import com.cmti.analytics.hbase.util.FamilyColumn;

/**
 * Super class of all HBase objects
 * @author Guobiao Mo
 *
 */
public abstract class HBaseObject {

	//store if a column is dirty
	private Set<String> dirtyProperties = new HashSet<String>();
	private boolean allDirty;
	
	public void setDirty(String propertyName) {
		dirtyProperties.add(propertyName);
	}

	public void setAllDirty() {
		allDirty = true;
	}	
	
	public boolean isDirty(String propertyName) {
		if(allDirty) 
			return true;
		return dirtyProperties.contains(propertyName);
	}

	public void clearDirty() {
		allDirty = false;
		dirtyProperties.clear();
	}	

	//column versions
	protected Map<FamilyColumn, Long> versions = new HashMap<FamilyColumn, Long>();
	
	public void setVersion(FamilyColumn column, Long version) {
		versions.put(column, version);
	}

	public Long getVersion(FamilyColumn column) {
		Long version = versions.get(column);
		return version;
	}

	//unmapped columns
	
	protected Map<FamilyColumn, String> unmapped = new HashMap<FamilyColumn, String>();

	public Map<FamilyColumn, String> getUnmapped(){
		return unmapped;
	}

	public void setUnmapped(Map<FamilyColumn, String> map){
		unmapped=map;
	}
	
//add or replace
	public void addUnmapped(Collection<String> keys){//values are null
		for (String key : keys) {
			addUnmapped(key, null);
		}
	}

	public void addUnmapped(String key, Object value){
		addUnmapped(new FamilyColumn(getDefaultColumnFamily(), key), value==null?null:value.toString());
	}

	public String getUnmapped(String key){
		if(unmapped==null){
			return null;
		}
		
		return unmapped.get(new FamilyColumn(getDefaultColumnFamily(), key));
	}

	public void addUnmapped(FamilyColumn key, String value){
		unmapped.put(key, value);
	}
	
	public String getUnmappedAsString(){		
		StringBuilder sb = new StringBuilder("{");
				
		TreeSet<FamilyColumn> keys = new TreeSet<FamilyColumn>(unmapped.keySet());
		boolean first = true;
		for (FamilyColumn key: keys) {
			if(first){
				first = false;
			}else{
				sb.append(", ");
			}
			sb.append(key.toString()).append("=").append(unmapped.get(key));
		}
		
		sb.append("}");
		
		return sb.toString();
	}


	public abstract String getDefaultColumnFamily(); 
}