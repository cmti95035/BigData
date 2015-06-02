package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;
/**
 * Mapper for Double
 * @author Guobiao Mo
 *
 */
public class DoubleMapper extends Mapper<Double> {
	public static DoubleMapper instance = new DoubleMapper();

	@Override
	public Double fromBytes(byte[] bytes, int offset, int length) {
		return ArrayUtils.isEmpty(bytes) ? null : Bytes.toDouble(bytes, offset);
	}

}
