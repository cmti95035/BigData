package com.cmti.analytics.app.tracking.hbase.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cmti.analytics.app.tracking.hbase.domain.bean.RoadBean;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.util.CircularArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Road extends RoadBean {
	protected static final Logger logger = LogManager.getLogger(Road.class);

	protected org.apache.commons.configuration.Configuration config = Config.getConfig();

	List<RoadCell> cells;
	List<VirtualRoadCell> vcells;
	
	public boolean isLoop(){
		return Boolean.TRUE.equals(super.getLoop());
	}

	public void setRoadCells(List<RoadCell> cells) {//assume road cells are ordered 
		if(cells == null){
			logger.error("road id={}, ucells==null", this.getRoadId());
			return;
		}
		
		if(isLoop()) {
			this.cells = new CircularArrayList<>(cells);
		}else{
			this.cells = cells;
		}
	}

	public List<RoadCell> getRoadCells() {
		return cells;
	}

	public void setVirtualRoadCells(List<VirtualRoadCell> cells) {
		if(cells == null){
			logger.error("road id={}, v road cells==null", this.getRoadId());
			return;
		}
		
		if(isLoop()) {
			this.vcells = new CircularArrayList<>(cells);
		}else{
			this.vcells = cells;
		}
	}

	public List<VirtualRoadCell> getVirtualRoadCells() {
		return vcells;
	}

	public void setupVirtualRoadCells() throws IOException {
		List<VirtualRoadCell> vcells = new ArrayList<>();
		VirtualRoadCell last = null;
		for(RoadCell cell : cells) {
			if(last == null) {
				last = new VirtualRoadCell();
				last.setId(cell.getVirtualRoadCellId());
				last.addRoadCell(cell);
				
				vcells.add(last);
				continue;
			}
			
			if(last.getId()==cell.getVirtualRoadCellId()){
				last.addRoadCell(cell);
				continue;				
			}else{
				VirtualRoadCell now = new VirtualRoadCell();
				now.setId(cell.getVirtualRoadCellId());
				now.addRoadCell(cell);
				vcells.add(now);
				
				last.setNext(now);
				last = now;
				continue;				
			}
		}
		
		setVirtualRoadCells(vcells);
	}
	
	public List<UserCell> findMatchCell_notused(List<UserCell> ucells, int minMatch) {
		List<UserCell> ret = new ArrayList<>();
		List<UserCell> tmp = new ArrayList<>();

		Integer lastMatchRoadCellId = null; //last matched roadCell id
		logger.info("road id={}, ucells.size()={}", this.getRoadId(), ucells.size());
		
		for (int i = 0; i < ucells.size(); i++) {
			UserCell ucell = ucells.get(i);

			logger.info("i={}, lastMatchRoadCellId={}, ucell={}", i, lastMatchRoadCellId, ucell);
			
			if (lastMatchRoadCellId == null) {//new life
				lastMatchRoadCellId = find_notused(ucell); 
				logger.info("start a new block,  lastMatchRoadCellId={}", lastMatchRoadCellId);
				tmp.add(ucell); 
				continue;
			}
			
			//do ucell, if it is matched next cell, returned matchRoadCellId is not null
			lastMatchRoadCellId = match_notused(ucell, lastMatchRoadCellId);

			if (lastMatchRoadCellId != null) {
				logger.info("found match, add ucell to tmp. {}", ucell);
				tmp.add(ucell);
			} else {
				finishBlock_notused(ret, tmp, minMatch);
				logger.info("block end here by ucell={} ", ucell);
			}			
		}
		
		finishBlock_notused(ret, tmp, minMatch);
		
		return ret;
	}	
	
	private void finishBlock_notused(List<UserCell> ret, List<UserCell> tmp, int minMatch) {
		if (tmp.size() >= minMatch) {
			logger.info("got tmp.size()={}, add them", tmp.size());
			ret.addAll(tmp);
		}else{
			logger.info("got tmp.size()={}, skip them", tmp.size());
		}
		tmp.clear();
	}

	private Integer match_notused(UserCell ucell, int lastMatchId) { 
		RoadCell lastMatchCell = cells.get(lastMatchId);
		
		Integer lastMatchCellFuzzyId = lastMatchCell.getFuzzyId();
		 
		if(lastMatchCellFuzzyId==null){//previous match is not fuzzy, ucell has only one chance, searching from lastMatchId+1.
			int matchId = matchOneZone_notused(ucell, lastMatchId+1);
			return matchId<0?null:matchId;
		}else{//previous match is fuzzy, ucell has 2 chances, current fuzzy zone, and next 
			int matchId = matchOneZone_notused(ucell, lastMatchCellFuzzyId);
			if(matchId>0){//because of this, road cell id can not be 0.
				return matchId;//found
			}
				
			matchId = matchOneZone_notused(ucell, -matchId);
			
			return matchId<0?null:matchId;
		}
	}
	
	//check next (non-fuzzy or fuzzy) zone, starting at id
	//return id if found; return -id if not found, where id is the next zone's starting id 
	private int matchOneZone_notused(UserCell ucell, int id) {//TODO make sure id>0 since -id requires that
//		boolean working = true;
		Integer lastFuzzyId = null; 
		
		while(true) {
			RoadCell rcell = cells.get(id);

			//found
			if(rcell.getCellId().equals(ucell.getCellId())) {
				logger.info("found ucell={}, id={}", ucell, id);
				return id;
			}

			Integer thisFuzzyId = rcell.getFuzzyId();
			
			//hit a non-fuzzy cell
			if(thisFuzzyId==null){
				logger.info("just hit a non-fuzzy cell, ucell={}, id={}, return {}", ucell, id, -id);
				return -id;
			}			
			
			if(lastFuzzyId == null){
				//we are in a fuzzy zone
				lastFuzzyId = thisFuzzyId;
				logger.info("we are in a fuzzy zone ucell={}, id={}, thisFuzzyId={}", ucell, id, thisFuzzyId);
			}else{
				if(lastFuzzyId.equals(thisFuzzyId) == false){
					//from a fuzzy zone entering another (non-fuzzy or fuzzy) zone 
					logger.info("from a fuzzy zone entering another (non-fuzzy or fuzzy) zone, ucell={}, id={}, thisFuzzyId={}, lastFuzzyId={}", 
							ucell, id, thisFuzzyId, lastFuzzyId);
					return -id;
				}else{//else still in the same fuzzy zone, continue to check the next cell
					logger.info("else still in the same fuzzy zone, continue to check the next cell, ucell={}, id={}, thisFuzzyId={}, lastFuzzyId={}", 
							ucell, id, thisFuzzyId, lastFuzzyId);
				}
			}
			
			id++;
		}
	}
	
	//simply loop road cells and find the 1st one with the same cell id
	public int find_notused(UserCell ucell) {
		for(int ret = 0; ret<cells.size(); ret++){
			RoadCell rcell = cells.get(ret);
			if (rcell.getCellId().equals(ucell.getCellId())) {
				return ret;
			}			
		}

		throw new RuntimeException("should not come to here, user cell = "+ ucell);
	}

	public VirtualRoadCell findVirtualRoadCell(UserCell ucell) {
		for(VirtualRoadCell vroadCell : vcells) {
			if (vroadCell.covers(ucell)) {
				return vroadCell;
			}			
		}
		throw new RuntimeException("findVirtualRoadCell(): should not come to here, user cell = "+ ucell);
	}

	@Override
	public String toString(){ 
		return String.format("Road(roadId=%s, province=%s, city=%s, name=%s, loop=%s)", roadId, province, city, name, loop);
	}
	
}
