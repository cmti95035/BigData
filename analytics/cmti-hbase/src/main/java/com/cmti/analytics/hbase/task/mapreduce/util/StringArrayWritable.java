package com.cmti.analytics.hbase.task.mapreduce.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
/**
 * String[] to be passed among MapReduce tasks.
 * @author Guobiao Mo
 *
 */
public class StringArrayWritable extends ArrayWritable {
    public StringArrayWritable() {
            super(Text.class);
    }

    public StringArrayWritable(Text[] values) {
        super(Text.class, values);
    }

    public StringArrayWritable(Object... values) {
        super(Text.class, toTextArray(values));
    }
/*
    public StringArrayWritable(Collection<String> values) {
        super(Text.class, toTextArray(values));
    }

    public StringArrayWritable(String value) {
        this(new String[]{value});
    }

    public StringArrayWritable(int value) {
        this(Integer.toString(value));
    }
*/
    public void set(Collection<? extends Object> values) {
        super.set(toTextArray(values));
    }

    public void set(Object... values) {
        super.set(toTextArray(values));
    }

    //public void set(String value) {
  //      this.set(new String[]{value});
//    }
/*
    public void set(int value) {
        this.set(Integer.toString(value));
    }
*/

    public Set<String> toSet() {
		Set<String> ret = new HashSet<String>(); 
		String[] value =  toStrings();
		for(String v : value) {
			ret.add(v);
		}
	    
		return ret;    
    }

    public int toInt(){
		String[] value = super.toStrings();
    	return Integer.parseInt(value[0]);
    }
  
    private static Text[] toTextArray(Collection<? extends Object> values) {
    	Text[] ret = new  Text[values.size()];
    	int i=0;
    	
    	for (Object value : values) {
    		ret[i++] = toText(value);
    	}
    	return ret;
    }

    private static Text[] toTextArray(Object... values) {
    	Text[] ret = new  Text[values.length];
    	int i=0;
    	
    	for (Object value : values) {
    		ret[i++] = toText(value);
    	}
    	return ret;
    }

    private static Text toText(Object value) {
    	if(value==null){
    		return new Text("");
    	}else if(value instanceof Date){
    		return new Text(Long.toString(((Date)value).getTime()));    		
    	}else{
    		return new Text(value.toString());
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
            if (obj instanceof StringArrayWritable) {
                    StringArrayWritable comparison = (StringArrayWritable) obj;
                    
                    if (comparison.get().length == get().length) {
                            Writable[] thisWritables = get();
                            Writable[] comparisonWritables = get();
                            
                            for (int i = 0; i < comparisonWritables.length; i++) {
                                    if (!comparisonWritables[i].equals(thisWritables[i])) {
                                            return false;
                                    }
                            }
                            
                            return true;
                    }
            }
            
            return false;
    }
    
    @Override
    public String toString() {
            if (get().length != 0) {
                    StringBuilder builder = new StringBuilder();
                    
                    for (int i = 0; i < get().length; i++) {
                    	if(i!=0){
                    		builder.append(",");
                    	}
                        builder.append(get()[i]==null?"":get()[i]);
                    }
                    
                    return builder.toString();
            } else {
                    return "";
            }
    }
}