package com.cmti.analytics.app.tracking.hbase.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * a temp class records a user travel in a cell. 
 * this is not a hbase table domain
 * 
 * @author Guobiao Mo
 *
 */
public class UserCell {
	protected static final Logger logger = LogManager.getLogger(UserCell.class);

	private int cellId;

	private List<Mr> mrList = new ArrayList<>();

	public void addMr(Mr mr){
		mrList.add(mr);
	}	
	
	@Override
	public String toString() {
		return String.format("UserCell(cellId=%s, MRs=%s)", cellId, Arrays.toString(mrList.toArray()));
	}
	
///////////////////////////////////////////////
	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public List<Mr> getMrList() {
		return mrList;
	}

	public void setMrList(List<Mr> mrList) {
		this.mrList = mrList;
	}
}
