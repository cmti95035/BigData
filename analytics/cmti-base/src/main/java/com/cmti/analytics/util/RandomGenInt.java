package com.cmti.analytics.util;

import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomGenInt {
    //public Random numGen =new Random();

    public RandomDataGenerator numGen =new RandomDataGenerator();
    private int low, up;

    public RandomGenInt(int low, int up){//both end inclusive,
    	this.low=low;
    	this.up=up;
    }

    public RandomGenInt(int up){//both end inclusive,
    	this.low=0;
    	this.up=up;
    }
    
    public int next(){
        return numGen.nextInt(low, up);    	
    }



public static void main(String[] args){
	RandomGenInt r = new RandomGenInt(10, 13);
	
   System.out.println(r.next());
}

}