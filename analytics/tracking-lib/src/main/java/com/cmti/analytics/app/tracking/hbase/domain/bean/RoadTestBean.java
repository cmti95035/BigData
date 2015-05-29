package com.cmti.analytics.app.tracking.hbase.domain.bean;

import com.cmti.analytics.util.ObjectUtil;
import com.cmti.analytics.app.tracking.hbase.domain.mapping.RoadTestMapping;

public class RoadTestBean extends RoadTestMapping {

//key
	public void setRoadTestId(String roadTestId){
		this.roadTestId = roadTestId;
	}

	public String getRoadTestId(){
		return roadTestId;
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
 	public void setDirection(Boolean direction){
		boolean updated = !ObjectUtil.equals(this.direction, direction);
		if(updated){
			this.direction = direction;
			setDirty("direction");
		}
	}

	public Boolean getDirection(){
		return direction;
	}
  	
	@Override
	public String toString(){
		return "RoadTestBean(" + " roadTestId="+ roadTestId + ")";
	}
	
}