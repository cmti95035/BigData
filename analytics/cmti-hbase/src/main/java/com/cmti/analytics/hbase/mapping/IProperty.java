package com.cmti.analytics.hbase.mapping;

/**
 * This class represents a property.
 * @author Guobiao Mo
 * @param <T> target class type
 */
public interface IProperty<T> { 

	/**
	 * Get HBase column value (as byte[]) of this property from the input object.
	 * @param t input object
	 */
	byte[] getValueBytes(T t);

	/**
	 * Get HBase column value (as object)of this property from the input object.
	 */
	Object getValueObject(T t);

	/**
	 * Given input HBase value, set info in the target object.
	 * @param t target object
	 * @param value HBase column value
	 */
	void populate(T t, byte[] value);
	void populate(T t, Object value);
}