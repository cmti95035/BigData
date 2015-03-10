package com.cmti.analytics.util;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for String.
 * @author gmo
 *
 */
public class StringUtil {

	public static Double getDouble(String value) {
		return getDouble(value, null);
	}

	public static Double getDouble(String value, Double defaultValue) {
		Double ret = null;
		if(StringUtils.isNotBlank(value))
		try{
			ret = Double.parseDouble(value);
		}catch(Exception e) {
			//OK
		}
		return ret==null?defaultValue:ret;
	}
	

	public static Long getLong(String value) {
		return getLong(value, null);
	}

	public static Long getLong(String value, Long defaultValue) {
		Long ret = null;
		if(StringUtils.isNotBlank(value))
		try{
			ret = Long.parseLong(value);
		}catch(Exception e) {
			//OK
		}
		return ret==null?defaultValue:ret;
	}
	

	public static Integer getInt(String value) {
		return getInt(value, null);		
	}
	
	public static Integer getInt(String value, Integer defaultValue) {
		Integer ret = null;
		if(StringUtils.isNotBlank(value))
		try{
			ret = Integer.parseInt(value);
		}catch(Exception e) {
			//OK
		}
		return ret==null?defaultValue:ret;
	}
	
}