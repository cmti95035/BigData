package com.cmti.analytics.app.station.task.mapreduce.sig.mrhandler;

import static com.cmti.analytics.app.station.task.mapreduce.sig.SignatureConstant.COUNT;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.app.station.olapdatabase.domain.FactImsi;
import com.cmti.analytics.app.station.olapdatabase.service.DimStationService;
import com.cmti.analytics.app.station.olapdatabase.service.FactImsiService;
import com.cmti.analytics.hbase.task.mapreduce.SetValueMRHandler;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
import com.cmti.analytics.util.DateUtil;
import com.cmti.analytics.util.SpringUtil;

/**
 * 
key: 
value: sum user count

 * @author Guobiao Mo
 *
 */
public class OlapUserCountMRHandler extends SetValueMRHandler<RecordSig> {

	protected static final Logger logger = LogManager.getLogger(OlapUserCountMRHandler.class);
	//public static final String SIGNATURE = COUNT;

	protected DimStationService dimStationService;	
	protected FactImsiService factImsiService;	

	@Override
	public OlapUserCountMRHandler initMap(){
//		ApplicationContext springContext = SpringUtil.getApplicationContext();		
		return this;
	}
	
	@Override
	public OlapUserCountMRHandler initReduce() {
		ApplicationContext springContext = SpringUtil.getApplicationContext();
		factImsiService = springContext.getBean("factImsiService", FactImsiService.class);	
		return this;
	}

	@Override
	public void doMap(RecordSig sig, Mapper<ImmutableBytesWritable, Result, Text, StringArrayWritable>.Context context) throws IOException, InterruptedException {
		Date date = sig.getEventDate();
		String[] dateKeys = DateUtil.toKeyStrings(date);
		
		String imsi = sig.getImsi();
		Integer result=sig.getResult();

		value.set(imsi);
		for(int periodType = 0; periodType < dateKeys.length; periodType++) {//time period 0=day, 1=week, etc	 
			String keyStr = MRUtil.buildKey(getSignature(), periodType, dateKeys[periodType], result); 
			String keyStr2 = MRUtil.buildKey(getSignature(), periodType, dateKeys[periodType], 2);
			//logger.info(keyStr+event.getAccountId());
			key.set(keyStr);
			context.write(key, value);
			
			key.set(keyStr2);
			context.write(key, value);
		}
	}

	@Override
	public void doReduce(Text keyText, Iterable<StringArrayWritable> ivalues, Reducer<Text, StringArrayWritable, ImmutableBytesWritable, Mutation>.Context context) throws IOException, InterruptedException {
		Set<String> set = combineReduce(keyText, ivalues);
		
		if(set==null){
			return;//this happens if Signature not match
		}

		
		String[] keys = MRUtil.parseKey(keyText);

		int periodType = Integer.parseInt(keys[1]); 
		int dateKey = Integer.parseInt(keys[2]); 
		int result = Integer.parseInt(keys[3]); 
		logger.error("periodType {} dateKey {} result {} count{}", periodType , dateKey, result , set.size());
		

		FactImsi factImsi = new FactImsi();
		factImsi.setPeriodType(periodType);
		factImsi.setDateId(dateKey);
		factImsi.setResultTypeId(result);

		factImsi.setImsiCount(set.size()); 

		try{
			factImsiService.save(factImsi);
		}catch(Exception e){
			logger.error(keyText.toString() ,e);
		}
		
/*
		EventUser eventUser = null;
		
		eventUser = eventUserDao.getByKey(new Integer[]{periodType, eventTypeId, dateId, groupType});
				
		if(eventUser == null){
			eventUser = new EventUser();

			eventUser.setGroupType(groupType);
			eventUser.setPeriodType(periodType);
			eventUser.setDateId(dateId);
			eventUser.setEventTypeId(eventTypeId);
		}
		
		eventUser.addUnmapped(set);

		int userCount = eventUser.getUnmapped().size(); 
		eventUser.setCount(userCount);
*/ 		
	}
	

	@Override
	protected String getSignature() {
		return COUNT;
	}
 
}
