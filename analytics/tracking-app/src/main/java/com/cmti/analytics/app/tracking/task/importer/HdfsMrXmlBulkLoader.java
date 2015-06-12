package com.cmti.analytics.app.tracking.task.importer;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.MrDao;
import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hadoop.input.ZipFilesFileInputFormat;
import com.cmti.analytics.hbase.loader.BulkLoader;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;
 

/**
 * read MR files and generates HFiles, all files are in HDFS.
 * 
 * 
hdfs dfs -mkdir /data/input/mrxml
hdfs dfs -put *.zip /data/input/mrxml

export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-protocol.jar
hdfs dfs -rm -r /data/output/mrxml
 hadoop jar ~/tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsMrXmlBulkLoader /data/input/mrxml  /data/output/mrxml    -D mapreduce.map.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  


AWS:

hdfs dfs -mkdir /data/input/mrxml
hdfs dfs -put /mnt/raw/all-raw/* /* /*zip /data/input/mrxml

export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
hdfs dfs -rm -r /data/output/mrxml
nohup hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsMrXmlBulkLoader /data/input/mrxml  /data/output/mrxml -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" > HdfsMrXmlBulkLoader.log & 
tail -f HdfsMrXmlBulkLoader.log 

hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles  /data/output/mrxml mr

 * @author Guobiao Mo
 *
 */
public class HdfsMrXmlBulkLoader extends BulkLoader<Mr, Object> {

	protected final Logger logger = LogManager.getLogger(HdfsMrXmlBulkLoader.class); 
		
	private MrDao dao;
	public HdfsMrXmlBulkLoader() throws IOException{
		dao = new MrDao();
		dao.open();		
	}

	@Override
	protected MrDao getDao(){
		return dao;
	}

	@Override
	public Class<? extends BulkLoaderMapper> getMapperClass(){
		return MrXmlBulkLoaderMapper.class;
	}

	@Override
	protected Class<? extends FileInputFormat<LongWritable, Text>> getFileInputFormatClass(){
		return ZipFilesFileInputFormat.class;
	}
	
	public static void main(String[] args) throws Exception {
		HdfsMrXmlBulkLoader loader = new HdfsMrXmlBulkLoader();
        int res = ToolRunner.run(loader, args);
        loader.close();
        System.exit(res);
	}

}
