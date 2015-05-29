package com.cmti.analytics.app.tracking.hbase.domain.bean;

import com.cmti.analytics.util.ObjectUtil;
import com.cmti.analytics.app.tracking.hbase.domain.mapping.MrMapping;

public class MrBean extends MrMapping {

//key
	public void setImsi(Long imsi){
		this.imsi = imsi;
	}

	public Long getImsi(){
		return imsi;
	}
	
	public void setTime(java.util.Date time){
		this.time = time;
	}

	public java.util.Date getTime(){
		return time;
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
		return "MrBean(" + " imsi="+ imsi + " time="+ time + ")";
	}
	
}