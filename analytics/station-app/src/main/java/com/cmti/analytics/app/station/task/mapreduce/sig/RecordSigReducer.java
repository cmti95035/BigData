package com.cmti.analytics.app.station.task.mapreduce.sig;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.task.mapreduce.FullScanReducer;
 
/**
 * this MR full scans server event table. 
 * 
 * @author gmo
 *
 */
public class RecordSigReducer extends FullScanReducer<RecordSig> {

	protected static final Logger logger = LogManager.getLogger(RecordSigReducer.class); 
	Configuration config = Config.getConfig();
}