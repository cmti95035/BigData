package com.cmti.analytics.hbase.task.mapreduce;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.io.Text;

import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
/**
 * An abstract MRHandler for a set of unique Strings
 * Use it only if u need to remove duplicated items.
 * @author Guobiao Mo
 *
 */
public abstract class SetValueMRHandler<T extends HBaseObject> extends BaseMRHandler<T>{

	//returned is a set of unique Strings
	@Override
	protected Set<String> combineReduce(Text keyText, Iterable<StringArrayWritable> ivalues) {
		String keyStr = keyText.toString();
		if(keyStr.startsWith(getSignature()) == false) {
			return null;
		}

		Iterator<StringArrayWritable> it=ivalues.iterator();
		Set<String> accountIds = new HashSet<String>();
		while(it.hasNext()){
			StringArrayWritable saw = it.next();
			accountIds.addAll(saw.toSet());
		}

		return accountIds;
	}

}
