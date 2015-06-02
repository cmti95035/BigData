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
public abstract class SingleIntegerValueMRHandler<T extends HBaseObject> extends BaseMRHandler<T>{
	//used by PageTurnedMRHandler and OlapEventCountMRHandler
	@Override
	protected List<Integer> combineReduce(Text keyText, Iterable<StringArrayWritable> ivalues) {
		String keyStr = keyText.toString();
		if(keyStr.startsWith(getSignature()) == false) {
			return null;
		}

		Iterator<StringArrayWritable> it=ivalues.iterator();
		int sum = 0;
		while(it.hasNext()){
			StringArrayWritable array = it.next();
			sum += array.toInt();
		}
		
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(sum);
		
		return ret;
	}	

}
