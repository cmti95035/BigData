package com.cmti.analytics.app.tracking.hbase.domain.bean;

import com.cmti.analytics.util.ObjectUtil;
import com.cmti.analytics.app.tracking.hbase.domain.mapping.RoadMapping;

public class RoadBean extends RoadMapping {

//key
	public void setRoadId(Integer roadId){
		this.roadId = roadId;
	}

	public Integer getRoadId(){
		return roadId;
	}
	

//field
	public void setProvince(String province){
		boolean updated = !ObjectUtil.equals(this.province, province);
		if(updated){
			this.province = province;
			setDirty("province");
		}
	}

	public String getProvince(){
		return province;
	}
 	public void setCity(String city){
		boolean updated = !ObjectUtil.equals(this.city, city);
		if(updated){
			this.city = city;
			setDirty("city");
		}
	}

	public String getCity(){
		return city;
	}
 	public void setName(String name){
		boolean updated = !ObjectUtil.equals(this.name, name);
		if(updated){
			this.name = name;
			setDirty("name");
		}
	}

	public String getName(){
		return name;
	}
 	public void setLoop(Boolean loop){
		boolean updated = !ObjectUtil.equals(this.loop, loop);
		if(updated){
			this.loop = loop;
			setDirty("loop");
		}
	}

	public Boolean getLoop(){
		return loop;
	}
  	
	@Override
	public String toString(){
		return "RoadBean(" + " roadId="+ roadId + ")";
	}
	
}