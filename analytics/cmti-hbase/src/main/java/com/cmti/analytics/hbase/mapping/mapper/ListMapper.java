package com.cmti.analytics.hbase.mapping.mapper;

import java.util.ArrayList;

import com.cmti.analytics.hbase.util.HBaseUtil;
/**
 * Mapper for List
 * @author Guobiao Mo
 *
 */
public abstract class ListMapper<T> extends CollectionMapper<T, ArrayList<T>> {

	public ListMapper() {
		this(HBaseUtil.DELIMITER);
	}

	public ListMapper(char delimiter) {
		super(delimiter);
	}

	@Override
	protected ArrayList<T> newCollection(int size) {
		return new ArrayList<T>(size);
	}		
}

