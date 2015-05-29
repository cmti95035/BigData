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
 * @author gmo
 *
 */
public class RoadDao extends HBaseGenericDao<Road, Integer> {

	protected static final Logger logger = LogManager.getLogger(RoadDao.class);
	RoadCellDao roadCellDao;
	
	public RoadDao() throws IOException{
		roadCellDao = new RoadCellDao();
		roadCellDao.open();
	}
	
	public Road getRoad(int roadId) throws IOException {
		Road road = getByKey(roadId);
		
		//populate cells in road
		List<RoadCell> cells = roadCellDao.getRoadCellsByRoadId(road.getRoadId());//cells can be null
		road.setRoadCells(cells); 

		logger.error("roadId={} cellsize={} road={}", roadId, cells==null?0:cells.size(), road);
		
		return road;
	}

	@Override
	public void close() {
		roadCellDao.close();
		super.close();
	}
}
