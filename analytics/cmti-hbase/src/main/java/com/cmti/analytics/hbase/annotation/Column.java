package com.cmti.analytics.hbase.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author Guobiao Mo
 *
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Column {
	String value();//Hbase column name
	String cf() default "";
	boolean readVersion() default false;
}