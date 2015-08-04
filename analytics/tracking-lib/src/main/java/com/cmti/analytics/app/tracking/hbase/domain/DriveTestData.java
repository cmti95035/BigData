package com.cmti.analytics.app.tracking.hbase.domain;

import com.cmti.analytics.app.tracking.hbase.domain.bean.DriveTestDataBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriveTestData extends DriveTestDataBean {
	protected static final Logger logger = LogManager.getLogger(DriveTestData.class);

	public Double getAverageMrRscp(){

 		Long count = getMrCount();
 		Long rscpSum = getMrRscpSum();
 		
 		if(count==null || rscpSum==null){
 			return null;
 		}
 		
 		return rscpSum.doubleValue()/count.doubleValue();
	}
}
