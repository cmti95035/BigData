package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Mapper for String
 * @author Guobiao Mo
 *
 */
public class StringMapper extends Mapper<String> {
	public static StringMapper instance = new StringMapper();

	@Override
	public String fromBytes(byte[] bytes, int offset, int length) {
		return ArrayUtils.isEmpty(bytes) ? null : Bytes.toString(bytes, offset, length);
	}

}
