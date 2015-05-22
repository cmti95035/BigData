package com.cmti.analytics.app.tracking.task.mapreduce.mr;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.task.mapreduce.FullScanReducer;
 
/**
 * this MR full scans server event table. 
 * 
 * @author gmo
 *
 */
public class MrReducer extends FullScanReducer<Mr> {

//	protected static final Logger logger = LogManager.getLogger(MrReducer.class); 
	//Configuration config = Config.getConfig();
}