package com.cmti.analytics.util;

import java.util.Date;
import java.util.List;

/**
 * Utility class for objects and beans.
 * @author gmo
 *
 */
public class CompareUtil {

	/**
	return positive if date1>date2
	return 0 if date1==date2
	return negative if date1<date2
	null treated as time at 0
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compare(Date date1, Date date2) {		
		if (date1 == null) {
			date1 = new Date(0L);
		}

		if(date2==null){
			date2 = new Date(0L);
		}
		
		return date1.compareTo(date2);		
	}
/**
 * 
	null treated as Integer.MIN_VALUE
 * @param int1
 * @param int2
 * @return
 */
	
	public static int compare(Integer int1, Integer int2) {		
		if (int1 == null) {
			if(int2 == null) {
				return 0;
			}else{
				return -1;
			}
		}

		if(int2==null){
			return 1;
		}
		
		if(int1 > int2){
			return 1;
		}else if(int1 < int2){
			return -1;
		}else{
			return 0;
		}
	}

	public static int compare(Long int1, Long int2) {	
		if (int1 == null) {
			if(int2 == null) {
				return 0;
			}else{
				return -1;
			}
		}

		if(int2==null){
			return 1;
		}
		
		if(int1 > int2){
			return 1;
		}else if(int1 < int2){
			return -1;
		}else{
			return 0;
		}
	}

	public static int compare(Double int1, Double int2) {
		if (int1 == null) {
			if(int2 == null) {
				return 0;
			}else{
				return -1;
			}
		}

		if(int2==null){
			return 1;
		}
		
		if(int1 > int2){
			return 1;
		}else if(int1 < int2){
			return -1;
		}else{
			return 0;
		}
	}

}