package com.cmti.analytics.hbase.util;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.conf.Config;

/**
 * if "hbase.config" is defined in app.properties, like
 * hbase.config=hbase-site_qa.xml
 * it looks up this file in classpath. usually this file is place in resource folder.
 * if "hbase.config" is not defined, default hbase-site.xml is used.
 * @author Guobiao Mo
 *
 */
public class HBaseConfig {

	private static final Logger logger = LogManager.getLogger(HBaseConfig.class);
	
	private static ReentrantLock lock = new ReentrantLock();
	private static Configuration config;
	public static final String HBASE_CONFIG_KEY = "hbase.config";

	public static Configuration getConfig() {
		return getConfig(null);
	}
		
	public static Configuration getConfig(Configuration that) {
		if(config!=null){
			return config;
		}
		
		lock.lock();
		try {
			if(config == null) {
				loadConfiguration(that);
			}
			return config;
		} finally {
			lock.unlock();
		}
	}

	private static void loadConfiguration(Configuration that) { 
		org.apache.commons.configuration.Configuration appConfig = Config.getConfig();
		String hbaseConfigFile = appConfig.getString(HBASE_CONFIG_KEY);
		if(StringUtils.isNotBlank(hbaseConfigFile)) {
			config = that ==null?new Configuration():new Configuration(that);
			config.addResource(hbaseConfigFile);
			config = HBaseConfiguration.create(config);
		} else {
			logger.info("HBase config file not defined, load from default.");
			config = that ==null?HBaseConfiguration.create():HBaseConfiguration.create(config);
		}
	}
}