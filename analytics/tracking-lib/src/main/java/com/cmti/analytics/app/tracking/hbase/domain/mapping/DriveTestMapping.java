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
@Table(name = DriveTestMapping.DEFAULT_TABLE, 
		defaultCf=DriveTestMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class DriveTestMapping extends HBaseObject{

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "drive_test";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}

	@Key
	public String driveTestId; 
	
	//columns//////////////////////////////////////////////////////////////////////

	@Column(value = "road_id")
	public Integer roadId;

	@Column(value = "time")
	public Date time;

	@Column(value = "dir")//do we need this? TODO
	public Boolean direction;

}
