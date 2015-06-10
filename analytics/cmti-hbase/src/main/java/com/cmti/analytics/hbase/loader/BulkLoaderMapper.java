package com.cmti.analytics.hbase.loader;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.hbase.dao.ExportDao;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.util.ReflectUtil;

/**
 * 
 * Bulk loader to put data into HBase using MapReduce.
 * if outputPath != null, it generates HFile in hdfs outputPath.
 * else, it uses the TableOutputFormat API to insert data directly to HBase.
 * @author Guobiao Mo
 *
 * @param <T>
 * @param <P>
 */

public class BulkLoaderMapper<T extends HBaseObject, P> extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
		protected final Logger logger = LogManager.getLogger(BulkLoaderMapper.class); 
			
		protected ExportDao<T, P> dao;

		@Override
		protected void setup(Context context) throws IOException {
			Configuration config = context.getConfiguration();
			String className = config.get("dao.class"); //setup in BulkLoader.java
			try {
				Class daoClass = Class.forName(className);
				dao = (ExportDao<T, P>) ReflectUtil.newInstance(daoClass);
			} catch (ClassNotFoundException e) {
				logger.error(className, e);
			}
		}

		@Override
		public void map(LongWritable offset, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			
			if(StringUtils.isBlank(line)){
				logger.error("got an empty line.");
				return;
			}			

			T t = dao.parseLine(line, context);
				
			if(t == null){
				logger.debug("parse result == null for line:{}", line);
				return;
			}
				
			byte[] bRowKey = dao.getKey(t);
			ImmutableBytesWritable rowKey = new ImmutableBytesWritable(bRowKey);
			try{
				Put p = dao.getPut(t);
				context.write(rowKey, p);
			}catch(Exception e){
				logger.error(line, e);
			}
		}
}
