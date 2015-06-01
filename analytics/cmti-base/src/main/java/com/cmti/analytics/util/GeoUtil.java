package com.cmti.analytics.util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;


public class GeoUtil {
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

    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;//statute miles (default)   
      if (unit == 'K') {//kilometers
        dist = dist * 1.609344;
      } else if (unit == 'N') {// nautical miles     
      	dist = dist * 0.8684;
      }
      return dist;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) { 
      return distance(lat1, lon1, lat2, lon2, 'K');
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
	
    public static void main(String[] args) throws Exception{
    	System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'M') + " Miles\n");
    	System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'K') + " Kilometers\n");
    	System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'N') + " Nautical Miles\n");    	

    	System.out.println(distance(30.626354, 104.144173, 30.626497, 104.144241, 'K') + " Kilometers\n");
    	System.out.println(distance(30.626354, 104.144173, 30.626497, 104.144241) + " Kilometers\n");
    }
}