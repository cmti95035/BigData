package com.cmti.analytics.hbase.task.mapreduce;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.export.HBaseDataExport;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
import com.cmti.analytics.hbase.util.HBaseConfig;
import com.cmti.analytics.hbase.util.HBaseUtil;

/**
 * A export MR job to be extended.
 * @author Guobiao Mo
 *
 * @param <T>
 */
public abstract class ExportMR<T extends HBaseObject> extends ConfiguredMR {

	protected static final Logger logger = LogManager.getLogger(ExportMR.class); 


	protected org.apache.commons.configuration.Configuration config = Config.getConfig();
	
	protected Scan scan;
	
	public ExportMR(){		
	}

	public Scan getScan(){		
		return scan;
	}
	
	public void init() {
		scan = HBaseUtil.newOnePassMassScan();
		//scan = HBaseUtil.newOnePassMassScan(new EventDao());		//slow, don't use it.
		scan.setCaching(500);//TODO configurable, transfer 500 rows at a time to the client to be processed
		
		//range scan if need
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
		
		
		String jobName = getClass().getSimpleName() + " Export MR job";
		Job job = new Job(hbaseConfig, jobName);
		job.setJarByClass(ExportMR.class);
		
		//http://hbase.apache.org/book/mapreduce.specex.html
		job.setSpeculativeExecution(false);
		job.setReduceSpeculativeExecution(false);
		logger.info("Job jar for aggregate = " + job.getJar());

		Scan scan = getScan();//way for subclass to provide a scan
		
//		http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/mapreduce/TableMapReduceUtil.html#addDependencyJars(org.apache.hadoop.conf.Configuration, java.lang.Class...)
		TableMapReduceUtil.initTableMapperJob(
				getInputTableName(),
				scan,
				getMapperClass(),     // mapper class
				ImmutableBytesWritable.class,   // mapper outputDir key: "deviceid~"+event.getDeviceId() or "olap~"+...
				StringArrayWritable.class,   // mapper outputDir value
				job);
				
			job.setReducerClass(ExportReducer.class);
			job.setOutputFormatClass(TextOutputFormat.class);

		    job.setOutputKeyClass(ImmutableBytesWritable.class);
		    job.setOutputValueClass(StringArrayWritable.class);

		    //job.setOutputPath(new Path("out"));
		    //http://stackoverflow.com/questions/9849776/calling-a-mapreduce-job-from-a-simple-java-program
		    FileSystem fs = FileSystem.get(hbaseConfig);
		    Path out = new Path(getOutputFilePath());
		    fs.delete(out, true);
		    // finally set the empty out path
		    TextOutputFormat.setOutputPath(job, out);
		    
			job.setNumReduceTasks(1); 

		return job;
	}
 

	abstract public Class<? extends ExportMapper<T>> getMapperClass();	

	abstract public String getOutputFilePath();
	abstract public String getInputTableName();

	public static class ExportReducer  extends Reducer<ImmutableBytesWritable, StringArrayWritable, ImmutableBytesWritable, StringArrayWritable> {

		protected static final Logger logger = LogManager.getLogger(ExportReducer.class);  

		@Override
		public void reduce(ImmutableBytesWritable key, Iterable<StringArrayWritable> values, Context context) throws IOException, InterruptedException {

			Iterator<StringArrayWritable> it=values.iterator();
		
			while(it.hasNext()){
				StringArrayWritable array = it.next();
				context.write(null, array);
			}
		}
	}
}