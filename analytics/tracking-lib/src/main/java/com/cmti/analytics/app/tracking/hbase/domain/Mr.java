package com.cmti.analytics.app.tracking.hbase.domain;
 
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.bean.MrBean;

public class Mr extends MrBean implements Comparable<Mr>{
	protected static final Logger logger = LogManager.getLogger(Mr.class);

	public void setTime(long time){
		this.time = new Date(time);
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
