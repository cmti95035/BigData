package com.cmti.analytics.hbase.task.mapreduce;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;

import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
/**
 * An abstract MRHandler for single int value
 * @author Guobiao Mo
 *
 */
public abstract class IntegerLongValueMRHandler<T extends HBaseObject> extends BaseMRHandler<T>{

	//value is a pair of integer and long, usually count and sum
	@Override
	protected List<Object> combineReduce(Text keyText, Iterable<StringArrayWritable> ivalues) {
		String keyStr = keyText.toString();
		if(keyStr.startsWith(getSignature()) == false) {
			return null;
		}
		

		Iterator<StringArrayWritable> it=ivalues.iterator();
		int count = 0;
		long sum = 0L;
		
		while(it.hasNext()){
			StringArrayWritable array = it.next();
			String[] pair = array.toStrings();
			
			count += Integer.parseInt(pair[0]);
			sum += Long.parseLong(pair[1]);
		}
		ArrayList<Object> ret = new ArrayList<Object>();
		ret.add(count);
		ret.add(sum);
		
		return ret;
	}

}
