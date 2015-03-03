package com.cmti.analytics.app.station.hbase.domain.mapping;
 
import java.util.*;

import com.cmti.analytics.hbase.annotation.Column;
import com.cmti.analytics.hbase.annotation.CompositeKey;
import com.cmti.analytics.hbase.annotation.Key;
import com.cmti.analytics.hbase.annotation.Table;
import com.cmti.analytics.hbase.dao.HBaseObject;

/**
 * 
 * MB_SIG_RECORD

 * @author gmo
 *
 */
@Table(name = RecordSigMapping.DEFAULT_TABLE, 
		defaultCf=RecordSigMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class RecordSigMapping extends HBaseObject{ 	

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "sig";
	//keys 

	@Override
	public String getDefaultColumFamily(){
		return DEFAULT_CF;
	}

		@Key 
		public Long id; 		
			
		//columns//////////////////////////////////////////////////////////////////////
		@Column(value = "date")
		public Date eventDate;		
		
		@Column(value = "imsi")
		public String imsi; 

		@Column(value = "sigs")
		public String sigSession;
		
		@Column(value = "subs")
		public Integer subSession;	

		@Column(value = "event")
		public Integer eventType;	

		@Column(value = "lac")
		public Integer lac;

		@Column(value = "cell")
		public Integer cell;

		@Column(value = "pcu")
		public Integer pcu;

		@Column(value = "rac")
		public Integer rac;
		
		@Column(value = "res")
		public Integer result;	

		@Column(value = "fail")
		public Integer failReason;		

		@Column(value = "dur")//in msec
		public Integer sigDuration;

		@Column(value = "chan")
		public Integer channelType;

		@Column(value = "busi")
		public Integer businessType;	 

		@Column(value = "apn")
		public String apn;	 
		
		@Column(value = "ip")
		public String apnIpv4;	 

		@Column(value = "hr")
		public Integer hour;	 
}
