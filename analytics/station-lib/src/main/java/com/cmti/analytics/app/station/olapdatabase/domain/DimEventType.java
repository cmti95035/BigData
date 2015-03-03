package com.cmti.analytics.app.station.olapdatabase.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;

@Entity
@Table( name = "dim_event_type" )
public class DimEventType implements IDimObject { 
	@Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
	@Column(name="id", nullable = false) 
	protected int id;	
    
    @Column(name ="name", nullable = false, length=200) 
	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getKey() {
		return  getName();
	}

} 
