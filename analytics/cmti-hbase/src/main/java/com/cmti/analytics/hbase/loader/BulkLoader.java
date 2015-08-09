package com.cmti.analytics.hbase.loader;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
//import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.PutSortReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



//import com.cmti.analytics.app.device.task.importer.HdfsEventBulkLoader;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.ConfiguredMR;
import com.cmti.analytics.hbase.util.HBaseConfig;

/**
 * 
 * Bulk loader to batch put data into HBase using MapReduce.
 * if outputPath != null, it generates HFile in hdfs outputPath.
 * else, it uses the TableOutputFormat API to insert data directly to HBase.
 * 
 * example usages see:
 * HdfsMrBulkLoader.java
 * HdfsRecordSigBulkLoader.java
 * 
 * @author Guobiao Mo
 *
 * @param <T>
 * @param <P>
 */

public abstract class BulkLoader<T extends HBaseObject, P> extends ConfiguredMR {
	protected final Logger logger = LogManager.getLogger(BulkLoader.class); 
	
	protected abstract HBaseGenericDao<T, P> getDao();
	protected abstract Class<? extends Mapper> getMapperClass();	

	protected Class<? extends FileInputFormat<LongWritable, Text>> getFileInputFormatClass(){
		return TextInputFormat.class;
	}
	
	protected void close() {//called by subclass
		getDao().close();
	};

	public Job createJob(String inputPath, String outputPath) throws IOException {
		HTable table = getDao().getTable();
		Configuration conf = HBaseConfig.getConfig();
		//conf.setInt("mapreduce.reduce.memory.mb", 1024*60);//TODO this high memory is for MR loading 
		
		conf.set("dao.class", getDao().getClass().getCanonicalName());//used in BulkLoaderMapper
		logger.error(conf.toString());
		
		Path inputDir = new Path(inputPath);
		Job job = new Job(conf, this.getClass().getSimpleName()+"-"+inputPath);
		
		job.setJarByClass(BulkLoader.class);
		FileInputFormat.setInputPaths(job, inputDir);
		FileInputFormat.setInputDirRecursive(job, true);
		//FileInputFormat.addInputPath(job, path);
		//FileInputFormat.addInputPaths(job, commaSeparatedPaths);
		job.setInputFormatClass(getFileInputFormatClass());
		job.setMapperClass(getMapperClass());

		if (StringUtils.isNotBlank(outputPath)) {// generate HFile in HDFS
			job.setReducerClass(PutSortReducer.class);
			Path outputDir = new Path(outputPath);
			FileOutputFormat.setOutputPath(job, outputDir);
			job.setMapOutputKeyClass(ImmutableBytesWritable.class);
			job.setMapOutputValueClass(Put.class);
			HFileOutputFormat.configureIncrementalLoad(job, table);
		} else {
			// insert into table directly using TableOutputFormat, not tested TODO
			TableMapReduceUtil.initTableReducerJob(getDao().getTableName(), null, job);
			job.setNumReduceTasks(0);
		}		
		
		TableMapReduceUtil.addDependencyJars(job);
		return job;
	}

	@Override
	public int run2(String[] args) throws Exception {
		Job job = createJob(args[0], args[1]);

		if (!job.waitForCompletion(true)) {
		    throw new IOException("Error with job " + job);
		}
		
		return 0;
	}

}
