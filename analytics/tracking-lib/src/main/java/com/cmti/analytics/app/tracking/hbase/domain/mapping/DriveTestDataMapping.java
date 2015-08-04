package com.cmti.analytics.app.tracking.hbase.domain.mapping;
 
import java.util.*;

import com.cmti.analytics.hbase.annotation.Column;
import com.cmti.analytics.hbase.annotation.CompositeKey;
import com.cmti.analytics.hbase.annotation.Table;
import com.cmti.analytics.hbase.dao.HBaseObject;

/** 
 * @author Guobiao Mo
 * 
 * 
 */
@Table(name = DriveTestDataMapping.DEFAULT_TABLE, 
		defaultCf=DriveTestDataMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class DriveTestDataMapping extends HBaseObject{

	public final static String COUNTER_CF = "c";
	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "drive_test_data";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}

	@CompositeKey(order=0)
	public Integer roadId;
	
	@CompositeKey(order=1)
	public String driveTestId;

	@CompositeKey(order=2)
	public Integer frame; 
	
	//columns////////////////////////////////////////////////////////////////////// 

	@Column(value = "cell")
	public Integer cell;

	@Column(value = "time")
	public Date time;

	@Column(value = "lon")
	public Double longitude;

	@Column(value = "lat")
	public Double latitude;
	
	@Column(value = "rscp")//PCCPCH RSCP/MR.TdScPccpchRscp
	public Integer rscp;

	@Column(value = "mr_count", cf=COUNTER_CF)//MR that can be projected to this DriveTestData
	public Long mrCount;

	@Column(value = "mr_rscp_sum", cf=COUNTER_CF)//rscp sum of the MRs
	public Long mrRscpSum;
	
	
	/*
	public static void main(String[] args){//float is not enough for longitude/latitude
		Double d1=  104.034714;
		Double d2=  30.605476;
		d1=  (d1+1.);
		d2=  (d2+1.5);
		System.out.println(d1);
		System.out.println(d2);

		Float f1=  (float) 104.034714999;
		Float f2=  (float) 30.605476999;
		f1=  (float) (f1+1.);
		f2=  (float) (f2+1.5);
		System.out.println(f1);
		System.out.println(f2);
	}*/
}
