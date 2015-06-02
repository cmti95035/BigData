package com.cmti.analytics.hbase.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define table parameters
 * @author Guobiao Mo
 *
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Table {
	String name();
	String defaultCf() default "d";
	boolean readVersion() default false;
	boolean hasUnmapped() default false;
}
