package com.cmti.analytics.app.tracking.hbase.domain.mapping;
 
import java.util.*;

import com.cmti.analytics.hbase.annotation.Column;
import com.cmti.analytics.hbase.annotation.CompositeKey;
import com.cmti.analytics.hbase.annotation.Key;
import com.cmti.analytics.hbase.annotation.Table;
import com.cmti.analytics.hbase.dao.HBaseObject;

/** 
 * @author gmo
 * 
 * 
 */
@Table(name = RoadTestMapping.DEFAULT_TABLE, 
		defaultCf=RoadTestMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class RoadTestMapping extends HBaseObject{

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "road_test";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}

	@Key
	public String roadTestId; 
	
	//columns//////////////////////////////////////////////////////////////////////

	@Column(value = "road_id")
	public Integer roadId;

	@Column(value = "time")
	public Date time;

	@Column(value = "dir")//do we need this? TODO
	public Boolean direction;

}
