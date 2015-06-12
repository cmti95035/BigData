package com.cmti.analytics.app.tracking.hbase.domain.mapping;
 
import java.util.*;

import com.cmti.analytics.hbase.annotation.Column;
import com.cmti.analytics.hbase.annotation.CompositeKey;
import com.cmti.analytics.hbase.annotation.Table;
import com.cmti.analytics.hbase.dao.HBaseObject;

/** 
 * @author Guobiao Mo
 *
 *we may use MD5 to encrypt the imsi 
 *http://howtodoinjava.com/2013/07/22/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
 *https://www.informit.com/guides/content.aspx?g=java&seqNum=30
 *
 *
 *MD5 is 16-byte, while long is 8-byte
 *
 *we may add salt to the hash, salt will not be remembered. but we lost connection between salts.
 * 
 */
@Table(name = MrMapping.DEFAULT_TABLE, 
		defaultCf=MrMapping.DEFAULT_CF,
		hasUnmapped=false)  
public class MrMapping extends HBaseObject{

	public final static String DEFAULT_CF = "d";
	public final static String DEFAULT_TABLE = "mr";
	//keys 

	@Override
	public String getDefaultColumnFamily(){
		return DEFAULT_CF;
	}

	@CompositeKey(order=0)
	public Long imsi;

	@CompositeKey(order=1)
	public Date time;

	//columns//////////////////////////////////////////////////////////////////////

	@Column(value = "ce")
	public Integer cellId; 

	@Column(value = "rscp")//MR.TdScPccpchRscp
	public Integer rscp;	

}
