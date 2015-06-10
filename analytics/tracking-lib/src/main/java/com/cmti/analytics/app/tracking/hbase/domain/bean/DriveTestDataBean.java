package com.cmti.analytics.app.tracking.hbase.domain.bean;

import com.cmti.analytics.util.ObjectUtil;
import com.cmti.analytics.app.tracking.hbase.domain.mapping.DriveTestDataMapping;

public class DriveTestDataBean extends DriveTestDataMapping {

//key
	public void setRoadId(Integer roadId){
		this.roadId = roadId;
	}

	public Integer getRoadId(){
		return roadId;
	}
	
	public void setDriveTestId(String driveTestId){
		this.driveTestId = driveTestId;
	}

	public String getDriveTestId(){
		return driveTestId;
	}
	
	public void setFrame(Integer frame){
		this.frame = frame;
	}

	public Integer getFrame(){
		return frame;
	}
	

//field
	public void setCell(Integer cell){
		boolean updated = !ObjectUtil.equals(this.cell, cell);
		if(updated){
			this.cell = cell;
			setDirty("cell");
		}
	}

	public Integer getCell(){
		return cell;
	}
 	public void setTime(java.util.Date time){
		boolean updated = !ObjectUtil.equals(this.time, time);
		if(updated){
			this.time = time;
			setDirty("time");
		}
	}

	public java.util.Date getTime(){
		return time;
	}
 	public void setLongitude(Double longitude){
		boolean updated = !ObjectUtil.equals(this.longitude, longitude);
		if(updated){
			this.longitude = longitude;
			setDirty("longitude");
		}
	}

	public Double getLongitude(){
		return longitude;
	}
 	public void setLatitude(Double latitude){
		boolean updated = !ObjectUtil.equals(this.latitude, latitude);
		if(updated){
			this.latitude = latitude;
			setDirty("latitude");
		}
	}

	public Double getLatitude(){
		return latitude;
	}
 	public void setRscp(Integer rscp){
		boolean updated = !ObjectUtil.equals(this.rscp, rscp);
		if(updated){
			this.rscp = rscp;
			setDirty("rscp");
		}
	}

	public Integer getRscp(){
		return rscp;
	}
  	
	@Override
	public String toString(){
		return "DriveTestDataBean(" + " roadId="+ roadId + " driveTestId="+ driveTestId + " frame="+ frame + ")";
	}
	
}