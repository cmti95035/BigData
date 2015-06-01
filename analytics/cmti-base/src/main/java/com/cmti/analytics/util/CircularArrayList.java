package com.cmti.analytics.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import com.google.common.collect.Lists;

/**
 *  
 * @author Guobiao Mo
 *
 */
public class CircularArrayList<E> extends ArrayList<E>
{
    private static final long serialVersionUID = 1L;

    public CircularArrayList(Collection<? extends E> c){
    	super(c);
    }
    
    public E get(int index)
    {
    	index = index%size();
		
		if (index<0)
			index = index+size();
        return super.get(index);
    }
    /*
	public static void main(final String[] args) {
		int len = 9;
		int id= -12;
		
		id = id%len;
		
		if (id<0)
			id = id+len;
		System.out.println(id );
		
		List<String> list = Lists.newArrayList("A", "B", "C");
		final CircularArrayList<String> circular = new CircularArrayList<String>(list);
		
		for (int i = -10; i < 8; i++) {
			System.out.println(i + circular.get(i));
		}
	}*/
}