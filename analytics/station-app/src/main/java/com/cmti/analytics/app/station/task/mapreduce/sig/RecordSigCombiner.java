package com.cmti.analytics.app.station.task.mapreduce.sig;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.task.mapreduce.FullScanCombiner;
 
/**
 * this MR full scans event table.
 * can filter on both event and receive dates
 * 
 * @author gmo
 *
 */
//Combiner
public class RecordSigCombiner extends FullScanCombiner<RecordSig>  {

	protected static final Logger logger = LogManager.getLogger(RecordSigCombiner.class); 
	Configuration config = Config.getConfig();
 
}