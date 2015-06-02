package com.cmti.analytics.hbase.mapping;

import java.lang.reflect.Field;

import com.cmti.analytics.hbase.dao.HBaseObject;

/**
 * Key based on single property.
 * @author Guobiao Mo
 * @param <T> domain object
 * @param <P> Key type
 */
public class SingleKey<T extends HBaseObject, P> extends SingleProperty<T, P> implements IKey<T, P> {
	
	public SingleKey(Field field) {
		super(field, true);
	}

	@Override
	public byte[] getKey(T t) {
		return getValueBytes(t);
	}

	/**
	 * Given the key, convert the key to bytes representation.
	 */
	@Override
	public byte[] keyToBytes(P key) {
		return mapper.toBytes(key);
	}

	/**
	 * Given row bytes, convert the row to the key object.
	 */
	@Override
	public P bytesToKey(byte[] row) {
		return mapper.fromBytes(row);
	}

}
