package com.cmti.analytics.app.tracking.hbase.domain;

import java.util.List;

import com.cmti.analytics.app.tracking.hbase.domain.bean.RoadCellBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoadCell extends RoadCellBean {
	protected static final Logger logger = LogManager.getLogger(RoadCell.class);

	public int getVirtualRoadCellId(){
		Integer ret = super.getFuzzyId();
		if(ret==null){
			ret= super.getOrder();
		}
		return ret;
	}
	
	public void appendDriveTestData(DriveTestData driveTestData) {
		Double lon = driveTestData.getLongitude();
		Double lat = driveTestData.getLatitude();
		
		boolean sameAsLast = false;
		
		if(this.getLongitudeList()!=null && this.getLongitudeList().size()>0){
			List<Double> lons = this.getLongitudeList();
			List<Double> lats = this.getLatitudeList();
			
			int size = this.getLongitudeList().size();
			Double lastLon = lons.get(size-1);
			Double lastLat = lats.get(size-1);
			
			sameAsLast = lastLon.equals(lon) && lastLat.equals(lat);
		}
		
		if(sameAsLast==false) {
			addLongitudeList(lon);
			addLatitudeList(lat);
			super.addFrameList(driveTestData.getFrame());
			super.addRscpList(driveTestData.getRscp());
		}
	}
	
	@Override
	public String toString() {
		return String.format("RoadCell(roadId=%s, order=%s, cellId=%s, fuzzy id=%s, frame size=%s)", roadId, order, cellId, fuzzyId, frameList==null?0:frameList.size());
	}
	
}
