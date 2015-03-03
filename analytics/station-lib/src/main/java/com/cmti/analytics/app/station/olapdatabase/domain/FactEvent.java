package com.cmti.analytics.app.station.olapdatabase.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "fact_event" )
public class FactEvent   { 
	@Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
	@Column(name="id", nullable = false) 
	protected int id;	
 
    @Column(name ="event_count", nullable = false) 
    public int eventCount;     

    @Column(name ="date_id", nullable = false) 
    public int dateId;

    @Column(name ="event_type_id", nullable = false) 
    public int eventTypeId;

    @Column(name ="result_id", nullable = false) 
    public int resultId;

 ///////////////////////////////////////////////

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public int getDateId() {
		return dateId;
	}

	public void setDateId(int dateId) {
		this.dateId = dateId;
	}

	public int getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}
} 
