package com.cmti.analytics.hbase.task.mapreduce;

import java.io.IOException; 
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.conf.Config; 
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
/**
 * 
 * @author Guobiao Mo
 *
 * @param <T>
 */
public   class FullScanCombiner<T extends HBaseObject> extends Reducer<Text, StringArrayWritable, Text, StringArrayWritable> {// implements IMRHandlerConsumer<T>{

		protected static final Logger logger = LogManager.getLogger(FullScanCombiner.class); 

		protected List<BaseMRHandler<T>> handlers;
		
		@Override
		protected void setup(Context context) {// throws IOException, InterruptedException {
			Config.getConfig();
			handlers = MRUtil.getMRHandler(this.getClass());
			//handlers = setupMRHandlers();
		}

		@Override
	    public void reduce(Text key, Iterable<StringArrayWritable> values, Context context) throws IOException, InterruptedException {

   			for(BaseMRHandler<T> handler : handlers){
   				handler.doCombine(key, values, context);
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