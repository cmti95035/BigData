package com.cmti.analytics.hbase.task.mapreduce.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.hadoop.io.Text;

import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.BaseMRHandler;

/**
 * Utility class for MapReduce job.
 * @author Guobiao Mo
 *
 */
public class MRUtil {
	public static final String KEY_SEPARATOR = "#";//keyStr.split use regular expression, KEY_SEPARATOR need to avoid special char in regexp.

	public static String buildKey(Object... objects) {
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		for(Object obj:objects) {
			if(first){
				first = false;
			}else{
				sb.append(KEY_SEPARATOR);
			}
			sb.append(obj==null?"":obj.toString());
		}
		
		return sb.toString();
	}

	/**
	 * for an object with null value passed to buildKey(), from parseKey() you get "" instead of null.
	 * @param keyText
	 * @return
	 */
	public static String[] parseKey(Text keyText) {
		return parseKey(keyText.toString());
	}

	public static String[] parseKey(String keyStr) {
		return keyStr.split(KEY_SEPARATOR);
	}

/**
 * for a MR class (EventDateMR), find all its MRHandlers, all handlers must be in sub package "mrhandler".
 * also check app property like
 * -D_app_config.com.cmti.analytics.app.tracking.task.mapreduce.mr.mrhandler.CellMrCountMRHandler=true
 * 
 * 
 * @param mrClazz MR class 
 * @return
 */
	public static <T extends HBaseObject> List<BaseMRHandler<T>> getMRHandler(Class mrClazz){		
		String mrClassName = mrClazz.getName();
		int i = mrClassName.lastIndexOf('.');
		String prefix = mrClassName.substring(0, i+1) + "mrhandler.";//assume all handlers are in sub package "mrhandler".
		
		List<BaseMRHandler<T>> ret = new ArrayList<>();
		List<String> handlerNames = getHandlerNames(prefix);
		for(String handlerName : handlerNames) {
			Class<BaseMRHandler<T>> clazz;
			try {
				clazz = (Class<BaseMRHandler<T>>)Class.forName(handlerName);
				BaseMRHandler<T> handler = clazz.newInstance();
				ret.add(handler);
			} catch (Exception e) {
				throw new RuntimeException(handlerName, e);
			} 
		}
			
		return ret;
	}	

	private static List<String> getHandlerNames(String prefix) {
		List<String> ret = new ArrayList<>();
		
		Configuration config = Config.getConfig();
		//Iterator<String> keys = config.getKeys(prefix);//does not work
		Iterator<String> keys = config.getKeys();
		while(keys.hasNext()){
			String key = keys.next();
			if(key.startsWith(prefix) && config.getBoolean(key)){
				ret.add(key);
			}
		}
		
		return ret;
	}
}



