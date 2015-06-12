package com.cmti.analytics.app.tracking.hbase.domain;

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
	
	CircularArrayList<RoadCell> cells;
//	CircularListIterator<RoadCell> iterator;


	public void setRoadCells(List<RoadCell> cells) {
		if(cells != null)
			this.cells = new CircularArrayList<RoadCell>(cells);//assume road cells are ordered TODO should make sure it is ordered here
//		iterator = new CircularListIterator<RoadCell>(cells);
	}

	public List<RoadCell> getRoadCells() {
		return cells;
	}

	public List<UserCell> findMatchCell(List<UserCell> ucells, int minMatch) {
		List<UserCell> ret = new ArrayList<UserCell>();
		List<UserCell> tmp = new ArrayList<UserCell>();

		Integer matchRoadCellId = null; //last matched roadCell id
		logger.info("road id={}, ucells.size()={}", this.getRoadId(), ucells.size());
		
		for (int i = 0; i < ucells.size(); i++) {
			UserCell ucell = ucells.get(i);

			logger.info("i={}, match id ={}, ucell={}", i, matchRoadCellId, ucell);
			
			if (matchRoadCellId == null) {//new life
				matchRoadCellId = find(ucell); 
				logger.info("new life, i={} ,  match id ={} add 1st ucell={}", i, matchRoadCellId, ucell);
				tmp.add(ucell); 
				continue;
			}
			
			//do ucell, if it is matched next cell, returned matchRoadCellId is not null
			matchRoadCellId = match(ucell, matchRoadCellId);

			if (matchRoadCellId != null) {
				logger.info("found match, add ucell to tmp {}", ucell);
				tmp.add(ucell);
			} else {
				logger.info("end here by ucell={} ", ucell);
				if (tmp.size() >= minMatch) {
					logger.info("got tmp.size()={}, add them", tmp.size());
					ret.addAll(tmp);
				}else{
					logger.info("got tmp.size()={}, skip them", tmp.size());
				}
				tmp.clear();
			}			
		}

		if (tmp.size() >= minMatch) {
			logger.info("got tmp.size()={}, add them", tmp.size());
			ret.addAll(tmp);
		}else{
			logger.info("got tmp.size()={}, skip them", tmp.size());
		}

		return ret;
	}	

	private Integer match(UserCell ucell, int lastMatchId) { 
		RoadCell lastMatchCell = cells.get(lastMatchId);
		
		Integer lastMatchCellFuzzyId = lastMatchCell.getFuzzyId();
		 
		if(lastMatchCellFuzzyId==null){//previous match is not fuzzy, ucell has only one chance, searching from lastMatchId+1.
			int matchId = matchOneZone(ucell, lastMatchId+1);
			return matchId<0?null:matchId;
		}else{//previous match is fuzzy, ucell has 2 chances, current fuzzy zone, and next 
			int matchId = matchOneZone(ucell, lastMatchCellFuzzyId);
			if(matchId>0){
				return matchId;//found
			}
				
			matchId = matchOneZone(ucell, -matchId);
			
			return matchId<0?null:matchId;
		}
	}
	
	//check next (non-fuzzy or fuzzy) zone, starting at id
	//return id if found; return -id if not found, where id is the next zone's starting id 
	private int matchOneZone(UserCell ucell, int id) {//TODO make sure id>0 since -id requires that
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
			
			//just hit a non-fuzzy cell
			if(thisFuzzyId==null){
				logger.info("just hit a non-fuzzy cell, ucell={}, id={}", ucell, id);
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
	private int find(UserCell ucell) {
		for(int ret = 0; ret<cells.size(); ret++){
			RoadCell rcell = cells.get(ret);
			if (rcell.getCellId().equals(ucell.getCellId())) {
				return ret;
			}			
		}

		throw new RuntimeException("should not come to here, user cell = "+ ucell);
	}

	@Override
	public String toString(){ 
		return String.format("RoadCell(roadId=%s, province=%s, city=%s, name=%s, loop=%s)", roadId, province, city, name, loop);
	}
	
}
