package com.cmti.analytics.app.tracking.hbase.domain;
 
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.bean.MrBean;

public class Mr extends MrBean implements Comparable<Mr>{
	protected static final Logger logger = LogManager.getLogger(Mr.class);

	public void setTime(long time){
		setTime(new Date(time));
	}

	@Override
	public void setImsi(Long imsi){
		super.setImsi(imsi);//TODO this to be done by GenSource. imsi2/time2 is be used by hive mapping and hbase filter
		super.setImsi2(imsi);
	}

	@Override
	public void setTime(Date time){
		super.setTime(time);
		super.setTime2(time);
	}

	@Override
	public int compareTo(Mr o) {
		//compare time only, assuming imsi are the same TODO 
		return time.compareTo(o.getTime());		
	}

	@Override
	public String toString(){
		return String.format("Mr(imsi=%s, time=%s, cellId=%s, rscp=%s)", imsi, time, cellId, rscp);
	}
	
}
