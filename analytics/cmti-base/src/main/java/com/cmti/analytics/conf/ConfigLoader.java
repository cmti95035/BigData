package com.cmti.analytics.conf;

import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used by Config.
 * @author Guobiao Mo
 *
 */
public class ConfigLoader {

	private static final Logger logger = LogManager.getLogger(ConfigLoader.class);
	
	public PropertiesConfiguration loadConfiguration() {
		PropertiesConfiguration base = new PropertiesConfiguration();
		base.setThrowExceptionOnMissing(true);
		populateConfiguration(base);
		addCommandLineProperties(base);
		return base;
	}

	private static final String COMMAND_LINE_PREFIX = "_app_config.";
	protected void addCommandLineProperties(PropertiesConfiguration base) {
		Properties properties = System.getProperties();
		Set<String> keys = properties.stringPropertyNames();
		for(String key : keys){
			if(key.startsWith(COMMAND_LINE_PREFIX)){
				String value = properties.getProperty(key);
				base.setProperty(key.substring(COMMAND_LINE_PREFIX.length()), value);
			}
		}
	}
	
	protected void populateConfiguration(PropertiesConfiguration base) {
		String configFile = getConfigFile();
		
		URL resource = getConfigResource(configFile);
		if(resource == null) {
			throw new IllegalArgumentException("Cannot find config file: " + configFile);
		}

		try {
			base.load(resource);
			logger.error("Loaded configuration from " + resource);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	protected URL getConfigResource(String resource) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null) {
			classLoader = ConfigLoader.class.getClassLoader();
		}
		return classLoader.getResource(resource);
	}
	

	private static final String DEFAULT_CONFIG_FILE = "app.properties";

	protected String getConfigFile() {
		String configFile = DEFAULT_CONFIG_FILE;
		
		String site = System.getProperty("site"); 
		if(StringUtils.isNotBlank(site)){
			configFile = String.format("app_%s.properties", site);
			logger.error("Use "+ configFile);
		}
		
		return "app/"+configFile;
	}
}