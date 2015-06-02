package com.cmti.analytics.hbase.mapping;

import com.cmti.analytics.hbase.util.FamilyColumn;

/**
 * This class represents a HBase column.
 * @author Guobiao Mo
 * @param <T> target class type
 */
public interface IColumn<T> extends IProperty<T>{	
	
	/**
	 * tell Dao that we need to access this column's version
	 * @return
	 */
	boolean readVersion();

	String getColumnFamily();
	String getColumn();
	byte[] getColumnFamilyBytes();
	byte[] getColumnBytes();
	/**
	 * return the full hbase column name like "d:event_name"
	 */	 
	String getFullName();

	FamilyColumn getFamilyColumn();
	
	/**
	 * Return true if the column has become dirty from the input object.
	 * @param t
	 */
	boolean isDirty(T t);

}