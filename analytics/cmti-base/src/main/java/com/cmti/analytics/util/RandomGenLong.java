package com.cmti.analytics.util;

import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomGenLong {
    public RandomDataGenerator numGen =new RandomDataGenerator();

    private long low, up;

    public RandomGenLong(long low, long up){// (endpoints included).
    	this.low=low;
    	this.up=up;
    }

    public RandomGenLong(long up){// (endpoints included).
    	this.low=0L;
    	this.up=up;
    }
    
    public long next(){
        return numGen.nextLong(low, up);    	
    }



public static void main(String[] args){
	RandomGenLong r = new RandomGenLong(10L, 13L);
	
   System.out.println(r.next());
}

}