package com.cmti.analytics.app.tracking.hbase.domain;

import java.util.List;

import com.cmti.analytics.app.tracking.hbase.domain.bean.RoadCellBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoadCell extends RoadCellBean {
	protected static final Logger logger = LogManager.getLogger(RoadCell.class);

	public void appendGeo(RoadTestData roadTestData) {
		appendGeo(roadTestData.getLongitude(), roadTestData.getLatitude());
	}

	public void appendGeo(Double lon, Double lat) {
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
		}
	}
	
	@Override
	public String toString(){ 
		return String.format("RoadCell(roadId=%s, order=%s, cell=%s, fuzzy id=%s, lon size=%s)", roadId, order, cellId, fuzzyId, getLongitudeList().size());
	}
	
}
