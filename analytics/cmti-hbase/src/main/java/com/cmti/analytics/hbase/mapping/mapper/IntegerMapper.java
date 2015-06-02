package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;
/**
 * Mapper for Integer		
 * @author Guobiao Mo
 *
 */
public class IntegerMapper extends Mapper<Integer> {
		public static IntegerMapper instance = new IntegerMapper();//thread safe

		@Override
		public Integer fromBytes(byte[] bytes, int offset, int length) {
			return ArrayUtils.isEmpty(bytes) ? null : Bytes.toInt(bytes, offset, length);
		}

	}
