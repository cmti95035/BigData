package com.cmti.analytics.app.station.task.mapreduce.sig.mrhandler;



import static com.cmti.analytics.app.station.task.mapreduce.sig.SignatureConstant.INTERVAL_EVENT_COUNT;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import redis.clients.jedis.Jedis;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.app.station.olapdatabase.domain.SigHistory;
import com.cmti.analytics.app.station.olapdatabase.service.DimStationService;
import com.cmti.analytics.app.station.olapdatabase.service.FactImsiService;
import com.cmti.analytics.app.station.olapdatabase.service.SigHistoryService;
import com.cmti.analytics.hbase.task.mapreduce.SingleIntegerValueMRHandler;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
import com.cmti.analytics.util.DateUtil;
import com.cmti.analytics.util.SpringUtil;
import com.cmti.analytics.util.StringUtil;

/**
 * 
key: day of week, hour
value: sum event count

 * @author gmo
 *
 */

public class IntervalEventCountMRHandler extends SingleIntegerValueMRHandler<RecordSig> {//SingleIntegerValueMRHandler

	protected static final Logger logger = LogManager.getLogger(IntervalEventCountMRHandler.class);
	private static final StringArrayWritable ONE = new StringArrayWritable(1);
	//public static final String SIGNATURE = COUNT;

	protected SigHistoryService sigHistoryService;	

	protected Jedis jedis = new Jedis("quickstart.cloudera");
	//protected FactImsiService factImsiService;	
//	protected FactNonAggregateService factNonAggregateService; 
/*
	@Override
	public IntervalEventCountMRHandler initMap(){
		ApplicationContext springContext = SpringUtil.getApplicationContext();	
//		osVersionService = springContext.getBean("osVersionService", OsVersionService.class);		
		return this;
	}
	*/
	
	@Override
	public IntervalEventCountMRHandler initReduce() {
		ApplicationContext springContext = SpringUtil.getApplicationContext();
		sigHistoryService = springContext.getBean("sigHistoryService", SigHistoryService.class);
		return this;
	}

	@Override
	public void doMap(RecordSig sig, org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {
		Date date = sig.getEventDate();
		int[] dateKeys = DateUtil.toKeyWeekHour(date);
		
		int cell=sig.getCell();	 
		String keyStr = MRUtil.buildKey(getSignature(), cell, dateKeys[0], dateKeys[1]); 
		key.set(keyStr);
		
		context.write(key, ONE);
	}

	@Override
	public void doReduce(Text keyText, Iterable<StringArrayWritable> ivalues, Context context) throws IOException, InterruptedException {
		List<Integer> result = combineReduce(keyText, ivalues);	
		if(result==null){
			return;//this happens if Signature not match
		}
		int count = result.get(0);

		String[] keys = MRUtil.parseKey(keyText);
		int cell = StringUtil.getInt(keys[1]);//0 is SIGNATURE
		int dayOfWeek = StringUtil.getInt(keys[2]);
		int hour = StringUtil.getInt(keys[3]);

		jedis.set(String.format("history:%s:%s:%s", cell, dayOfWeek, hour), String.valueOf(count));
		/*
		SigHistory history = new SigHistory();
		history.setCell(cell);
		history.setDayOfWeek(dayOfWeek);
		history.setHour(hour);
		history.setImsiCount(count);
		sigHistoryService.save(history);
//		logger.error("cell {} weekDay {} hour {} count {}", cell , dayOfWeek, hour, count);
		/*
		String weekDay=keys[2];
		
		Long profileId = StringUtil.getLong(keys[2], -1L);
		Long ean = StringUtil.getLong(keys[3]);
		String dateKey = keys[4];
		
		Reading reading = new Reading();

		reading.setAccountId(accountId);
		reading.setProfileId(profileId);	
		reading.setEan(ean);
		reading.addUnmapped(dateKey, count);

		readingDao.upsert(reading);		
		
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
		return INTERVAL_EVENT_COUNT;
	}
 
}
