package com.cmti.analytics.app.tracking.hbase.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.Road;
import com.cmti.analytics.app.tracking.hbase.domain.RoadCell;
import com.cmti.analytics.app.tracking.hbase.domain.VirtualRoadCell;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;

/**
 * Dao for 'Road'
 * @author Guobiao Mo
 *
 */
public class RoadDao extends HBaseGenericDao<Road, Integer> {

	protected static final Logger logger = LogManager.getLogger(RoadDao.class);
	RoadCellDao roadCellDao;
	
	public RoadDao() throws IOException{
		roadCellDao = new RoadCellDao();
		roadCellDao.open();
	}

	public List<Road> getAllRoads() throws IOException {
		List<Road> roads = super.getAll();
		for(Road road : roads) {
			populateRoadCells(road);
		}
		
		return roads;		
	}

	// populated with RoadCells and VirtualRoadCells
	public Road getRoad(int roadId) throws IOException {
		Road road = super.getByKey(roadId);
		populateRoadCells(road);
		road.setupVirtualRoadCells();
		return road;
	}
/*
	//not used from outside, so private
	private Road getRoadWithRoadCells(int roadId) throws IOException {
		Road road = getRoad(roadId);
		populateRoadCells(road);
		return road;
	}

	public Road getRoadWithVirtualRoadCells(int roadId) throws IOException {
		Road road = getRoadWithRoadCells(roadId);
		setupVirtualRoadCells(road);
		return road;
	}*/

	private void setupVirtualRoadCells(Road road) throws IOException {//TODO loop
		List<RoadCell> cells = road.getRoadCells();
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
		
		road.setVirtualRoadCells(vcells);//TODO loop .next should be set for ender.
	}
	
	//populate cells on road
	private void populateRoadCells(Road road) throws IOException {
		List<RoadCell> cells = roadCellDao.getRoadCellsByRoadId(road.getRoadId());//cells can be null
		road.setRoadCells(cells);

		logger.info("RoadCell size={} after populate RoadCell for road={}", cells==null?0:cells.size(), road);	
	}
	
	@Override
	public void close() {
		roadCellDao.close();
		super.close();
	}
}
