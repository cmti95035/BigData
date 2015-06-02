package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;
/**
 * Mapper for Float
 * @author Guobiao Mo
 *
 */
public class FloatMapper extends Mapper<Float> {
	public static FloatMapper instance = new FloatMapper();

	@Override
	public Float fromBytes(byte[] bytes, int offset, int length) {
		return ArrayUtils.isEmpty(bytes) ? null : Bytes.toFloat(bytes, offset);
	}

}
