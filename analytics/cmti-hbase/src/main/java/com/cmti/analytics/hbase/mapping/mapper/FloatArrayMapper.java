package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;

import com.cmti.analytics.hbase.util.HBaseUtil;

/**
 * Mapper for Float[]
 * @author Guobiao Mo
 *
 */
public class FloatArrayMapper extends Mapper<Float[]> {//TODO we should create a ArrayMapper, StringArrayMapper, LongArrayMapper, etc subclass from it

//	private char delimiter;
	private ByteFormatter joinerAndSplitter;
	private Mapper<Float> memberMapper;

	public FloatArrayMapper() {
		this(HBaseUtil.DELIMITER);
	}

	public FloatArrayMapper(char delimiter) {
	//	this.delimiter = delimiter;
		joinerAndSplitter = new ByteFormatter(delimiter);
		memberMapper = FloatMapper.instance;
	}

	@Override
	public Float[] fromBytes(byte[] bytes, int offset, int length) {
		if (ArrayUtils.isEmpty(bytes)) {
			return null;
		}

		byte[] copy = new byte[length];
		System.arraycopy(bytes, offset, copy, 0, length);

		byte[][] memberBytes = joinerAndSplitter.parse(copy);
		Float[] ret = new Float[memberBytes.length];
		int counter = 0;
		for (byte[] b : memberBytes) {
			ret[counter] = memberMapper.fromBytes(b);
			counter++;
		}
		return ret;
	}

	@Override
	public byte[] toBytes(Float[] t) {
		return t == null || t.length == 0 ? null : joinerAndSplitter
				.format(getMemberByteArray(t));
	}

	private byte[][] getMemberByteArray(Float[] list) {
		byte[][] ret = new byte[list.length][];
		for (int i = 0; i < list.length; i++) {
			ret[i] = memberMapper.toBytes(list[i]);
		}
		return ret;
	}

}