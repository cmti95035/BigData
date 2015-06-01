package com.cmti.analytics.util;

import java.util.*;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Util to retrieve data from Configuration.
 * @author Guobiao Mo
 *
 */
public class ConfigUtil{ 

	private static Logger logger = Logger.getLogger(ConfigUtil.class);	

	//2014-6-1 00:00:00.000 +0000
	public static Date getDate(Configuration config, String name) {
		String value = config.getString(name, null);
		
		if( StringUtils.isNotBlank(value)){
			return DateUtil.parseGMTString(value);
		} else {
			return null;
		}		
	}

	//for 2014-6-1 get 2014-6-1 00:00:00.000 +0000
	public static Date getGMTDate(Configuration config, String name) {
		String value = config.getString(name, null);
		
		if( StringUtils.isNotBlank(value)){
			value += " 00:00:00.000 +0000";
			return DateUtil.parseGMTString(value);
		} else {
			return null;
		}		
	}
	
}







