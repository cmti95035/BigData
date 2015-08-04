package com.cmti.analytics.hbase.mapping.mapper;

/**
 * Mapper for ArrayList<Long>
 * @author Guobiao Mo
 *
 */
public class LongListMapper extends ListMapper<Long> {

	@Override
	protected Mapper<Long> getMemberMapper() {
		return LongMapper.instance;
	}
 
}

