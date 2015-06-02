package com.cmti.analytics.hbase.mapping;

import java.lang.reflect.Field;
import java.util.SortedSet;

import com.cmti.analytics.hbase.dao.HBaseObject;

/**
 * A key that is constructed using multiple property values.
 * @author Guobiao Mo
 *
 * @param <T> domain object
 * @param <P> Key
 */
public class CompoKey<T extends HBaseObject, P> extends CompoProperty<T> implements IKey<T, P> {

	public CompoKey(SortedSet<Field> fields) {
		super(fields, NullAction.BREAK);
	}

	/**
	 * get composite row key in byte[] from object t
	 */
	@Override
	public byte[] getKey(T t) {
		return getValueBytes(t);
	}

/**
 * get composite row key in byte[] from a set of composite keys
 */
	@Override
	public byte[] keyToBytes(P keys) {
		verifyAllNotNull( (Object[])keys);
    	return compositeMapper.toBytes((Object[])keys);
	}

	/**
	 * Given row key in bytes, construct the key objects.
	 * @param bytes
	 */
	@Override
	public Object bytesToKey(byte[] row) {
		return compositeMapper.fromBytes(row);
	}

}