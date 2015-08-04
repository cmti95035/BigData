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
	public void setImsi2(Long imsi2){
		boolean updated = !ObjectUtil.equals(this.imsi2, imsi2);
		if(updated){
			this.imsi2 = imsi2;
			setDirty("imsi2");
		}
	}

	public Long getImsi2(){
		return imsi2;
	}
 	public void setTime2(java.util.Date time2){
		boolean updated = !ObjectUtil.equals(this.time2, time2);
		if(updated){
			this.time2 = time2;
			setDirty("time2");
		}
	}

	public java.util.Date getTime2(){
		return time2;
	}
 	public void setCellId(Integer cellId){
		boolean updated = !ObjectUtil.equals(this.cellId, cellId);
		if(updated){
			this.cellId = cellId;
			setDirty("cellId");
		}
	}

	public Integer getCellId(){
		return cellId;
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