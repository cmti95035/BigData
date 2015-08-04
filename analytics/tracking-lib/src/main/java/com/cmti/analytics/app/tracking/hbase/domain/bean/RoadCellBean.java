package com.cmti.analytics.app.tracking.hbase.domain.bean;

import com.cmti.analytics.util.ObjectUtil;
import com.cmti.analytics.app.tracking.hbase.domain.mapping.RoadCellMapping;

public class RoadCellBean extends RoadCellMapping {

//key
	public void setRoadId(Integer roadId){
		this.roadId = roadId;
	}

	public Integer getRoadId(){
		return roadId;
	}
	
	public void setOrder(Integer order){
		this.order = order;
	}

	public Integer getOrder(){
		return order;
	}
	

//field
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
 	public void setDriveTestId(String driveTestId){
		boolean updated = !ObjectUtil.equals(this.driveTestId, driveTestId);
		if(updated){
			this.driveTestId = driveTestId;
			setDirty("driveTestId");
		}
	}

	public String getDriveTestId(){
		return driveTestId;
	}
 	public void setFuzzyId(Integer fuzzyId){
		boolean updated = !ObjectUtil.equals(this.fuzzyId, fuzzyId);
		if(updated){
			this.fuzzyId = fuzzyId;
			setDirty("fuzzyId");
		}
	}

	public Integer getFuzzyId(){
		return fuzzyId;
	}
 	public void setLongitudeList(java.util.ArrayList<java.lang.Double> longitudeList){
		boolean updated = !ObjectUtil.equals(this.longitudeList, longitudeList);
		if(updated){
			this.longitudeList = longitudeList;
			setDirty("longitudeList");
		}
	}

	public java.util.ArrayList<java.lang.Double> getLongitudeList(){
		return longitudeList;
	}

	public void addLongitudeList(Double value){
		java.util.ArrayList<java.lang.Double> list = getLongitudeList();
		if(list==null){
			list = new java.util.ArrayList<java.lang.Double>();
			setLongitudeList(list);
		}
					
		list.add(value);
		setDirty("longitudeList");
	}
	
 	public void setLatitudeList(java.util.ArrayList<java.lang.Double> latitudeList){
		boolean updated = !ObjectUtil.equals(this.latitudeList, latitudeList);
		if(updated){
			this.latitudeList = latitudeList;
			setDirty("latitudeList");
		}
	}

	public java.util.ArrayList<java.lang.Double> getLatitudeList(){
		return latitudeList;
	}

	public void addLatitudeList(Double value){
		java.util.ArrayList<java.lang.Double> list = getLatitudeList();
		if(list==null){
			list = new java.util.ArrayList<java.lang.Double>();
			setLatitudeList(list);
		}
					
		list.add(value);
		setDirty("latitudeList");
	}
	
 	public void setFrameList(java.util.ArrayList<java.lang.Integer> frameList){
		boolean updated = !ObjectUtil.equals(this.frameList, frameList);
		if(updated){
			this.frameList = frameList;
			setDirty("frameList");
		}
	}

	public java.util.ArrayList<java.lang.Integer> getFrameList(){
		return frameList;
	}

	public void addFrameList(Integer value){
		java.util.ArrayList<java.lang.Integer> list = getFrameList();
		if(list==null){
			list = new java.util.ArrayList<java.lang.Integer>();
			setFrameList(list);
		}
					
		list.add(value);
		setDirty("frameList");
	}
	
 	public void setRscpList(java.util.ArrayList<java.lang.Integer> rscpList){
		boolean updated = !ObjectUtil.equals(this.rscpList, rscpList);
		if(updated){
			this.rscpList = rscpList;
			setDirty("rscpList");
		}
	}

	public java.util.ArrayList<java.lang.Integer> getRscpList(){
		return rscpList;
	}

	public void addRscpList(Integer value){
		java.util.ArrayList<java.lang.Integer> list = getRscpList();
		if(list==null){
			list = new java.util.ArrayList<java.lang.Integer>();
			setRscpList(list);
		}
					
		list.add(value);
		setDirty("rscpList");
	}
	
  	
	@Override
	public String toString(){
		return "RoadCellBean(" + " roadId="+ roadId + " order="+ order + ")";
	}
	
}