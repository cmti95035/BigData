package com.cmti.analytics.hbase.task.mapreduce;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;

import com.cmti.analytics.hbase.util.HBaseConfig;

/**
 * pass config from Tool to HBaseConfig
 * @author Guobiao Mo
 *
 * @param <T>
 */
public abstract class ConfiguredMR extends Configured implements Tool {
	public ConfiguredMR(){		
	}

	@Override
	public int run(String[] args) throws Exception {
	    HBaseConfig.getConfig(super.getConf());
	    return run2(args);
	}
	
	public abstract int run2(String[] args) throws Exception;
}