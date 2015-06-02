package com.cmti.analytics.app.station.hbase.domain.mapping;
 
import java.util.*;

import com.cmti.analytics.hbase.annotation.Column;
import com.cmti.analytics.hbase.annotation.CompositeKey;
import com.cmti.analytics.hbase.annotation.Key;
import com.cmti.analytics.hbase.annotation.Table;
import com.cmti.analytics.hbase.dao.HBaseObject;

/**
 * sample data https://docs.google.com/spreadsheets/d/1FSPbGAesZmjGQoOKmOcBXWVwpK2aEBTawB37ESy2QmM/edit#gid=821411373
 * 
 * @author gmo
 *
 */
@Table(name = StationMapping.DEFAULT_TABLE, 
		defaultCf=StationMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class StationMapping extends HBaseObject{ 	

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "station";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}
	
		@Key 
		public Integer id; 		
			
		//columns//////////////////////////////////////////////////////////////////////

		@Column(value = "bsc")
		public String bsc; 

		@Column(value = "cgi")
		public String cgi;	

		@Column(value = "lng")
		public Double longitude;	
		
		@Column(value = "lat")
		public Double latitude;
		

		@Column(value = "type")
		public String type;	 
		
		@Column(value = "lac")
		public Integer lac;

		@Column(value = "ci")
		public Integer ci;
		
		@Column(value = "angle")
		public Integer angle;
			
		// macro station and wireless room sub-station 
		@Column(value = "room")
		public Boolean room;
}
