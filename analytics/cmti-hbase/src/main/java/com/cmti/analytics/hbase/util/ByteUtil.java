package com.cmti.analytics.hbase.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Byte utility
 * @author Guobiao Mo
 *
 */
public class ByteUtil {

	private static final Logger logger = LogManager.getLogger(ByteUtil.class);

	public static byte[] toBytes(Object obj){
		if(obj==null){
			return null;
		}else if(obj instanceof Long){
			return Bytes.toBytes((Long)obj);
		}else if(obj instanceof Integer){
			return Bytes.toBytes((Integer)obj);
		}else if(obj instanceof Boolean){
			return Bytes.toBytes((Boolean)obj);
		}else if(obj instanceof Double){
			return Bytes.toBytes((Double)obj);
		}else if(obj instanceof Float){
			return Bytes.toBytes((Float)obj);
		}else if(obj instanceof String){
			return Bytes.toBytes((String)obj);
		}else if(obj instanceof Date){
			return Bytes.toBytes(((Date)obj).getTime());
		}else{
			throw new RuntimeException("not supported:"+obj.getClass());
		}
	}

	public static byte[] add(final Byte... bytes) {
		List<Byte> list = new ArrayList<Byte> ();
		
		for(Byte b : bytes) {
			if(b==null){
				continue;
			}
			list.add(b);
		}
		
		byte[] ret = new byte[list.size()];

		for(int i = 0; i < list.size(); i++) {
			ret[i] = list.get(i);
		}
		
		return ret;
	}

	public static byte[] add(final byte[]... byteArrays) {
		int length = 0;

		for (int i = 0; i < byteArrays.length; i++) {
			length += byteArrays[i].length;
		}

		byte[] result = new byte[length];

		int start = 0;
		for (int i = 0; i < byteArrays.length; i++) {
			System.arraycopy(byteArrays[i], 0, result, start, byteArrays[i].length);
			start += byteArrays[i].length;
		}

		return result;
	}

}