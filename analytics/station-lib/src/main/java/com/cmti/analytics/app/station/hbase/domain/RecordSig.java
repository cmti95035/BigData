package com.cmti.analytics.app.station.hbase.domain;

import java.util.Date;

import com.cmti.analytics.app.station.hbase.domain.bean.RecordSigBean; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecordSig extends RecordSigBean {
	protected static final Logger logger = LogManager.getLogger(RecordSig.class);

	@Override
	public String toString(){
		return String.format("RecordSigBean(id=%s imsi=%s cell=%s result=%s evet type=%s date = %s)", id, imsi, cell, result, eventType, eventDate);
	}

	public void setEventDate(long time){
		setEventDate(new Date(time));
	}
}
