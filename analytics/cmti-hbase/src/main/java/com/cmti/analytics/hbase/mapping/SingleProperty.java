package com.cmti.analytics.hbase.mapping;

import java.lang.reflect.Field;

import com.cmti.analytics.hbase.mapping.mapper.Mapper;
import com.cmti.analytics.hbase.mapping.mapper.MapperFactory;
import com.cmti.analytics.hbase.util.ReflectUtil;
/**
 * Property with one value
 * @author Guobiao Mo
 *
 * @param <T> Domain object -- like 'Event'
 * @param <P> Key -- like Long for 'UserId'
 */
public class SingleProperty<T, P> implements IProperty<T>{
	protected Field field;
	protected Mapper<P> mapper;
	protected boolean notNull; //for key, the value must be not null

	public SingleProperty(Field field, boolean notNull) {
		this.field = field;
		this.mapper = (Mapper<P>)MapperFactory.getMapper(field);
		this.notNull = notNull;//TODO need notNull?
	}

	/**
	 * get the property value from object t, and convert it to byte[]
	 * @param t
	 * @return
	 */
	@Override
	public byte[] getValueBytes(T t) {
		P propValue = getValueObject(t);
		return mapper.toBytes(propValue);
	}

	@Override
	public P getValueObject(T t) {
		P propValue = (P)ReflectUtil.getValue(field, t);
		if (notNull && propValue == null) {
			throw new RuntimeException("Property " + field.getName() + " is null");
		}

		return propValue;
	}

	/**
	 * convert bytes to object, and set it in t
	 * @param t
	 * @param bytes
	 */
	@Override
	public void populate(T t, byte[] bytes) {
		P value = mapper.fromBytes(bytes);
		if(notNull && value == null) {
			throw new RuntimeException("Property " + field.getName() + " is null");
		}
		
		populate(t, value);	
	}

	@Override
	public void populate(T t, Object value) {
		ReflectUtil.setValue(t, field, value);		
	}
	
	/**
	 * need a method to convert byte[] to value object? 
	 * SingleKey.bytesToKey() does this.
	 */
	
	@Override
	public String toString() {
		return String.format("property = %s, mapper = [%s]",  field.getName(), mapper);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

}