package com.cmti.analytics.hbase.mapping.mapper;

/**
 * Mapper for ArrayList<String>
 * @author Guobiao Mo
 *
 */
public class StringListMapper extends ListMapper<String> {

	@Override
	protected Mapper<String> getMemberMapper() {
		return StringMapper.instance;
	}
 
}

