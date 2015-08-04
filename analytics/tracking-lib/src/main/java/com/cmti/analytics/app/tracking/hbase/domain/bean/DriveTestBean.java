package com.cmti.analytics.app.tracking.hbase.domain.bean;

import com.cmti.analytics.util.ObjectUtil;
import com.cmti.analytics.app.tracking.hbase.domain.mapping.DriveTestMapping;

public class DriveTestBean extends DriveTestMapping {

//key
	public void setDriveTestId(String driveTestId){
		this.driveTestId = driveTestId;
	}

	public String getDriveTestId(){
		return driveTestId;
	}
	

//field
	public void setRoadId(Integer roadId){
		boolean updated = !ObjectUtil.equals(this.roadId, roadId);
		if(updated){
			this.roadId = roadId;
			setDirty("roadId");
		}
	}

	public Integer getRoadId(){
		return roadId;
	}
  	
	@Override
	public String toString(){
		return "DriveTestBean(" + " driveTestId="+ driveTestId + ")";
	}
	
}