package com.cmti.analytics.app.station.olapdatabase.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "sig_history" )
public class SigHistory { 
	@Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
	@Column(name="id", nullable = false) 
	protected int id;	

    @Column(name ="cell", nullable = false) 
    public int cell;//0:day 1 week 2 month 3 year

    @Column(name ="imsi_count") 
    public Integer imsiCount;

    @Column(name ="day_of_week", nullable = false) 
    public int dayOfWeek;

    @Column(name ="hour", nullable = false) 
    public int hour;

    //auto gen code============
    
    
	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public Integer getImsiCount() {
		return imsiCount;
	}

	public void setImsiCount(Integer imsiCount) {
		this.imsiCount = imsiCount;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getId() {
		return id;
	}

} 
