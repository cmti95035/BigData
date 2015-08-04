package com.cmti.analytics.hbase.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Annotation Utility
 * @author Guobiao Mo
 *
 */
public class AnnotationUtil {

/*
@Table("student")
public class StudentMapping extends HBaseObject{...}
 
This method returns "student":
ReflectionUtils.getAnnotationValue(Student.class,  Table.class);
It looks into all super classes until it finds it. 
*/

	public final static <A extends Annotation> Object getClassAnnotationValue(Class<?> targetClass, Class<A> annotationClass) {
		return getClassAnnotationValue(targetClass, annotationClass, null);
	}

	//example: tableName = (String) ReflectionUtils.getClassAnnotationValue(targetClass, Table.class, "name"); 
	public final static <A extends Annotation> Object getClassAnnotationValue(Class<?> targetClass, Class<A> annotationClass, String name) {
		if(name==null){
			name = "value";
		}
		Class<?> superClass = targetClass;
		do{
			A annotation = superClass.getAnnotation(annotationClass);
			if(annotation!=null) {
				return ReflectUtil.invokeMethod(name, annotation);
//				return invokeMethod(name, annotationClass, annotation);
			}
			superClass = superClass.getSuperclass();
		}while(superClass!=null);
		
		return null;
	}
	
/*
 * get all fields with annotation 'annotationClass'
 * 	
	For example, in Student.java, getAnnotationFields(Student.class, Column.class) returns all fields with @Column like:
		
	@Column(name = "rank")
	public Long rank;	
 */
	public final static <A extends Annotation> List<Field> getAnnotationFields(Class<?> targetClass, Class<A> annotationClass) {
		List<Field> ret = new ArrayList<>();
		Class<?> superClass = targetClass;
		do{
			Field[] fields = superClass.getDeclaredFields();
			for(Field field : fields) {
				A colAnnotation = field.getAnnotation(annotationClass);
				
				if(colAnnotation != null){
					ret.add(field);				
				}			
			}
			
			superClass = superClass.getSuperclass();
		}while(superClass!=null);
		
		return ret;
	}
	
}