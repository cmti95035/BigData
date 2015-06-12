package com.cmti.analytics.app.tracking.hbase.domain.mapping;
 
import java.util.*;

import com.cmti.analytics.hbase.annotation.Column;
import com.cmti.analytics.hbase.annotation.CompositeKey;
import com.cmti.analytics.hbase.annotation.Key;
import com.cmti.analytics.hbase.annotation.Table;
import com.cmti.analytics.hbase.dao.HBaseObject;

/** 
 * @author Guobiao Mo 
 * 
 */
@Table(name = MrOnRoadMapping.DEFAULT_TABLE, 
		defaultCf=MrOnRoadMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class MrOnRoadMapping extends HBaseObject{

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "mr_on_road";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}

	@CompositeKey(order=0)
	public Integer roadId; 

//	@CompositeKey(order=1)
	//public Integer cell; 
	
//	@CompositeKey(order=2)
	//public Integer cellPercent; 

	@CompositeKey(order=1)	
	public Date time;	

	@CompositeKey(order=2)
	public Long imsi; 
	
	//columns////////////////////////////////////////////////////////////////////// 
	@Column(value = "ce")
	public Integer cell; 

	@Column(value = "lon")
	public Double longitude;

	@Column(value = "lat")
	public Double latitude;
	
	@Column(value = "rscp")//MR.TdScPccpchRscp
	public Integer rscp;
}
