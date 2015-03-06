package com.cmti.analytics.redis;

import org.apache.commons.lang3.StringUtils;
 
 
public class RedisObject { 

	public String key;

	public String value;

	public String[] parseKey(){
		return key.split("~");
	}

	public static String buildKey(Object... objs) {
		return StringUtils.join(objs, "~"); 
	}

////////////////////////
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
} 
