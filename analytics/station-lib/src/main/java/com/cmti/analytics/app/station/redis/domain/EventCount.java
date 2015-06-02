package com.cmti.analytics.app.station.redis.domain;

import java.util.Date;

import com.cmti.analytics.redis.RedisObject;
import com.cmti.analytics.util.DateUtil;
import com.cmti.analytics.util.StringUtil;
 
public class EventCount extends RedisObject implements Comparable<EventCount>{ 

	public int cell;
	public int intervale;

	public Date baseDate;	
	int count;

	public EventCount (String key, String value) {
		setKey(key);
		setValue(value);
		
		
		String[] keys = parseKey();
		cell = StringUtil.getInt(keys[1], 0);//0 is 'c'
		baseDate = DateUtil.parseKeyStringMinute(keys[2]);
		intervale = StringUtil.getInt(keys[3], 0);
		
		count = StringUtil.getInt(value, 0);
	}

	public String buildKey() {
		return  buildKey("c", cell, DateUtil.toKeyStringMinute(baseDate), intervale);
	}

	public static String buildKey(int cell, Date date, int intervale) {
		return  buildKey("c", cell, DateUtil.toKeyStringMinute(date), intervale);
	}

	public static String buildKey(int cell, long date, int intervale) {
		return  buildKey(cell, new Date(date), intervale);
	}
	
	////////////////////////
	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public int getIntervale() {
		return intervale;
	}

	public void setIntervale(int intervale) {
		this.intervale = intervale;
	}

	public Date getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(Date baseDate) {
		this.baseDate = baseDate;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(EventCount o) {
		int ret = cell-o.cell;
		if(ret!=0){
			return ret;
		}
		ret= o.baseDate.compareTo(baseDate);
		return ret;
	}
	
} 
