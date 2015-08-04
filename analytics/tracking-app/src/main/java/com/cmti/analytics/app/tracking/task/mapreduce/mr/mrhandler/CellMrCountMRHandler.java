package com.cmti.analytics.app.tracking.task.mapreduce.mr.mrhandler;

import static com.cmti.analytics.app.tracking.task.mapreduce.mr.SignatureConstant.MR_COUNT;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hbase.task.mapreduce.SingleIntegerValueMRHandler;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;

/**
 * print out cell id and its MR count.
key: cell
value: sum MR count

 * @author Guobiao Mo
 *
 */
public class CellMrCountMRHandler extends SingleIntegerValueMRHandler<Mr> {

	protected static final Logger logger = LogManager.getLogger(CellMrCountMRHandler.class);

	private static final StringArrayWritable ONE = new StringArrayWritable(1);
	
	@Override
	public void doMap(Mr mr, Mapper<ImmutableBytesWritable, Result, Text, StringArrayWritable>.Context context) throws IOException, InterruptedException {
		int cell = mr.getCellId(); 
		String keyStr = MRUtil.buildKey(getSignature(), cell);
				
		key.set(keyStr);
		context.write(key,  ONE);
	}

	@Override
	public void doReduce(Text keyText, Iterable<StringArrayWritable> ivalues, Reducer<Text, StringArrayWritable, ImmutableBytesWritable, Mutation>.Context context) throws IOException, InterruptedException {
		List<Integer> result = combineReduce(keyText, ivalues);
		if(result==null){
			return;
		}

		int count = result.get(0);
		String key = keyText.toString();
		
		logger.info("{}:\t{}", key, count);
	}

	@Override
	protected String getSignature() {
		return MR_COUNT;
	}

}
