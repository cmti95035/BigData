package com.cmti.analytics.app.tracking.task.scan.scanhandler;

import java.io.IOException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.configuration.Configuration;

import com.cmti.analytics.app.tracking.hbase.dao.RoadCellDao;
import com.cmti.analytics.app.tracking.hbase.dao.RoadDao;
import com.cmti.analytics.app.tracking.hbase.domain.RoadCell;
import com.cmti.analytics.app.tracking.hbase.domain.DriveTestData;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.task.scan.IHandler;

/**
 * scan RoadTestData of a road to create Road cells of that road
 * 
 * @author Guobiao Mo
 *
 */
public class DriveTestRoadCellHandler implements IHandler<DriveTestData>{

	protected static final Logger logger = LogManager.getLogger(DriveTestRoadCellHandler.class);
	int fuzzyLookAheadCell; 

	protected RoadCellDao roadCellDao;
	protected RoadDao roadDao;
	
	public DriveTestRoadCellHandler() {
		Configuration config = Config.getConfig();
		fuzzyLookAheadCell = config.getInt("fuzzy.look.ahead.cell", 10); //max no. of look ahead same cell for fuzzy zone. 

		logger.info("fuzzyLookAheadCell = {}", fuzzyLookAheadCell);

		roadCellDao = new RoadCellDao();
		try {
			roadCellDao.open();

			roadDao = new RoadDao();
			roadDao.open();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		saveAndClearRoad();
		roadCellDao.close();
		roadDao.close();
		logger.info("done");	
	}
	
	Integer roadId;
	RoadCell cell;
	//Double longitude, latitude;
	
	List<RoadCell> cells=new ArrayList<>();
	
	@Override 
	public void handle(DriveTestData roadTestData)  throws IOException{	//can handle multiple roads.
/*		Integer cellId = roadTestData.getCell();
		if(MrDao.cellList.contains(cellId)){
			logger.info("cellId {} in MrDao.cellList", cellId);			
		}else{
			logger.error("cellId {} not in MrDao.cellList!!!!!!!!!!!!!!!!!", cellId);				
		}
	*/
		
		if(cell==null){//brand new
			newRoadCell(roadTestData);
			return;
		}

		Integer lastRoadId = cell.getRoadId();
		Integer thisRoadId = roadTestData.getRoadId();
		
		if(thisRoadId.equals(lastRoadId) == false){//save old and create a new road. for now, multiple roads case is not tested.
			logger.info("thisRoadId {}!= lastRoadId {}",thisRoadId, lastRoadId);
			//output cells in the previous road, 
			saveAndClearRoad();
			newRoadCell(roadTestData);
			return;
		}
		
		if(cell.getCellId().equals(roadTestData.getCell())){
			//in the same cell, add lon and lat if not present
			cell.appendDriveTestData(roadTestData);
		}else{	//new cell
			newRoadCell(roadTestData);
			return;
		}
	}
	
	private void newRoadCell(DriveTestData roadTestData) {
		cell = new RoadCell();
		cell.setCellId(roadTestData.getCell());
		cell.setRoadId(roadTestData.getRoadId());
		cell.setDriveTestId(roadTestData.getDriveTestId());
		// add lat lon 
		cell.appendDriveTestData(roadTestData);
		cells.add(cell);
	}

	//do 2 extra things besides saving:
	//1. set order
	//2. set fuzzy
	//calculate distance
	//combine ender starter and their lon lat
	
	//TODO drive test car may ran overlap some road segment for a loop? 
	private void saveAndClearRoad() throws IOException {//all cells in cells should have the same road id
		
		//for a loop, combine starter and ender if they are the same cell.
		RoadCell cell0 = cells.get(0);
		
	//	int roadId = cell0.getRoadId();
		//Road road = roadDao.getByKey(roadId);
		
		//logger.info("road {}", road);
		//FIXME based on road.loop to ....		
		
		RoadCell cellLast = cells.get(cells.size()-1);
		if(cell0.getCellId().equals(cell.getCellId())) {
			cell0.getFrameList().addAll(0, cellLast.getFrameList());
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
				RoadCell cellj = cells.get(j);
				if(j-i< fuzzyLookAheadCell && cellj.getCellId()==cellId) {
					//if(tmpCell.getCellId().equals(cellId) && j-i<len/2){ cd = 9607 has issue, cell 9607 appears far away					
//					logger.error("apart={} {} {} same cell id, mark all between fuzzy = true", j-i, cell, tmpCell);
					
					Integer fuzzyId = cell.getFuzzyId();//if cell is already in a fuzzy zone, 
					if(fuzzyId==null) {
						fuzzyId = cell.getOrder();//if not, cell is the start of new fuzzy zone
					}
					
					//everything in between belongs to the fuzzy zone
					for(int k= i; k<=j; k++) {
						RoadCell fuzzyCell = cells.get(k);
						fuzzyCell.setFuzzyId(fuzzyId);
					}
					break;
				}
			}

			try {
				roadCellDao.insert(cell);
				logger.info("inserted {}", cell);
			} catch (InterruptedException | IOException e) {
				logger.error(cell, e);
			}
		}

		cells.clear();
	}
}
