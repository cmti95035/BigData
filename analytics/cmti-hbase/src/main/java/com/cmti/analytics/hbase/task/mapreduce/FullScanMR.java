package com.cmti.analytics.hbase.task.mapreduce;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.export.HBaseDataExport;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
import com.cmti.analytics.hbase.util.HBaseConfig;
import com.cmti.analytics.hbase.util.HBaseUtil;

/**
 * A framework that we can plug in multiple handlers in one MR job.
 * @author Guobiao Mo
 *
 * @param <T>
 */
public abstract class FullScanMR<T extends HBaseObject> extends ConfiguredMR {

	protected static final Logger logger = LogManager.getLogger(FullScanMR.class); 

	protected org.apache.commons.configuration.Configuration config = Config.getConfig();
	
	protected Scan scan;
	
	public FullScanMR(){		
	}

	public Scan getScan(){		
		return scan;
	}
	
	public void init() {
		scan = HBaseUtil.newOnePassMassScan();
		//scan = HBaseUtil.newOnePassMassScan(new EventDao());		//slow, don't use it.
		scan.setCaching(500);//TODO configurable
		
		//copied from EventDao.java
		String startRow = config.getString(HBaseDataExport.START_ROW, null);
		if(StringUtils.isNotBlank(startRow)){
			scan.setStartRow(Bytes.toBytes(startRow));
		}

		String stopRow = config.getString(HBaseDataExport.STOP_ROW, null);
		if(StringUtils.isNotBlank(stopRow)){
			scan.setStopRow(Bytes.toBytes(stopRow));
		}
	}

	public Job createJob() throws IOException, InterruptedException, ClassNotFoundException {
		return createJob(HBaseConfig.getConfig());
	}
	
	public Job createJob(Configuration hbaseConfig) throws IOException, InterruptedException, ClassNotFoundException {
		init();
		
		
		String jobName = getClass().getSimpleName() + " MR job";
		Job job = new Job(hbaseConfig, jobName);
		job.setJarByClass(FullScanMR.class);
		
		//http://hbase.apache.org/book/mapreduce.specex.html
		job.setSpeculativeExecution(false);
		job.setReduceSpeculativeExecution(false);
		logger.info("Job jar for aggregate = " + job.getJar());

		Scan scan = getScan();//way for subclass to provide a scan
//		if(scan == null){
	//		scan = HBaseUtil.newOnePassMassScan();
		//}
		
//		http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/mapreduce/TableMapReduceUtil.html#addDependencyJars(org.apache.hadoop.conf.Configuration, java.lang.Class...)
		TableMapReduceUtil.initTableMapperJob(
				getInputTableName(),
				scan,
				getMapperClass(),     // mapper class
				Text.class,   // mapper outputDir key: "deviceid~"+event.getDeviceId() or "olap~"+...
				StringArrayWritable.class,   // mapper outputDir value
				job);
		
		if(getCombinerClass() != null){
			job.setCombinerClass(getCombinerClass());
		}

		if(getReducerClass() != null){
			TableMapReduceUtil.initTableReducerJob(
				getOutputTableName(),        // outputDir table
				getReducerClass(),    // reducer class
				job);

			//config.setInt(JobContext.NUM_MAPS, getNumMapTasks());
			job.setNumReduceTasks(getNumReduceTasks());
		}else{
			job.setOutputFormatClass(NullOutputFormat.class);
		    job.setNumReduceTasks(0);
		}

		return job;
	}

	protected int getNumReduceTasks(){
		String className = this.getClass().getCanonicalName()+".reducer.number";
		int ret =  config.getInt(className, 1); 

		logger.info(className+ret);
		return ret;
	}

	abstract public Class<? extends FullScanMapper<T>> getMapperClass();	
	abstract public Class<? extends FullScanCombiner<T>> getCombinerClass();
	abstract public Class<? extends FullScanReducer<T>> getReducerClass();
	
	abstract public String getOutputTableName();
	abstract public String getInputTableName();
	
}