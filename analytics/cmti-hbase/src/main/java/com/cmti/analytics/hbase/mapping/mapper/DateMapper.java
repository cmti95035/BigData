package com.cmti.analytics.hbase.mapping.mapper;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Mapper for Date
 * @author Guobiao Mo
 *
 */
public class DateMapper extends Mapper<Date> {
	public static DateMapper instance = new DateMapper();

	@Override
	public Date fromBytes(byte[] bytes, int offset, int length) {
		return ArrayUtils.isEmpty(bytes) ? null : new Date(Bytes.toLong(bytes, offset, length));
	}

}
