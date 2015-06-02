package com.cmti.analytics.hbase.task.mapreduce;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection; 

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;

import com.cmti.analytics.hbase.dao.HBaseObject; 
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
/**
 * An empty MRHandler
 * @author Guobiao Mo
 *
 */
public abstract class BaseMRHandler<T extends HBaseObject> implements Closeable{

	protected Text key = new Text();
	protected StringArrayWritable value = new StringArrayWritable();
	
	//@Override
 	public void doReduce(Text key, Iterable<StringArrayWritable> values, Context context) throws IOException, InterruptedException {
   	}

	//@Override
	public void doCombine(Text _key, Iterable<StringArrayWritable> values, Context context) throws IOException, InterruptedException {
		Collection<? extends Object> set = combineReduce(_key, values);
		
		if(set==null){
			return;
		}
		
		value.set(set);
		context.write(_key, value);				
	}

	//@Override
	public void doMap(T t, org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {
		
	}

	@Override
	public void close() throws IOException {
		
	}

	public BaseMRHandler<T> initMap(){
		return this;
	}
	public BaseMRHandler<T> initReduce(){
		return this;
	}
 
	protected abstract String getSignature();
 
	protected Collection<? extends Object> combineReduce(Text key,
			Iterable<StringArrayWritable> ivalues) {
		throw new UnsupportedOperationException();
	}
 
}
