package com.cmti.analytics.app.tracking.olapdatabase.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "road_test_simple" )
public class RoadTest { 
	@Id
	@Column(name="frame", nullable = false) 
	public int frame;	

    @Column(name ="cell", nullable = false) 
    public int cell;

    //auto gen code============
    
	public int getFrame() {
		return frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}
} 
