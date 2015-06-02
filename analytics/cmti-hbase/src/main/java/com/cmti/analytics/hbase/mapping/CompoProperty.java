package com.cmti.analytics.hbase.mapping;

import java.lang.reflect.Field;
import java.util.*;

import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.mapping.mapper.CompositeMapper;
import com.cmti.analytics.hbase.mapping.mapper.Mapper;
import com.cmti.analytics.hbase.mapping.mapper.MapperFactory;
import com.cmti.analytics.hbase.util.ReflectUtil;

/**
 * A class to represent a property with composite fields.
 * @author Guobiao Mo
 *
 * @param <T> target object class
 */
public class CompoProperty<T extends HBaseObject>  implements IProperty<T>{

	/** 
	 * Action when any of the component is null 
	 */
	public static enum NullAction {
		/** throw exception */
		BREAK,

		/** return null */
		SKIP,

		/** no action */
		NOOP
	}

	protected Field[] fields;
	protected CompositeMapper compositeMapper;
	protected NullAction nullAction;

	public CompoProperty(SortedSet<Field> fieldSet, NullAction nullAction) {
		int len = fieldSet.size();
		
		fields = new Field[len];
		Mapper<?>[] mappers = new Mapper<?>[len];
		
		int i =0;
		for(Field field : fieldSet) {
			fields[i]=field;
			mappers[i]=MapperFactory.getMapper(field);
			i++;
		}
				
		compositeMapper = new CompositeMapper(mappers) ;

		if(nullAction == null) {
			throw new IllegalArgumentException("Null action is not defined");
		}
		this.nullAction = nullAction;
	}

	@Override
	public byte[] getValueBytes(T t) {
		Object[] values = (Object[])getValueObject(t);
		switch(nullAction) {
			case NOOP: 
				break;
			case BREAK:
				verifyAllNotNull(values); 
				break;
			case SKIP:
				if(indexOfNullValue(values) > -1) {
					return null;
				}
				break;
		}
    	return compositeMapper.toBytes(values);
	}

	@Override
	public Object getValueObject(T t) {
		Object[] values = ReflectUtil.getValues(fields, t);
		return values;
	}
	
	@Override
	public void populate(T t, byte[] bytes) {
		Object[] values = compositeMapper.fromBytes(bytes);
		populate(t, values);
	}

	@Override
	public void populate(T t, Object value) {
		Object[] values;

		if(value instanceof Object[]){
			values = (Object[]) value;
		}else{
			values= new Object[]{value};//device's userId may contain only 1 element
		}
		
		switch(nullAction) {
			case NOOP: 
				break;
			case BREAK:
				verifyAllNotNull(values); 
				break;
			case SKIP:
				if(indexOfNullValue(values) > -1) {
					return;
				}
				break;
		}
		ReflectUtil.setValues(t, fields, values);
	}

	protected static int indexOfNullValue(Object[] values) {
		for(int i = 0; i < values.length; i++) {
			if(values[i] == null) {
				return i;
			}
		}
		return -1;
	}

	protected void verifyAllNotNull(Object[] values) {
		for(int i = 0; i < fields.length; i++) {
			if(values[i] == null) {
				throw new RuntimeException("Property " + fields[i].getName() + " is null");
			}
		}
	}

	@Override
	public String toString() {
		String ret = null;
		for(Field pd : fields) {
			if(ret == null) {
				ret = "propertis = [" + pd.getName();
			} else {
				ret += ", " + pd.getName();
			}
		}
		ret += "], compositeMapper = " + compositeMapper.getClass().getSimpleName() 
				+ "[" + compositeMapper + "]";
		return ret;
	}

}
