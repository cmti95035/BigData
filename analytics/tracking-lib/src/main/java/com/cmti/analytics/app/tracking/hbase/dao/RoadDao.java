package com.cmti.analytics.app.tracking.hbase.dao;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.domain.Road;
import com.cmti.analytics.app.tracking.hbase.domain.RoadCell;
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

//	@Override we don't override getAll() since we want to have 2 versions, one with cells, one w/o
	public List<Road> getAllRoads() throws IOException {
		List<Road> roads = super.getAll();
		for(Road road : roads) {
			populateRoadCells(road);
		}
		
		return roads;		
	}

//	@Override we don't override getByKey() since we want to have 2 versions, one with cells, one w/o
	public Road getRoad(int roadId) throws IOException {
		Road road = super.getByKey(roadId);
		
		populateRoadCells(road);
		
		return road;
	}

	//populate cells on road
	private void populateRoadCells(Road road) throws IOException {
		List<RoadCell> cells = roadCellDao.getRoadCellsByRoadId(road.getRoadId());//cells can be null
		road.setRoadCells(cells); 	

		logger.info("cellsize={} road={}", cells==null?0:cells.size(), road);	
	}
	
	@Override
	public void close() {
		roadCellDao.close();
		super.close();
	}
}
