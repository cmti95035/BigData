package com.cmti.analytics.hbase.mapping.mapper;

/**
 * Mapper for ArrayList<Integer>
 * @author Guobiao Mo
 *
 */
public class IntegerListMapper extends ListMapper<Integer> {

	@Override
	protected Mapper<Integer> getMemberMapper() {
		return IntegerMapper.instance;
	}
 
}

