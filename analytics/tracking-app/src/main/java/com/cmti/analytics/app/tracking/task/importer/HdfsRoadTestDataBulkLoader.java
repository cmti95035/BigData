package com.cmti.analytics.app.tracking.task.importer;

import java.io.IOException;

import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.RoadTestDataDao;
import com.cmti.analytics.app.tracking.hbase.domain.RoadTestData;
import com.cmti.analytics.hbase.loader.BulkLoader;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;
 

/**
 * read road test data and generate HFiles, all files are in HDFS.
 * 
 * 
hdfs dfs -mkdir /data
hdfs dfs -mkdir /data/input
hdfs dfs -mkdir /data/input/roadtest
hdfs dfs -put 0102885120140928160610ms9.csv /data/input/roadtest

on my local cloudera VM:
export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-protocol.jar
 export HADOOP_CLASSPATH=/home/hadoop/hbase/hbase-0.94.18.jar
hdfs dfs -rm -r /data/output/roadtest
 hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsRoadTestDataBulkLoader /data/input/roadtest /data/output/roadtest -D mapreduce.map.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  

on AWS:
export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
hdfs dfs -rm -r /data/output/roadtest
nohup hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsRoadTestDataBulkLoader /data/input/roadtest /data/output/roadtest -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" >HdfsRoadTestDataBulkLoader.log &  
tail -f HdfsRoadTestDataBulkLoader.log 
 
 hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles  /data/output/roadtest road_test_data

 * @author gmo
 *
 */
public class HdfsRoadTestDataBulkLoader extends BulkLoader<RoadTestData, Object> {

	protected final Logger logger = LogManager.getLogger(HdfsRoadTestDataBulkLoader.class); 
		
	private RoadTestDataDao dao;
	public HdfsRoadTestDataBulkLoader() throws IOException{
		dao = new RoadTestDataDao();
		dao.open();		
	}

	@Override
	protected RoadTestDataDao getDao(){
		return dao;
	}

	@Override
	public Class<? extends BulkLoaderMapper> getMapperClass(){
		BulkLoaderMapper<RoadTestData, Object> mapper = new BulkLoaderMapper<RoadTestData, Object>();
		return mapper.getClass();
	}

	public static void main(String[] args) throws Exception {
		HdfsRoadTestDataBulkLoader loader = new HdfsRoadTestDataBulkLoader();
        int res = ToolRunner.run(loader, args);
        loader.close();
        System.exit(res);
	}

}