package com.cmti.analytics.hbase.mapping.mapper;

import java.util.Arrays;

/**
 * Mapper for byte[]
 * @author Guobiao Mo
 *
 */
public class ByteArrayMapper extends Mapper<byte[]> {
	public static ByteArrayMapper instance = new ByteArrayMapper();

	@Override
	public byte[] fromBytes(byte[] bytes) {
		return bytes;
	}

	@Override
	public byte[] fromBytes(byte[] bytes, int offset, int length) {
		return bytes == null ? null : Arrays.copyOfRange(bytes, offset, length);
	}

	@Override
	public byte[] toBytes(byte[] t) {
		return t;
	}


}
