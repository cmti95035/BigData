package com.cmti.analytics.app.station.task.mapreduce.sig;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.hbase.task.mapreduce.FullScanCombiner;
import com.cmti.analytics.hbase.task.mapreduce.FullScanMR;
import com.cmti.analytics.hbase.task.mapreduce.FullScanMapper;
import com.cmti.analytics.hbase.task.mapreduce.FullScanReducer;
import com.cmti.analytics.hbase.util.FilterBuilder;
import com.cmti.analytics.hbase.util.HBaseConfig;
import com.cmti.analytics.util.ConfigUtil;
 
 
/**
 * this MR full scans sig table.
 
  export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-protocol.jar 
 hadoop jar station-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.station.task.mapreduce.sig.RecordSigMR  -libjars  p.jar     -D mapred.map.child.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapred.reduce.child.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  
  
 * 
 * @author gmo
 *
 */
public class RecordSigMR extends FullScanMR<RecordSig>{
	protected static final Logger logger = LogManager.getLogger(RecordSigMR.class); 	
	
	public RecordSigMR() {		
	}

	@Override
	protected int getNumReduceTasks(){
		return 1;//config.getInt("reducer_no", 4);
	}

	@Override
	public Class<? extends FullScanMapper<RecordSig>> getMapperClass(){
		return RecordSigMapper.class;
	}

	@Override
	public Class<? extends FullScanCombiner<RecordSig>> getCombinerClass(){
		return RecordSigCombiner.class;
	}

	@Override
	public Class<? extends FullScanReducer<RecordSig>> getReducerClass(){
		return RecordSigReducer.class;
	}

	@Override
	public String getInputTableName(){
		return RecordSig.DEFAULT_TABLE;
	};

	@Override
	public String getOutputTableName(){
		return "aa";//this MR has many handlers, this OutputTableName is not used.
	};

	@Override
	public void init() {
		super.init();
		
		Date event_date_start = ConfigUtil.getDate(config, "event_date_start");
		Date event_date_end = ConfigUtil.getDate(config, "event_date_end");

		Date receive_date_start = ConfigUtil.getGMTDate(config, "receive_date_start");
		Date receive_date_end = ConfigUtil.getGMTDate(config, "receive_date_end");		
		
		logger.error(String.format("event_date_start=%s event_date_end=%s receive_date_start=%s receive_date_end=%s ", event_date_start, event_date_end, receive_date_start, receive_date_end));

		FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
/*
		Filter filter = FilterBuilder.buildBetween(DEFAULT_CF, EVENT_DATE, event_date_start, event_date_end);
		if(filter != null) {
			filterList.addFilter(filter);
		}	

		filter = FilterBuilder.buildBetween(DEFAULT_CF, RECEIVE_DATE, receive_date_start, receive_date_end);
		if(filter != null) {
			filterList.addFilter(filter);
		}		
*/
		if(filterList.getFilters().size()>0) {
			for(Filter f:filterList.getFilters()){
				logger.error("Filter:"+f);
			}
			scan.setFilter(filterList);
		}	
	}
	
	@Override
	public int run2(String[] args) throws Exception {
		Configuration hbaseConfig = HBaseConfig.getConfig();
		Job job = createJob(hbaseConfig);

		if (!job.waitForCompletion(true)) {
		    throw new IOException("Error with job " + job);
		}
		
		logger.error("Finished " + job);
				
		return 0;
	}

	public static void main(String args[]) throws Exception{//2013-11-11-10:00:00 2013-11-15-10:00:00
        int res = ToolRunner.run(new RecordSigMR(), args);
        System.exit(res);
	} 

}
