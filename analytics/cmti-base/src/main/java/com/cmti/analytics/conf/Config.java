package com.cmti.analytics.conf;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class that provides system configuration
 * 
 * @author gmo
 */
public class Config {
	protected static final Logger logger = LogManager.getLogger(Config.class);
	
	private static final ReentrantLock lock = new ReentrantLock();
	private static PropertiesConfiguration config;

	public static Configuration getConfig() {
		if(config!=null){
			return config;
		}
		
		lock.lock();
		try {
			if(config == null) {
				loadConfiguration();
			}
			return config;
		} finally {
			lock.unlock();
		}
	}
/*
	public static Configuration refreshConfig() {		
		lock.lock();
		try {
			if(config!=null){
				config.clear();
			}
			loadConfiguration();
			return config;
		} finally {
			lock.unlock();
		}
	}
*/
	private static void loadConfiguration() {
		config = new ConfigLoader().loadConfiguration();
		Iterator<String> keys= config.getKeys();
		while(keys.hasNext()) {
			String key = keys.next();
			String value = config.getString(key);
			logger.error(key +"="+ value);
		}
	}

}