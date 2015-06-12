package com.cmti.analytics.app.station.task.importer;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.cmti.analytics.app.station.hbase.dao.RecordSigDao;
import com.cmti.analytics.app.station.hbase.dao.StationDao;
import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.app.station.hbase.domain.Station;
import com.cmti.analytics.hbase.loader.BulkLoader;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;
import com.cmti.analytics.util.RandomGenInt;
import com.cmti.analytics.util.RandomGenLong;
 

/**
 * read Station dump file and generates HFiles, all files are in HDFS.
 * 
export HADOOP_OPTS="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"
 export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-protocol.jar
hdfs dfs -rm -r /user/cloudera/output/sig
 hadoop jar station-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.station.task.importer.HdfsRecordSigBulkLoader /user/cloudera/input/sig  /user/cloudera/output/sig    -D mapred.child.java.opts="-Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml"  
 
 hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles /user/cloudera/output/sig  sig
 

 * @author Guobiao Mo
 *
 */
public class HdfsRecordSigBulkLoader extends BulkLoader<RecordSig, Integer> {

	protected final Logger logger = LogManager.getLogger(HdfsRecordSigBulkLoader.class); 
		
	private RecordSigDao dao;
	public HdfsRecordSigBulkLoader() throws IOException{
		dao = new RecordSigDao();
		dao.open();		
	}

	@Override
	protected RecordSigDao getDao(){
		return dao;
	}

	@Override
	public Class<? extends BulkLoaderMapper> getMapperClass(){
	//	BulkLoaderMapper<RecordSig, Integer> mapper = new BulkLoaderMapper<RecordSig, Integer>();//FIXME BulkLoaderMapper<RecordSig, Integer>.class works?
//		return mapper.getClass();
		
		return RecordSigMapper.class;
	}

	public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new HdfsRecordSigBulkLoader(), args);
        System.exit(res);        
	}
}

