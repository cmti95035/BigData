package com.cmti.analytics.app.tracking.task.scan.scanhandler;

import java.io.IOException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.RoadCellDao;
import com.cmti.analytics.app.tracking.hbase.domain.RoadCell;
import com.cmti.analytics.app.tracking.hbase.domain.RoadTestData;
import com.cmti.analytics.hbase.task.scan.IHandler;
 
/**
 * scan RoadTestData of a road to create Road cells of that road
 * 
 * @author gmo
 *
 */
public class RoadTestRoadCellHandler implements IHandler<RoadTestData>{

	protected static final Logger logger = LogManager.getLogger(RoadTestRoadCellHandler.class);
	
	public RoadTestRoadCellHandler() {
		dao = new RoadCellDao();
		try {
			dao.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected RoadCellDao dao;
	 
	@Override
	public void close() throws IOException {
		saveAndClearRoad();
		dao.close();
		logger.info("done");	
	}
	
	Integer roadId;
	RoadCell cell;
	//Double longitude, latitude;
	
	List<RoadCell> cells=new ArrayList<RoadCell>();
	
	@Override 
	public void handle(RoadTestData roadTestData) {
		if(cell==null){//brand new
			newRoadCell(roadTestData);
			return;
		}

		Integer lastRoadId = cell.getRoadId();
		Integer thisRoadId = roadTestData.getRoadId();
		if(thisRoadId.equals(lastRoadId) == false){//new road 
			logger.error("thisRoadId {}!= lastRoadId {}",thisRoadId, lastRoadId);
			//output cells in the previous road, 
			saveAndClearRoad();
			newRoadCell(roadTestData);
			return;
		}
		
		if(cell.getCellId().equals(roadTestData.getCell())){
			//in the same cell, add lon and lat if not present
			cell.appendLonLat(roadTestData);
		}else{	//new cell
			newRoadCell(roadTestData);
			return;
		}
	}
	
	private void newRoadCell(RoadTestData roadTestData) {
		cell = new RoadCell();
		cell.setCellId(roadTestData.getCell());
		cell.setRoadId(roadTestData.getRoadId());
		// add lat lon 
		cell.appendLonLat(roadTestData);
		cells.add(cell);
	}

	//do 2 extra things here:
	//1. set order
	//2. set fuzzy
	//calculate distance
	//combine ender starter and their lon lat
	
	//TODO if road test car ran overlap some road segment, 
	private void saveAndClearRoad() {
		
		//for a loop, combine starter and ender if they are the same cell.
		RoadCell cell0 = cells.get(0);
		RoadCell cellLast = cells.get(cells.size()-1);
		if(cell0.getCellId().equals(cell.getCellId())) {
			cell0.getLongitudeList().addAll(0, cellLast.getLongitudeList());
			cell0.getLatitudeList().addAll(0, cellLast.getLatitudeList());
		}
		cells.remove(cells.size()-1);
		
		int len = cells.size();

		for(int i=0; i<len; i++) {
			RoadCell cell = cells.get(i);
						
			cell.setOrder(i+1);//order is non-zero, later we need negative of it 
		}
		
		for(int i=0; i<len; i++) {
			RoadCell cell = cells.get(i);
			
			int cellId=cell.getCellId();
			
			//find fuzzy FIXME ender starter fuzzy 
			for(int j = len-1; j>i+1; j--) {
				RoadCell tmpCell = cells.get(j);
				if(tmpCell.getCellId().equals(cellId) && j-i< 10){//FIXME 10 is arbitrary
					//if(tmpCell.getCellId().equals(cellId) && j-i<len/2){ cd = 9607 has issue, cell 9607 appears far away

//					logger.error("apart={} {} {} same cell id, mark all between fuzzy = true", j-i, cell, tmpCell);
					
					Integer fuzzyId = cell.getFuzzyId();
					if(fuzzyId==null) {
						fuzzyId = cell.getOrder();
					}
					for(int k= i; k<=j; k++) {
						RoadCell fuzzyCell = cells.get(k);
						fuzzyCell.setFuzzyId(fuzzyId);
					}
					break;
				}
			}

			try {
				dao.insert(cell);
				logger.error(cell);
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				logger.error(cell, e);
			}
		}

		cells.clear();
	}
}
