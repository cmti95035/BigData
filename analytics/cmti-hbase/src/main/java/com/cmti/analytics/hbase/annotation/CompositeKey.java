package com.cmti.analytics.hbase.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines the composite key of a class/table.
 * @author Guobiao Mo
 *
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface CompositeKey {
	int order();
	String column() default "";
}