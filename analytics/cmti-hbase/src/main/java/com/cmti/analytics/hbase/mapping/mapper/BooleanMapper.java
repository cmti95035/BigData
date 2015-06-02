package com.cmti.analytics.hbase.mapping.mapper;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Mapper for Boolean
 * @author Guobiao Mo
 *
 */
public class BooleanMapper extends Mapper<Boolean> {
	public static BooleanMapper instance = new BooleanMapper();

	@Override
	public Boolean fromBytes(byte[] bytes, int offset, int length) {
		return ArrayUtils.isEmpty(bytes) ? null : Bytes.toBoolean(Arrays.copyOfRange(bytes, offset, Bytes.SIZEOF_BOOLEAN));
	}

}
