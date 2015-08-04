package com.cmti.analytics.hbase.mapping.mapper;

/**
 * Mapper for ArrayList<Float>
 * @author Guobiao Mo
 *
 */
public class FloatListMapper extends ListMapper<Float> {

	@Override
	protected Mapper<Float> getMemberMapper() {
		return FloatMapper.instance;
	}
 
}

