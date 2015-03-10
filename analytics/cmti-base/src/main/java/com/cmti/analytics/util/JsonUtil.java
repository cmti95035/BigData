package com.cmti.analytics.util;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil{ 

	private static Logger logger = Logger.getLogger(JsonUtil.class);	

	public static Map<String, Object> getMap(String line) {
		ObjectMapper mapper = new ObjectMapper();//FIXME use threadlocal
		Map<String, Object> data = new HashMap<String, Object>(); 
		
		if(StringUtils.isNotBlank(line))
		try {
			data = mapper.readValue(line, data.getClass());
		} catch (Exception e) {
			throw new RuntimeException(line, e);
		}
		
		return data;
	}
}







