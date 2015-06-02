package com.cmti.analytics.hbase.mapping.mapper;

/**
 * Mapper for ArrayList<Double>
 * @author Guobiao Mo
 *
 */
public class DoubleListMapper extends ListMapper<Double> {

	@Override
	protected Mapper<Double> getMemberMapper() {
		return DoubleMapper.instance;
	}
 
}

