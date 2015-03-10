package com.cmti.analytics.util;

import java.util.List;

/**
 * Utility class for objects and beans.
 * @author gmo
 *
 */
public class ObjectUtil {

	public static boolean equals(Object object1, Object object2) {
		if (object1 == object2) {
			return true;
		}
		
		if ((object1 == null) || (object2 == null)) {
			return false;
		}
		
		if (object1 instanceof List) {//TODO implement for Set, Map etc.
			if (object1 instanceof List) {
				return equalLists((List)object1, (List)object2);
			}else{
				return false;
			}
		}
		
		return object1.equals(object2);
	}
	
	public static boolean equalLists(List<? extends Object> object1, List<? extends Object> object2){    
		if (object1 == object2) {
			return true;
		}
		
		if ((object1 == null) || (object2 == null)) {
			return false;
		}
		
	    if( object1.size() != object2.size()){
	        return false;
	    }

	    for(int i=0; i<object1.size(); i++) {
	    	boolean same = equals(object1.get(i), object2.get(i));
	    	if(same==false){
	    		return false;
	    	}
	    }

	    return true;
	}
}