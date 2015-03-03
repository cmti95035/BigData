package com.cmti.analytics.app.station.olapdatabase.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "fact_imsi_sig" )
public class FactImsi { 
	@Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
	@Column(name="id", nullable = false) 
	protected int id;	

    @Column(name ="period_type", nullable = false) 
    public int periodType;//0:day 1 week 2 month 3 year

    @Column(name ="imsi_count") 
    public Integer imsiCount;

    @Column(name ="date_id", nullable = false) 
    public int dateId;

    @Column(name ="result_type_id", nullable = false) 
    public int resultTypeId;

    //auto gen code============
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPeriodType() {
		return periodType;
	}

	public void setPeriodType(int periodType) {
		this.periodType = periodType;
	}

	public Integer getImsiCount() {
		return imsiCount;
	}

	public void setImsiCount(Integer imsiCount) {
		this.imsiCount = imsiCount;
	}

	public int getDateId() {
		return dateId;
	}

	public void setDateId(int dateId) {
		this.dateId = dateId;
	}

	public int getResultTypeId() {
		return resultTypeId;
	}

	public void setResultTypeId(int resultTypeId) {
		this.resultTypeId = resultTypeId;
	}

} 
