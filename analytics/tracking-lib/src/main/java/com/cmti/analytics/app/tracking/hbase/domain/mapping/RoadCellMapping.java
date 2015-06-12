package com.cmti.analytics.app.tracking.hbase.domain.mapping;

import java.util.ArrayList;

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
@Table(name = RoadCellMapping.DEFAULT_TABLE, 
		defaultCf=RoadCellMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class RoadCellMapping extends HBaseObject{

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "road_cell";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}

	@CompositeKey(order=0)
	public Integer roadId;

	@CompositeKey(order=1)
	public Integer order;

	//columns//////////////////////////////////////////////////////////////////////

	@Column(value = "len")
	public Integer length;

	@Column(value = "cell")
	public Integer cellId;	

	//@Column(value = "fuz")
//	public Boolean fuzzy;	
	
	@Column(value = "fuid")
	public Integer fuzzyId;//	the leader's order id

	@Column(value = "lon")//Longitude list of the cell
	public ArrayList<Double> longitudeList;
	//public Double[] longitudeArray;
	
	@Column(value = "lat")//Latitude list
	public ArrayList<Double> latitudeList;	
	//public Double[] latitudeArray;	
	
}
