package com.cmti.analytics.hbase.export;
 
import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.cmti.analytics.hbase.dao.DaoScanner;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.mapping.MappingMetaData;

/**
 * this class wraps a DaoScanner into a Iterator<String> for data export
 * @author Guobiao Mo
 *
 * @param <T>
 */
	public  class ExportIterator<T extends HBaseObject, P> implements Iterator<String>, Closeable{
		 DaoScanner<T> scanner;
		 MappingMetaData<T, P> mapping;
		 List<String> columns;//columns to be exported. if empty, all will be exported
		 boolean includeUnmapped;

		public ExportIterator(MappingMetaData<T, P> mapping, DaoScanner<T> scanner, List<String> columns){
			this(mapping, scanner, columns, false);
		}
		
		public ExportIterator(MappingMetaData<T, P> mapping, DaoScanner<T> scanner, List<String> columns, boolean includeUnmapped){
			this.mapping = mapping;
			this.scanner = scanner;
			this.columns = columns;//TODO if this is empty, export all columns
			this.includeUnmapped = includeUnmapped;
		}
		
		@Override
		public boolean hasNext() {
			throw new RuntimeException("operation not supported");
		}

		@Override
		public String next() {
			T t = scanner.next();
			if(t==null)
				return null;
			
			StringBuilder sb = new StringBuilder();
			
			Object[] keys = mapping.getRowKeys(t);
			sb.append(format(keys));
			
			List<Object> objs = mapping.getColumnValues(t, columns);
			if(objs != null && objs.size() > 0){
				sb.append(",").append(format(objs));
			}
			
			if(includeUnmapped){
				String json = t.getUnmappedAsString();
				if(StringUtils.isNotBlank(json)){
					sb.append(",\"").append(json).append("\"");
				}
			}
			
			return sb.toString();
		}
		
		@Override
		public void remove() {
			throw new RuntimeException("operation not supported");
		}	

		private String format(Collection<?> collection){			
			Object[] objs = collection.toArray();
	    	return format(objs);
		}


		private String format(Object[] objs){
	    	for(int i=0; i<objs.length; i++){
	    		Object obj = objs[i];
	    		if(obj instanceof String){//don't touch it if it is not String
	    			String str = (String)obj;
	    			if(str.indexOf(',') > -1){//TODO delimiter can be passed in, low priority
	    				str = str.replaceAll("\"", "\"\"");
		    			objs[i] = "\""+str+"\"";
	    			}
	    		}
	    	}

	    	return StringUtils.join(objs, ",");
		}

		@Override
		public void close() throws IOException {
			scanner.close();			
		}
	}

