package com.cmti.analytics.hbase.mapping;

/**
 * 
 * This class represents a HBase key.
 * @author Guobiao Mo
 *
 * @param <T>
 * @param <P>
 */
public interface IKey<T, P> extends IProperty<T>{
	/**
	 * Generate HBase key from the input object.
	 * @param t input object
	 */
	byte[] getKey(T t);

	/**
	 * Given the key, convert the key to bytes representation.
	 * @param key
	 */
	byte[] keyToBytes(P key);


	/**
	 * Given row bytes, convert the row to the key object.
	 * @param bytes
	 */
	Object bytesToKey(byte[] row);
	
}