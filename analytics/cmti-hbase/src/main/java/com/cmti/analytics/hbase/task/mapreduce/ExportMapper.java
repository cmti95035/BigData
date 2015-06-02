package com.cmti.analytics.hbase.task.mapreduce;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
/**
 * 
 * @author Guobiao Mo
 *
 * @param <T>
 */
public abstract class ExportMapper<T extends HBaseObject> extends TableMapper<ImmutableBytesWritable, StringArrayWritable> {

		protected static final Logger logger = LogManager.getLogger(ExportMapper.class); 

		protected HBaseGenericDao<T, ?> dao;// no open(), don't need to access hbase table
	   	
	   	/*
	   	 * note: this dao is not opened, since it is for parse result only.
	   	 */
	   	protected abstract HBaseGenericDao<T, ?> setupDao() throws IOException;

		@Override
		protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context){
			Config.getConfig();			
			
			try {
				dao = setupDao();
			} catch (IOException e) {
				logger.error("setupDao error", e);
			}
		}

		@Override
	   	public void map(ImmutableBytesWritable row, Result r, Context context) throws IOException, InterruptedException {
			T t = dao.parseResult(r);
	   			
			if(t==null){
				logger.error("dao.parseResult(r) return null. "+r);
				return;
			}
	   			
			doMap(row, t, context);
	   	}
		
		abstract public void doMap(ImmutableBytesWritable row, T t, Context context)  throws IOException, InterruptedException ;

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
				super.cleanup(context);
		}

}