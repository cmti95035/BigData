package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;

import com.cmti.analytics.hbase.util.HBaseUtil;

/**
 * Mapper for String[]
 * @author Guobiao Mo
 *
 */
public class StringArrayMapper extends Mapper<String[]> {//TODO we should create a ArrayMapper, StringArrayMapper, LongArrayMapper, etc subclass from it

//	private char delimiter;
	private ByteFormatter joinerAndSplitter;
	private Mapper<String> memberMapper;

	public StringArrayMapper() {
		this(HBaseUtil.DELIMITER);
	}

	public StringArrayMapper(char delimiter) {
	//	this.delimiter = delimiter;
		joinerAndSplitter = new ByteFormatter(delimiter);
		memberMapper = StringMapper.instance;
	}

	@Override
	public String[] fromBytes(byte[] bytes, int offset, int length) {
		if (ArrayUtils.isEmpty(bytes)) {
			return null;
		}

		byte[] copy = new byte[length];
		System.arraycopy(bytes, offset, copy, 0, length);

		byte[][] memberBytes = joinerAndSplitter.parse(copy);
		String[] ret = new String[memberBytes.length];
		int counter = 0;
		for (byte[] b : memberBytes) {
			ret[counter] = memberMapper.fromBytes(b);
			counter++;
		}
		return ret;
	}

	@Override
	public byte[] toBytes(String[] t) {
		return t == null || t.length == 0 ? null : joinerAndSplitter
				.format(getMemberByteArray(t));
	}

	private byte[][] getMemberByteArray(String[] list) {
		byte[][] ret = new byte[list.length][];
		for (int i = 0; i < list.length; i++) {
			ret[i] = memberMapper.toBytes(list[i]);
		}
		return ret;
	}

}