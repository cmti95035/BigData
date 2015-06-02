package com.cmti.analytics.hbase.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection utility
 * @author Guobiao Mo
 *
 */
public class ReflectUtil {
	
	public final static Object getValue(Field field, Object obj) {
		try {
			return field.get(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public final static void setValue(Object t, Field field, Object value) {
		try {
			field.set(t, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public final static Object[] getValues(Field[] fields, Object obj) {
		Object[] ret = new Object[fields.length];
		int i=0;
		for(Field field : fields) {
			ret[i++] = getValue(field, obj);
		}
		return ret;
	}

	public final static void setValues(Object t, Field[] fields, Object[] values) {
		for(int i= 0 ; i<fields.length; i++) {
			setValue(t, fields[i], values[i]);
		}
	}

	public final static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		}catch(Exception e){
			throw new RuntimeException(e);			
		}
	}

	//invoke methodName() on 'object'
	public final static Object invokeMethod(String methodName, Object object) {//TODO method can take args
		try{
			Class<?> clazz = object.getClass();
			Method  method = clazz.getDeclaredMethod (methodName);
			Object ret= method.invoke (object);				
			return ret;
		}catch(Exception e){
			throw new RuntimeException(e);			
		}
	}

}