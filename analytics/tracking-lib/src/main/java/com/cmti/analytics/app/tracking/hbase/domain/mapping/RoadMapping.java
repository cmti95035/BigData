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
 * 
 */
@Table(name = RoadMapping.DEFAULT_TABLE, 
		defaultCf=RoadMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class RoadMapping extends HBaseObject{

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "road";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}

	//skip Province 
	@Key
	public Integer roadId; 
	
	//columns//////////////////////////////////////////////////////////////////////

	@Column(value = "province")
	public String province;	

	@Column(value = "city")
	public String city;	

	@Column(value = "name")//like 101, 880
	public String name;	

	@Column(value = "loop")//is a loop like 3 huan?
	public Boolean loop;	

	
}
