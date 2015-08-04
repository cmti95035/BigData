package com.cmti.analytics.app.tracking.task.importer;

import java.io.IOException;

import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.DriveTestDataDao;
import com.cmti.analytics.app.tracking.hbase.domain.DriveTestData;
import com.cmti.analytics.hbase.loader.BulkLoader;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;
 

/**
 * read road test data and generate HFiles, all files are in HDFS.
 * 
 * 
hdfs dfs -mkdir /data
hdfs dfs -mkdir /data/input
hdfs dfs -mkdir /data/input/drivetest
hdfs dfs -put 0102885120140928160610ms9.csv /data/input/drivetest

on my local cloudera VM:
export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-protocol.jar
hdfs dfs -rm -r /data/output/roadtest
 hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsDriveTestDataBulkLoader /data/input/drivetest /data/output/roadtest -D mapreduce.map.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  

on AWS:
mkdir logs
export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
hdfs dfs -rm -r /data/output/drivetest
  hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsDriveTestDataBulkLoader /data/input/drivetest /data/output/drivetest -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" 
 
  chmod u+r tracking-app-1.0-SNAPSHOT.jar  ; export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" ; hdfs dfs -rm -r /data/output/drivetest ;   hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsDriveTestDataBulkLoader /data/input/drivetest /data/output/drivetest -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"

 hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles  /data/output/drivetest drive_test_data

 * @author Guobiao Mo
 *
 */
public class HdfsDriveTestDataBulkLoader extends BulkLoader<DriveTestData, Object> {

	protected final Logger logger = LogManager.getLogger(HdfsDriveTestDataBulkLoader.class); 
		
	private DriveTestDataDao dao;
	public HdfsDriveTestDataBulkLoader() throws IOException{
		dao = new DriveTestDataDao();
		dao.open();		
	}

	@Override
	protected DriveTestDataDao getDao(){
		return dao;
	}

	@Override
	public Class<? extends BulkLoaderMapper> getMapperClass(){
		BulkLoaderMapper<DriveTestData, Object> mapper = new BulkLoaderMapper<>();
		return mapper.getClass();
	}

	public static void main(String[] args) throws Exception {
		HdfsDriveTestDataBulkLoader loader = new HdfsDriveTestDataBulkLoader();
        int res = ToolRunner.run(loader, args);
        loader.close();
        System.exit(res);
	}

}