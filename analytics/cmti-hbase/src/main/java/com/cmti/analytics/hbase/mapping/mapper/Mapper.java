package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;

import com.cmti.analytics.hbase.util.ByteUtil;
 


/**
 * Two-way conversion class to transform byte array to target object and vice versa.
 * @author Guobiao Mo
 * @param <V> target object class
 */
public abstract class Mapper<V>{
	
	//sub class to implement fromBytes(bytes, offset, length) and getSize();
	
	public V fromBytes(byte[] bytes){
		return ArrayUtils.isEmpty(bytes) ? null : fromBytes(bytes, 0, bytes.length);
	}
  
	public byte[] toBytes(V v) {
		return ByteUtil.toBytes(v);
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public int hashCode() {
		return getClass().hashCode();
	}

	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		return this == o || getClass() == o.getClass();
	}

	abstract V fromBytes(byte[] bytes, int offset, int length);

}
