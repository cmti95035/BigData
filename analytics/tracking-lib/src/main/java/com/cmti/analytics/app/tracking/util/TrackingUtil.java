package com.cmti.analytics.app.tracking.util;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.util.StringUtil;

public class TrackingUtil { 

	protected static final Logger logger = LogManager.getLogger(TrackingUtil.class);

	public static int extractRoadIdFromDriveTestId(String driveTestId) {//driveTestId is like 0102885120140928160610ms9.csv
		String roadIdStr = driveTestId.substring(1, 8);
		int roadId = StringUtil.getInt(roadIdStr);

        return roadId;
	}

	public static int getProjectIndex(int m, int n, int i){
		if(n>m){
			logger.error("m={}, n={}, i={}", m, n, i);
			return i>m-1?m-1:i;//TODO can handle this better
		}
		int d=m/n;
		
		return d*i;
	}
	

	public static void main(String[] args){

		logger.error("{}", getProjectIndex(10,3,0));
		logger.error("{}", getProjectIndex(10,3,1));
		logger.error("{}", getProjectIndex(10,3,2));
		logger.error("{}", getProjectIndex(3,3,0));
		logger.error("{}", getProjectIndex(3,3,1));
		logger.error("{}", getProjectIndex(3,3,2));
		logger.error("{}", getProjectIndex(10,30,0));
		logger.error("{}", getProjectIndex(10,30,5));
		logger.error("{}", getProjectIndex(10,30,9));
		logger.error("{}", getProjectIndex(10,30,10));
		logger.error("{}", getProjectIndex(10,30,29));
	}
}







