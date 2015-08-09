package com.cmti.analytics.app.tracking.task.importer;

import java.io.IOException;

import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.MrDao;
import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hbase.loader.BulkLoader;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;
 

/**
 * read MR files and generates HFiles, all files are in HDFS.
 * 
 * 
hdfs dfs -mkdir /data/input/mock
hdfs dfs -put mock*.txt /data/input/mock
 
export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
hdfs dfs -rm -r /data/output/mr
 hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsMrBulkLoader /data/input/mock  /data/output/mr    -D mapreduce.map.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  


AWS:

hdfs dfs -mkdir /data/input/mr
hdfs dfs -put /mnt/raw/all-raw/* /* /*txt /data/input/mr

export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
hdfs dfs -rm -r /data/output/mr 
nohup hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsMrBulkLoader /data/input/mr  /data/output/mr    -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" > HdfsMrBulkLoader.log & 
tail -f HdfsMrBulkLoader.log 

hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles /data/output/mr mr

 * @author Guobiao Mo
 *
 */
public class HdfsMrBulkLoader extends BulkLoader<Mr, Object> {

	protected final Logger logger = LogManager.getLogger(HdfsMrBulkLoader.class); 
		
	private MrDao dao;
	public HdfsMrBulkLoader() throws IOException{
		dao = new MrDao();
		dao.open();		
	}

	@Override
	protected MrDao getDao(){
		return dao;
	}

	@Override
	public Class<? extends BulkLoaderMapper> getMapperClass(){
		BulkLoaderMapper<Mr, Object> mapper = new BulkLoaderMapper<>();
		return mapper.getClass();
	}

	public static void main(String[] args) throws Exception {
		HdfsMrBulkLoader loader = new HdfsMrBulkLoader();
        int res = ToolRunner.run(loader, args);
        loader.close();
        System.exit(res);
	}

}
