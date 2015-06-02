package com.cmti.analytics.app.station.redis.domain;

import java.util.Date;

import com.cmti.analytics.redis.RedisObject;
import com.cmti.analytics.util.DateUtil;
import com.cmti.analytics.util.StringUtil;
 
public class HistoryEventCount extends RedisObject{// implements Comparable<HistoryEventCount>{ 

	public int cell;
	public int dayInWeek;

	public int hour;	
	int count;

	public HistoryEventCount (String key, String value) {
		setKey(key);
		setValue(value);		
		
		String[] keys = parseKey();
		cell = StringUtil.getInt(keys[1], -1);//0 is 'c'
		dayInWeek = StringUtil.getInt(keys[2], -1);
		hour = StringUtil.getInt(keys[3], -1);
		
		count = StringUtil.getInt(value, 0);
	}

	public static String buildKey(int cell, long date) {
		return buildKey(cell, new Date(date));
	}
	
	public static String buildKey(int cell, Date date) {
		int[] keys=DateUtil.toKeyWeekHour(date);
		
		return  buildKey("history", cell, keys[0],  keys[1]);
	}
	
	////////////////////////
	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public int getDayInWeek() {
		return dayInWeek;
	}
	
	public void setDayInWeek(int dayInWeek) {
		this.dayInWeek = dayInWeek;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
/*
	@Override
	public int compareTo(HistoryEventCount o) {
		int ret = cell-o.cell;
		if(ret!=0){
			return ret;
		}
		ret= o.baseDate.compareTo(baseDate);
		return ret;
	}
	*/
} 
