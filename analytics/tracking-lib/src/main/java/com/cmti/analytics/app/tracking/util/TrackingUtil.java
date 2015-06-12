package com.cmti.analytics.app.tracking.util;

import org.apache.log4j.Logger;

import com.cmti.analytics.util.StringUtil;

public class TrackingUtil { 

	private static Logger logger = Logger.getLogger(TrackingUtil.class);	

	public static int extractRoadIdFromDriveTestId(String driveTestId) {//driveTestId is like 0102885120140928160610ms9.csv
		String roadIdStr = driveTestId.substring(1, 8);
		int roadId = StringUtil.getInt(roadIdStr);

        return roadId;
	}

}







