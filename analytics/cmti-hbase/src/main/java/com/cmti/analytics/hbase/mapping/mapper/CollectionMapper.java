package com.cmti.analytics.hbase.mapping.mapper;

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;

import com.cmti.analytics.hbase.util.HBaseUtil;

/**
 * Mapper for Collection
 * @author Guobiao Mo
 *
 * @param <T> element type
 * @param <C> Collection type
 */
public abstract class CollectionMapper<T, C extends Collection<T>> extends Mapper<C> {//TODO combine this with ArrayMapper

//	private char delimiter;
	private ByteFormatter byteFormatter;
	private Mapper<T> memberMapper;

	public CollectionMapper() {
		this(HBaseUtil.DELIMITER);
	}

	public CollectionMapper(char delimiter) {
	//	this.delimiter = delimiter;
		byteFormatter = new ByteFormatter(delimiter);
		memberMapper = getMemberMapper();
	}

	protected abstract Mapper<T> getMemberMapper();

	protected abstract C newCollection(int size);

	@Override
	public C fromBytes(byte[] bytes, int offset, int length) {
		if (ArrayUtils.isEmpty(bytes)) {
			return null;
		}

		byte[] copy = new byte[length];
		System.arraycopy(bytes, offset, copy, 0, length);

		byte[][] memberBytes = byteFormatter.parse(copy);
		C ret = newCollection(memberBytes.length);
		for (byte[] b : memberBytes) {
			ret.add(memberMapper.fromBytes(b));
		}
		return ret;
	}

	@Override
	public byte[] toBytes(C t) {
		return t == null || t.isEmpty() ? null : byteFormatter
				.format(getMemberByteArray(t));
	}

	private byte[][] getMemberByteArray(C collection) {
		byte[][] ret = new byte[collection.size()][];
		int i = 0;
		for(T member : collection) {
			ret[i] = memberMapper.toBytes(member);
			i++;
		}
		return ret;
	}
}