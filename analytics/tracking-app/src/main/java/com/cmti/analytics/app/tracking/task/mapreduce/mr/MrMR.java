package com.cmti.analytics.app.tracking.task.mapreduce.mr;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hbase.task.mapreduce.FullScanCombiner;
import com.cmti.analytics.hbase.task.mapreduce.FullScanMR;
import com.cmti.analytics.hbase.task.mapreduce.FullScanMapper;
import com.cmti.analytics.hbase.task.mapreduce.FullScanReducer;
 
/**
 * this MR full scans mr table.
 
 export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-protocol.jar (no need for VM and AWS)
 hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.mapreduce.mr.MrMR -D mapreduce.map.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  
  
 #hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.mapreduce.mr.MrMR -D mapred.map.child.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapred.reduce.child.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  
  

 #hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.mapreduce.mr.MrMR  -libjars  p.jar     -D mapred.map.child.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapred.reduce.child.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  
 

AWS:

export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 
nohup hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.mapreduce.mr.MrMR -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" > MrMR.log & 
nohup hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.mapreduce.mr.MrMR -D mapred.map.child.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapred.reduce.child.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" > MrMR.log & 

tail -f MrMR.log 

 * 
 * @author Guobiao Mo
 *
 */
public class MrMR extends FullScanMR<Mr>{
	protected static final Logger logger = LogManager.getLogger(MrMR.class);
	
	public MrMR() {		
	}

	@Override
	public Class<? extends FullScanMapper<Mr>> getMapperClass(){
		return MrMapper.class;
	}

	@Override
	public Class<? extends FullScanCombiner<Mr>> getCombinerClass(){
		return null;//return MrCombiner.class;
	}

	@Override
	public Class<? extends FullScanReducer<Mr>> getReducerClass(){
		return MrReducer.class;
	}

	@Override
	public String getInputTableName(){
		return Mr.DEFAULT_TABLE;
	};

	@Override
	public String getOutputTableName(){
		return "aa";//this OutputTableName is not used.
	};

	@Override
	public int run2(String[] args) throws Exception {
		Job job = createJob();

		logger.info("Finished createJob " + job.getJobName());
		
		if (!job.waitForCompletion(true)) {
		    throw new IOException("Error with job " + job);
		}
		
		logger.info("Finished " + job);
				
		return 0;
	}

	public static void main(String args[]) throws Exception{//2013-11-11-10:00:00 2013-11-15-10:00:00
		MrMR mr = new MrMR();
        int res = ToolRunner.run(mr, args);
       
        System.exit(res);
	}
	
}
