package com.cmti.analytics.hbase.mapping.mapper;

import java.lang.reflect.Field;
import java.util.*;

import com.google.inject.TypeLiteral;
import com.cmti.analytics.hbase.util.ReflectUtil;
/**
 * Factory class that provides a Mapper for a Field.
 * @author Guobiao Mo
 */
public class MapperFactory {	
	private static Map<TypeLiteral<?>, Mapper<?>> mappers;//for a Field, TypeLiteral provides more info than class
	
	static{
		mappers = new HashMap<TypeLiteral<?>, Mapper<?>>();
		addDefaultMappers();		
	}
 	
	public static Mapper<?> getMapper(Field field) {
		Mapper<?> bc = getCustomMapper(field);
		if(bc != null){
			return bc;
		}
		
		TypeLiteral<?> typeLiteral = TypeLiteral.get(field.getGenericType());
		return mappers.get(typeLiteral);
	}

/*example, EventMapping.java:
	@CompositeKey(order=1, delimitered=true)
	@com.bn.analytics.hbase.annotation.Mapper(clazz=com.bn.analytics.hbase.mapping.mapper.ReverseDateMapper.class)
	public Date eventDateKey;
	*/
	private static Mapper<?> getCustomMapper(Field field) {
		com.cmti.analytics.hbase.annotation.Mapper customType = field.getAnnotation(com.cmti.analytics.hbase.annotation.Mapper.class);
		if(customType == null) {
			return null;
		}
		if(customType.clazz() == null) {
			throw new RuntimeException("Converter is not defined in custom type for property " + field.getName());
		}

		return ReflectUtil.newInstance(customType.clazz());
	}

	private static void addDefaultMappers() {
		addMapper(new TypeLiteral<Long>(){;}, LongMapper.instance);
		addMapper(new TypeLiteral<Long[]>(){;}, new LongArrayMapper());
		addMapper(new TypeLiteral<ArrayList<Long>>(){;}, new LongListMapper());
		
		addMapper(new TypeLiteral<Integer>(){;}, IntegerMapper.instance);
		addMapper(new TypeLiteral<Integer[]>(){;}, new IntegerArrayMapper());
		addMapper(new TypeLiteral<ArrayList<Integer>>(){;}, new IntegerListMapper());
		
		addMapper(new TypeLiteral<Double>(){;}, DoubleMapper.instance);
		addMapper(new TypeLiteral<Double[]>(){;}, new DoubleArrayMapper());
		addMapper(new TypeLiteral<ArrayList<Double>>(){;}, new DoubleListMapper());
		
		addMapper(new TypeLiteral<Float>(){;}, FloatMapper.instance);
		addMapper(new TypeLiteral<Float[]>(){;}, new FloatArrayMapper());		
		addMapper(new TypeLiteral<ArrayList<Float>>(){;}, new FloatListMapper());
		
		addMapper(new TypeLiteral<String>(){;}, StringMapper.instance);
		addMapper(new TypeLiteral<String[]>(){;}, new StringArrayMapper());
		addMapper(new TypeLiteral<ArrayList<String>>(){;}, new StringListMapper());

		addMapper(new TypeLiteral<Boolean>(){;}, BooleanMapper.instance);
		addMapper(new TypeLiteral<Date>(){;}, DateMapper.instance);
		addMapper(new TypeLiteral<byte[]>(){;}, ByteArrayMapper.instance);
	}

	private static <T> void addMapper(TypeLiteral<T> targetType, Mapper<T> mapper) {
		if(mapper == null || targetType == null) {
			throw new IllegalArgumentException("targetType="+targetType+" mapper="+ mapper);
		}
		mappers.put(targetType, mapper);
	}

}
