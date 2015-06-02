package com.cmti.analytics.hbase.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * To define a custom mapper for a column
 * @author Guobiao Mo
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Mapper {
	Class<? extends com.cmti.analytics.hbase.mapping.mapper.Mapper<?>> clazz();
}