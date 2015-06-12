package com.cmti.analytics.hbase.task.mapreduce;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;

import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
/**
 * An abstract MRHandler that does nothing in doCombine().
 * Example used by TrackingMRHandler
 * @author Guobiao Mo
 *
 */
public abstract class RelayCombineMRHandler<T extends HBaseObject> extends BaseMRHandler<T>{
	@Override
	public void doCombine(Text keyText, Iterable<StringArrayWritable> ivalues, Context context) throws IOException, InterruptedException {
		String keyStr = keyText.toString();
		if(keyStr.startsWith(getSignature()) == false) {
			return;
		}
		
		Iterator<StringArrayWritable> it=ivalues.iterator();
		while(it.hasNext()){
			StringArrayWritable array = it.next();
			context.write(keyText, array);		
		}
	}

	@Override
	protected Collection<? extends Object> combineReduce(Text keyText, Iterable<StringArrayWritable> ivalues) {
		return null;
	}

}
