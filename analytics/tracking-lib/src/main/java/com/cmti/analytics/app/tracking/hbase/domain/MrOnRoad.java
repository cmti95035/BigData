package com.cmti.analytics.app.tracking.hbase.domain;

import com.cmti.analytics.app.tracking.hbase.domain.bean.MrOnRoadBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MrOnRoad extends MrOnRoadBean {
	protected static final Logger logger = LogManager.getLogger(MrOnRoad.class);

	public MrOnRoad(int roadId, UserCell cell, Mr mr){
//keys		
		this.roadId = roadId;
		this.imsi = mr.getImsi();
		this.time = mr.getTime();
		
//fields
		this.cell = cell.getCellId();
		this.rscp = mr.getRscp();
//		this.lon = from user cell entering and exiting time , mr time,  and userCell's lon lat TODO
	}
}
