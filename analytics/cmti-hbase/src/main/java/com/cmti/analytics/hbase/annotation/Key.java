package com.cmti.analytics.hbase.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines the key of a class/table.
 * @author Guobiao Mo
 *
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Key {
}
