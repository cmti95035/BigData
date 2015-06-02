package com.cmti.analytics.hbase.task.mapreduce;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
/**
 * 
 * @author Guobiao Mo
 *
 * @param <T>
 */
public abstract class FullScanMapper<T extends HBaseObject> extends TableMapper<Text, StringArrayWritable> {

		protected static final Logger logger = LogManager.getLogger(FullScanMapper.class); 
//		private final IntWritable ONE = new IntWritable(1);

		protected HBaseGenericDao<T, ?> dao;// = new EventDao();//no open(), don't need to access hbase table

	   	protected List<BaseMRHandler<T>> handlers;
	   	
	   	/*
	   	 * note: this dao is not opened, since it is for parse result only.
	   	 */
	   	protected abstract HBaseGenericDao<T, ?> setupDao() throws IOException;
	   	//protected abstract List<String> getHandlerNames();

		@Override
		protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context){
//			Config.getConfig();
			
			handlers = MRUtil.getMRHandler(this.getClass());
			//setupMRHandlers();
			//handlers = setupMRHandlers();
			for(BaseMRHandler<T> handler : handlers){
				logger.error("including: "+handler.getClass().getName());
				handler.initMap();
			}
			
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
	   			
			//t.clearDirty(); already done in  dao.parseResult(r);
			for(BaseMRHandler<T> handler : handlers){
				handler.doMap(t, context);//note that if t is modified by one handler, others see the change
			}
	   	}

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
				super.cleanup(context);
	   			for(BaseMRHandler<T> handler : handlers){
	   				handler.close();
	   			}
		}
/*
		protected List<BaseMRHandler<T>> setupMRHandlers(){		
				List<BaseMRHandler<T>> ret = new ArrayList<BaseMRHandler<T>>();
				List<String> handlerNames = getHandlerNames();
				for(String handlerName : handlerNames) {
					Class<BaseMRHandler<T>> clazz;
					try {
						clazz = (Class<BaseMRHandler<T>>)Class.forName(handlerName);
						BaseMRHandler<T> handler = clazz.newInstance();
						ret.add(handler.initMap());
					} catch (Exception e) {
						throw new RuntimeException(handlerName, e);
					} 
				}
				
				return ret;
		}	*/
}