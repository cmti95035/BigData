package com.cmti.analytics.app.tracking.hbase.domain;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * a temp class records a user travel in a cell. 
 * this is not a hbase table domain
 * 
 * @author gmo
 *
 */
public class UserCell {
	protected static final Logger logger = LogManager.getLogger(UserCell.class);

	int cellId;
	Date enteringDate;
	Date exitingDate;
	long timeInCell;//time spent in the cell
	int startId;//start id in the user mr list
	int endId;//end id in the user mr list	

	@Override
	public String toString() {
		return String.format("UserCell(cellId=%s, enteringDate=%s, exitingDate=%s, startId=%s, endId=%s)", cellId, enteringDate, exitingDate, startId, endId);
	}
	
///////////////////////////////////////////////
	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public Date getEnteringDate() {
		return enteringDate;
	}

	public void setEnteringDate(Date enteringDate) {
		this.enteringDate = enteringDate;
	}

	public Date getExitingDate() {
		return exitingDate;
	}

	public void setExitingDate(Date exitingDate) {
		this.exitingDate = exitingDate;
	}

	public long getTimeInCell() {
		return timeInCell;
	}

	public void setTimeInCell(long timeInCell) {
		this.timeInCell = timeInCell;
	}

	public int getStartId() {
		return startId;
	}

	public void setStartId(int startId) {
		this.startId = startId;
	}

	public int getEndId() {
		return endId;
	}

	public void setEndId(int endId) {
		this.endId = endId;
	}
}
