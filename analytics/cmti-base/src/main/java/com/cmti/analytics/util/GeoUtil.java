package com.cmti.analytics.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 


public class GeoUtil {

	protected final static Logger logger = LogManager.getLogger(GeoUtil.class);
//from http://www.geodatasource.com/developers/java
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::                                                                         :*/
    /*::  This routine calculates the distance between two points (given the     :*/
    /*::  latitude/longitude of those points). It is being used to calculate     :*/
    /*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
    /*::                                                                         :*/
    /*::  Definitions:                                                           :*/
    /*::    South latitudes are negative, east longitudes are positive           :*/
    /*::                                                                         :*/
    /*::  Passed to function:                                                    :*/
    /*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
    /*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
    /*::    unit = the unit you desire for results                               :*/
    /*::           where: 'M' is statute miles (default)                         :*/
    /*::                  'K' is kilometers                                      :*/
    /*::                  'N' is nautical miles                                  :*/
    /*::  Worldwide cities and other features databases with latitude longitude  :*/
    /*::  are available at http://www.geodatasource.com                          :*/
    /*::                                                                         :*/
    /*::  For enquiries, please contact sales@geodatasource.com                  :*/
    /*::                                                                         :*/
    /*::  Official Web site: http://www.geodatasource.com                        :*/
    /*::                                                                         :*/
    /*::           GeoDataSource.com (C) All Rights Reserved 2015                :*/
    /*::                                                                         :*/
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	public enum Unit {MILE, KM}
	
    public static double distance(double lat1, double lon1, double lat2, double lon2, Unit unit) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      //dist can be 1.0000000000000002 and Math.acos(dist)=NaN, so need to do this:
      if(dist>1.){
    	  dist=1.;
      }
      dist = Math.acos(dist);
      dist = rad2deg(dist) * 60.;
      
      return unit==Unit.MILE?dist * 1.1515:dist * 1.85316;//number from http://en.wikipedia.org/wiki/Nautical_mile not the same as http://www.geodatasource.com/developers/java
      /*
      dist = dist * 60 * 1.1515;//statute miles (default)   
      if (unit == 'K') {//kilometers
        dist = dist * 1.609344;
      } else if (unit == 'N') {// nautical miles     
      	dist = dist * 0.8684;
      }
      return dist;*/
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) { 
      return distance(lat1, lon1, lat2, lon2, Unit.KM);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
      return deg * Math.PI / 180.0;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
      return rad * 180 / Math.PI;
    }
    
    public static double distance(List<Double> latitudeList, List<Double> longitudeList) {
    	double ret =0.;
    	for(int i=0; i<latitudeList.size()-1; i++) {
    		logger.error("{} {} {} {} {}", ret , latitudeList.get(i), longitudeList.get(i), latitudeList.get(i+1), longitudeList.get(i+1));
    		if(30.649797==latitudeList.get(i)){
    			int y=0;
    		}
    		ret+= distance(latitudeList.get(i), longitudeList.get(i), latitudeList.get(i+1), longitudeList.get(i+1));
    	}
    	return ret;
    }
    
	
    public static void main(String[] args) throws Exception{
    	System.out.println(distance(38.898556, -77.037852, 38.897147, -77.043934, Unit.MILE) + " Miles\n");
    	System.out.println(distance(38.898556, -77.037852, 38.897147, -77.043934, Unit.KM) + " Kilometers\n");
    	
    	System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, Unit.MILE) + " Miles\n");
    	System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, Unit.KM) + " Kilometers\n");
//    	System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'N') + " Nautical Miles\n");    	

    	System.out.println(distance(30.626354, 104.144173, 30.626497, 104.144241, Unit.KM) + " Kilometers\n");
    	System.out.println(distance(30.626354, 104.144173, 30.626497, 104.144241) + " Kilometers\n");
    	System.out.println(distance(30.626354, 104.144173, 30.626354, 104.144241) + " Kilometers\n");
    	System.out.println(distance(30.626354, 104.144173, 30.626354, 104.144173) + " Kilometers\n");

    	Double[] latitudeList = new Double[]{30.660627, 30.660473, 30.660318, 30.660164, 30.660009, 30.659855, 30.659704, 30.65955, 30.659395, 30.659237, 30.659079, 30.658918, 30.658918, 30.658756, 30.658426, 30.658756, 30.65859, 30.658426, 30.65826, 30.658089, 30.657919, 30.657751, 30.657576, 30.657402, 30.657227, 30.657047, 30.656868, 30.656694, 30.656515, 30.656694, 30.656694, 30.656515, 30.65634, 30.656168, 30.65601, 30.655872, 30.655745, 30.655628, 30.655514, 30.655396, 30.655273, 30.655144, 30.655012, 30.655144, 30.655012, 30.654875, 30.654732, 30.654585, 30.65444, 30.654289, 30.654289, 30.654146, 30.653996, 30.653849, 30.653698, 30.653543, 30.653389, 30.653543, 30.653389, 30.653231, 30.653072, 30.652914, 30.65275, 30.652588, 30.652426, 30.652262, 30.6521, 30.651937, 30.6521, 30.651937, 30.651773, 30.651611, 30.651611, 30.651453, 30.651289, 30.651131, 30.650969, 30.650801, 30.650635, 30.650463, 30.650129, 30.650463, 30.650297, 30.650129, 30.64996, 30.649797, 30.649797, 30.649637, 30.649483, 30.649328, 30.64917, 30.649015, 30.648865, 30.648718, 30.648571, 30.648718, 30.648571, 30.648434};
    	Double[] longitudeList = new Double[]{103.982018, 103.982002, 103.981979, 103.981949, 103.981934, 103.981918, 103.981888, 103.981865, 103.981834, 103.981819, 103.981804, 103.981773, 103.981773, 103.981758, 103.981705, 103.981758, 103.98172, 103.981705, 103.981689, 103.981659, 103.981644, 103.981606, 103.98159, 103.98156, 103.981544, 103.981529, 103.981514, 103.981491, 103.981491, 103.981491, 103.981491, 103.981491, 103.981476, 103.981476, 103.981476, 103.981491, 103.981491, 103.981514, 103.981514, 103.981529, 103.981544, 103.98156, 103.981575, 103.98156, 103.981575, 103.98159, 103.981621, 103.981659, 103.981689, 103.98172, 103.98172, 103.981773, 103.981804, 103.98185, 103.981903, 103.981949, 103.982018, 103.981949, 103.982018, 103.982063, 103.982132, 103.982208, 103.982277, 103.982376, 103.982452, 103.982536, 103.982635, 103.982735, 103.982635, 103.982735, 103.982841, 103.982941, 103.982941, 103.983055, 103.98317, 103.983284, 103.983414, 103.983528, 103.983643, 103.983772, 103.984016, 103.983772, 103.983902, 103.984016, 103.984131, 103.984261, 103.984261, 103.984375, 103.984489, 103.984604, 103.984718, 103.984833, 103.984932, 103.985039, 103.985138, 103.985039, 103.985138, 103.985252};
    	double dis=distance(Arrays.asList(latitudeList), Arrays.asList(longitudeList)); 
    	System.out.println(dis);
    }
}