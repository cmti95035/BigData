package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;
/**
 * Mapper for Long
 * @author Guobiao Mo
 *
 */
public class LongMapper extends Mapper<Long> {
	public static LongMapper instance = new LongMapper();

	@Override
	public Long fromBytes(byte[] bytes, int offset, int length) {
		return ArrayUtils.isEmpty(bytes) ? null : Bytes.toLong(bytes, offset, length);
	}
	
}
