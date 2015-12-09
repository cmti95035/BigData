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
 * read XML format MR files and generates HFiles, all files are in HDFS.
 * input files are zipped files which are read through ZipFilesFileInputFormat.
 * 
hdfs dfs -mkdir /data/input/mrzip
hdfs dfs -put *.zip /data/input/mrzip

export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-protocol.jar
hdfs dfs -rm -r /data/output/mrzip
 hadoop jar ~/tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.ZipMrBulkLoader /data/input/mrzip /data/output/mrzip -D mapreduce.map.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  


AWS:

hdfs dfs -mkdir /data/input/mrxml
hdfs dfs -put /mnt/raw/all-raw/* /* /*zip /data/input/mrxml

export HADOOP_OPTS="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
hdfs dfs -rm -r /data/output/mrxml
nohup hadoop jar tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.HdfsMrXmlBulkLoader /data/input/mrxml  /data/output/mrxml -D mapreduce.map.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" -D mapreduce.reduce.java.opts="-Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml" > HdfsMrXmlBulkLoader.log & 
tail -f HdfsMrXmlBulkLoader.log 

hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles  /data/output/mrzip mr

 * @author Guobiao Mo
 *
 */
public class ZipMrBulkLoader extends BulkLoader<Mr, Object> {

	protected final Logger logger = LogManager.getLogger(ZipMrBulkLoader.class); 
		
	private MrDao dao;
	public ZipMrBulkLoader() throws IOException{
		dao = new MrDao();
		dao.open();		
	}

	@Override
	protected MrDao getDao(){
		return dao;
	}

	@Override
	public Class<? extends BulkLoaderMapper> getMapperClass(){
		return ZipMrBulkLoaderMapper.class;
	}

	@Override
	protected Class<? extends FileInputFormat<LongWritable, Text>> getFileInputFormatClass(){
		return ZipFilesFileInputFormat.class;
	}
	
	public static void main(String[] args) throws Exception {
		ZipMrBulkLoader loader = new ZipMrBulkLoader();
        int res = ToolRunner.run(loader, args);
        loader.close();
        System.exit(res);
	}

}