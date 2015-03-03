package com.cmti.analytics.app.station.olapdatabase.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * tower station
 * @author gmo
 *
 */
@Entity
@Table( name = "dim_station" )
public class DimStation { 
	@Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
	@Column(name="id", nullable = false) 
	protected int id;	

	@Column(name = "bsc")
	public String bsc; 

	@Column(name = "cgi")
	public String cgi;	

	@Column(name = "longitude")
	public Double lng;	
	
	@Column(name = "latitude")
	public Double lat;	

	@Column(name = "type")
	public String type;	 
	
	@Column(name = "lac")
	public Integer lac;

	@Column(name = "ci")
	public Integer ci;
	
	@Column(name = "angle")
	public Double angle;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBsc() {
		return bsc;
	}

	public void setBsc(String bsc) {
		this.bsc = bsc;
	}

	public String getCgi() {
		return cgi;
	}

	public void setCgi(String cgi) {
		this.cgi = cgi;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getLac() {
		return lac;
	}

	public void setLac(Integer lac) {
		this.lac = lac;
	}

	public Integer getCi() {
		return ci;
	}

	public void setCi(Integer ci) {
		this.ci = ci;
	}

	public Double getAngle() {
		return angle;
	}

	public void setAng(Double angle) {
		this.angle = angle;
	}
	 
	@Override
	public String toString(){
		return String.format("%s (%s %s %s)", id, bsc, lat, lng);
	}
	// macro station or wireless room sub-station 
//	@Column(value = "room")
	//public Boolean room;	

} 
