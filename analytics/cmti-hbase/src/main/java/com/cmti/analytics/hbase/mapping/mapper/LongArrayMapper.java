package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;

import com.cmti.analytics.hbase.util.HBaseUtil;

/**
 * Mapper for Long[]
 * @author Guobiao Mo
 *
 */
public class LongArrayMapper extends Mapper<Long[]> {//TODO we should create a ArrayMapper, StringArrayMapper, LongArrayMapper, etc subclass from it

//	private char delimiter;
	private ByteFormatter joinerAndSplitter;
	private Mapper<Long> memberMapper;

	public LongArrayMapper() {
		this(HBaseUtil.DELIMITER);
	}

	public LongArrayMapper(char delimiter) {
	//	this.delimiter = delimiter;
		joinerAndSplitter = new ByteFormatter(delimiter);
		memberMapper = LongMapper.instance;
	}

	@Override
	public Long[] fromBytes(byte[] bytes, int offset, int length) {
		if (ArrayUtils.isEmpty(bytes)) {
			return null;
		}

		byte[] copy = new byte[length];
		System.arraycopy(bytes, offset, copy, 0, length);

		byte[][] memberBytes = joinerAndSplitter.parse(copy);
		Long[] ret = new Long[memberBytes.length];
		int counter = 0;
		for (byte[] b : memberBytes) {
			ret[counter] = memberMapper.fromBytes(b);
			counter++;
		}
		return ret;
	}

	@Override
	public byte[] toBytes(Long[] t) {
		return t == null || t.length == 0 ? null : joinerAndSplitter
				.format(getMemberByteArray(t));
	}

	private byte[][] getMemberByteArray(Long[] list) {
		byte[][] ret = new byte[list.length][];
		for (int i = 0; i < list.length; i++) {
			ret[i] = memberMapper.toBytes(list[i]);
		}
		return ret;
	}

}
