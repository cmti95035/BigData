package com.cmti.analytics.util;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cmti.analytics.conf.Config;

public class SpringUtil { 
//	private static Logger logger = Logger.getLogger(SpringUtil.class);	

	public static ApplicationContext getApplicationContext() {
		Configuration config = Config.getConfig();
		String configFile = config.getString("spring.config", "config.xml");
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring/"+configFile, "spring/app.xml"});
		
		return context;
	}	
}







