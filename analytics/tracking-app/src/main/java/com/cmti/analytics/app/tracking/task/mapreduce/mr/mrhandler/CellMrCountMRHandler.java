package com.cmti.analytics.app.tracking.task.mapreduce.mr.mrhandler;

import static com.cmti.analytics.app.tracking.task.mapreduce.mr.SignatureConstant.MR_COUNT;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hbase.task.mapreduce.SingleIntegerValueMRHandler;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;

/**
 * 
key: cell
value: sum mr count

 * @author gmo
 *
 */
public class CellMrCountMRHandler extends SingleIntegerValueMRHandler<Mr> {

	protected static final Logger logger = LogManager.getLogger(CellMrCountMRHandler.class);

	private static final StringArrayWritable ONE = new StringArrayWritable(1);
	
	@Override
	public void doMap(Mr mr, org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {
		int cell = mr.getCell(); 
		String keyStr = MRUtil.buildKey(getSignature(), cell);
				
		key.set(keyStr);
		context.write(key,  ONE);
	}

	@Override
	public void doReduce(Text keyText, Iterable<StringArrayWritable> ivalues, Context context) throws IOException, InterruptedException {
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
