package com.cmti.analytics.app.station.task.storm.sig;

import java.util.*;

import redis.clients.jedis.Jedis;


public class RedisReader { 
	
	public RedisReader(){ 
	}	 
	
    public static void main(String[] args) { 
    	      //Connecting to Redis server on localhost
    	      Jedis jedis = new Jedis("quickstart.cloudera");
    	      System.out.println("Connection to server sucessfully");
    	      //store data in redis list
    	     // Get the stored data and print it
     	     Set<String> keys = jedis.keys("*");
 //   	     Set<String> keys = jedis.keys("c~8~*~10");
    	     for(String key:keys) {
 //  	    	jedis.del(key);//.c.set(key, null);
    	    	 
    		    String c = jedis.get(key);
    		    System.err.println("key="+key+" count="+c);
    	     }
    	   
    }
}