package com.cmti.analytics.app.tracking.hbase.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * a class wraps fuzzy road cells into one Virtual Road Cell. 
 * like UserCell, this is not a hbase table domain
 * 
 * @author Guobiao Mo
 *
 */
public class VirtualRoadCell {
	protected static final Logger logger = LogManager.getLogger(VirtualRoadCell.class);

	private List<RoadCell> roadCells;
	private List<UserCell> userCells;
	
	private int id;
	
	private VirtualRoadCell next;
	
	public VirtualRoadCell() {
		roadCells = new ArrayList<>();
		userCells = new ArrayList<>();
	}

	public void addUserCell(UserCell ucell){
		userCells.add(ucell);
	}
	
	public void clearUserCell() {
		userCells.clear();
	}
	
	//called by RoadDao when setting up a Road
	public void addRoadCell(RoadCell rcell){
		roadCells.add(rcell);
	}

	//check if a user cell falls in one of the road cells
	public boolean covers(UserCell ucell){
		int cellId=ucell.getCellId();
		for(RoadCell roadCell:roadCells) {
			if(cellId==roadCell.getCellId()){
				return true;
			}
		}
		return false;
	}

	public List<Mr> getAllMrs() {
		List<Mr> ret = new ArrayList<>();
		for(UserCell userCell : userCells) {
			ret.addAll(userCell.getMrList());
		}
		return ret;
	}

	public List<Integer> getAllDriveTestDataFrameIds() {
		List<Integer> ret = new ArrayList<>();
		for(RoadCell roadCell : roadCells) {
			ret.addAll(roadCell.getFrameList());
		}
		return ret;
	}

	public int getRoadId(){
		return getRoadCells().get(0).getRoadId();
	}
	
	public String getDriveTestId(){
		return getRoadCells().get(0).getDriveTestId();
	}

	@Override
	public String toString(){ 
		return String.format("VirtualRoadCell(id=%s, roadCells.size()=%s, roadCells=%s, userCells=%s)", 
				id, roadCells.size(), Arrays.toString(roadCells.toArray()), Arrays.toString(userCells.toArray()));
	}
	
/////auto gen
	public List<RoadCell> getRoadCells() {
		return roadCells;
	}

	public void setRoadCells(List<RoadCell> roadCells) {
		this.roadCells = roadCells;
	}

	public List<UserCell> getUserCells() {
		return userCells;
	}

	public void setUserCells(List<UserCell> userCells) {
		this.userCells = userCells;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public VirtualRoadCell getNext() {
		return next;
	}

	public void setNext(VirtualRoadCell next) {
		this.next = next;
	}
	
}
