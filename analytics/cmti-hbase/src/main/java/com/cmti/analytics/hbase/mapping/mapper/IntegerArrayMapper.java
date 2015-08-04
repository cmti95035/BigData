package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;

import com.cmti.analytics.hbase.util.HBaseUtil;

/**
 * Mapper for Integer[]
 * @author Guobiao Mo
 *
 */
public class IntegerArrayMapper extends Mapper<Integer[]> {//TODO we should create a ArrayMapper, StringArrayMapper, LongArrayMapper, etc subclass from it

//	private char delimiter;
	private ByteFormatter joinerAndSplitter;
	private Mapper<Integer> memberMapper;

	public IntegerArrayMapper() {
		this(HBaseUtil.DELIMITER);
	}

	public IntegerArrayMapper(char delimiter) {
	//	this.delimiter = delimiter;
		joinerAndSplitter = new ByteFormatter(delimiter);
		memberMapper = IntegerMapper.instance;
	}

	@Override
	public Integer[] fromBytes(byte[] bytes, int offset, int length) {
		if (ArrayUtils.isEmpty(bytes)) {
			return null;
		}

		byte[] copy = new byte[length];
		System.arraycopy(bytes, offset, copy, 0, length);

		byte[][] memberBytes = joinerAndSplitter.parse(copy);
		Integer[] ret = new Integer[memberBytes.length];
		int counter = 0;
		for (byte[] b : memberBytes) {
			ret[counter] = memberMapper.fromBytes(b);
			counter++;
		}
		return ret;
	}

	@Override
	public byte[] toBytes(Integer[] t) {
		return t == null || t.length == 0 ? null : joinerAndSplitter
				.format(getMemberByteArray(t));
	}

	private byte[][] getMemberByteArray(Integer[] list) {
		byte[][] ret = new byte[list.length][];
		for (int i = 0; i < list.length; i++) {
			ret[i] = memberMapper.toBytes(list[i]);
		}
		return ret;
	}

}
