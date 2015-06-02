package com.cmti.analytics.hbase.util;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
 

/**
 * Utility that builds HBase Filter
 * @author Guobiao Mo
 *
 */
public class FilterBuilder {
	public static Filter buildSubstring(String family, String column, String subString){
		if(StringUtils.isBlank(subString)) {
			return null;
		}
		
		SubstringComparator comp = new SubstringComparator(subString);
		SingleColumnValueFilter filter = new SingleColumnValueFilter(
			Bytes.toBytes(family),
			Bytes.toBytes(column),
			CompareOp.EQUAL,
			comp
		);

		filter.setFilterIfMissing(true);
		
		return filter;
	}

	public static Filter buildRegex(String family, String column, String regex){
		if(StringUtils.isBlank(regex)) {
			return null;
		}
		
		RegexStringComparator comp = new RegexStringComparator(regex);
		SingleColumnValueFilter filter = new SingleColumnValueFilter(
			Bytes.toBytes(family),
			Bytes.toBytes(column),
			CompareOp.EQUAL,
			comp
		);

		filter.setFilterIfMissing(true);
		
		return filter;
	}
	
	public static FilterList buildIn(FamilyColumn fc, Object... set) {
		return buildIn(fc.getFamilyBytes(), fc.getColumnBytes(), set);
	}

	public static FilterList buildIn(byte[] family, byte[] column, Object... set) {
		return buildIn(family, column, Arrays.asList(set));
	}

	public static FilterList buildIn(String family, String column, Object... set) {
		return buildIn(Bytes.toBytes(family), Bytes.toBytes(column), set);
	}

	public static FilterList buildIn(byte[] family, byte[] column, Collection<? extends Object> set) {
		if(set==null || set.size()==0){
			return null;
		}
		
		FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);

		for(Object obj : set) {
			SingleColumnValueFilter filter = new SingleColumnValueFilter(
					family,
				column,
				CompareOp.EQUAL,
				ByteUtil.toBytes(obj));
			filter.setFilterIfMissing(true);
			filterList.addFilter(filter);					
		}
		return filterList;
	}

	public static Filter buildBetween(FamilyColumn fc, Object value0, Object value1) {
		return buildBetween(fc.getFamilyBytes(), fc.getColumnBytes(), value0, value1);
	}

	public static Filter buildBetween(String family, String column, Object value0, Object value1) {
		return buildBetween(Bytes.toBytes(family), Bytes.toBytes(column), value0, value1);
	}

	//value0 <= target < value1
	public static Filter buildBetween(byte[] family, byte[] column, Object value0, Object value1) {
		SingleColumnValueFilter filter0 = null;
		SingleColumnValueFilter filter1 = null;

		if (value0 != null) {
			filter0 = new SingleColumnValueFilter(
					family,
					column, 
					CompareOp.GREATER_OR_EQUAL,
					ByteUtil.toBytes(value0));
			filter0.setFilterIfMissing(true);
		}
		
		if (value1 != null) {
				filter1 = new SingleColumnValueFilter(
						family,
						column, 
						CompareOp.LESS,
						ByteUtil.toBytes(value1));
				filter1.setFilterIfMissing(true);
		}
		
		if(filter0 == null & filter1 == null){
			return null;
		}else if(filter0 == null){
			return filter1;
		}else if(filter1 == null){
			return filter0;
		}else{
			FilterList ret = new FilterList(FilterList.Operator.MUST_PASS_ALL);
			ret.addFilter(filter0);
			ret.addFilter(filter1);
			
			return ret;
		}
	}
}