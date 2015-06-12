package com.cmti.analytics.app.tracking.task.mapreduce.mr.mrhandler;

import static com.cmti.analytics.app.tracking.task.mapreduce.mr.SignatureConstant.ROAD_TEST;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.MrOnRoadDao;
import com.cmti.analytics.app.tracking.hbase.dao.RoadDao;
import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.app.tracking.hbase.domain.MrOnRoad;
import com.cmti.analytics.app.tracking.hbase.domain.Road;
import com.cmti.analytics.app.tracking.hbase.domain.RoadCell;
import com.cmti.analytics.app.tracking.hbase.domain.UserCell;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.task.mapreduce.RelayCombineMRHandler;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;

/**
 * 

 * @author Guobiao Mo
 *
 */
public class TrackingMRHandler extends RelayCombineMRHandler<Mr> {

	protected static final Logger logger = LogManager.getLogger(TrackingMRHandler.class);
	//public static final String SIGNATURE = COUNT;

	RoadDao roadDao;
	MrOnRoadDao mrOnRoadDao;
	
	List<Road> allRoads;
	
	//protected RoadTestService roadTestService;

	@Override
	public TrackingMRHandler initMap() throws IOException, InterruptedException {
//		ApplicationContext springContext = SpringUtil.getApplicationContext();	
//		osVersionService = springContext.getBean("osVersionService", OsVersionService.class);

		RoadDao tmpRoadDao = new RoadDao();
		tmpRoadDao.open();
		
		allRoads = tmpRoadDao.getAllRoads();	
		
		tmpRoadDao.close();
		
		return this;
	}
	

	protected org.apache.commons.configuration.Configuration config = Config.getConfig();
	int minMatch = config.getInt("match.cell.min", 3); //minimal of matching cell
	
	@Override
	public TrackingMRHandler initReduce() throws IOException, InterruptedException {
		roadDao = new RoadDao();
		roadDao.open();
			
		mrOnRoadDao = new MrOnRoadDao();
		mrOnRoadDao.open();
		
		return this;
	}

	@Override
	public void close() throws IOException {
		super.close();
		if(roadDao != null) {//roadDao is null for Reduce phase
			roadDao.close();
		}

		if(mrOnRoadDao != null) {//mrOnRoadDao is null for Reduce phase
			mrOnRoadDao.close();
		}
	}
	
	@Override
	public void doMap(Mr mr, org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {
		Date date = mr.getTime();
		int cellId = mr.getCellId(); 
		long imsi = mr.getImsi();
		Integer rscp = mr.getRscp();

		if(rscp == null){
			logger.error("rscp == null in "+mr);
			return;
		}
		
		//loop all roads, if a cell is in the road, 
		for(Road road :allRoads){
			List<RoadCell> cells = road.getRoadCells();
			if(cells == null) {
				continue;
			}

			for(RoadCell cell : cells) {
				if(cellId == cell.getCellId().intValue()){
					int roadId = road.getRoadId(); 
					String keyStr = MRUtil.buildKey(getSignature(), roadId, imsi); 
				
					key.set(keyStr);
					value.set(date, cellId, rscp);
					context.write(key, value);
				}
			}
		}		
	}

	@Override
	public void doReduce(Text keyText, Iterable<StringArrayWritable> ivalues, Context context) throws IOException, InterruptedException {
		String keyStr = keyText.toString();
		if(keyStr.startsWith(getSignature()) == false) {
			return;
		}

		String[] keys = MRUtil.parseKey(keyStr);
		int roadId = Integer.parseInt(keys[1]);
		long imsi = Long.parseLong(keys[2]);

		List<Mr> mrs = new ArrayList<Mr>();//store Mr from ivalues.
		HashSet<Integer> cells = new HashSet<Integer>();//store unique cells
		
		Iterator<StringArrayWritable> it=ivalues.iterator();
		
		//add all MRs into a sorted set
		while(it.hasNext()){
			StringArrayWritable array = it.next();
			String[] value = array.toStrings();

			long timeStamp = Long.parseLong(value[0]);
			Integer cellId = Integer.parseInt(value[1]);
			Integer power = Integer.parseInt(value[2]);
			Mr mr = new Mr();
			mr.setImsi(imsi);
			mr.setCellId(cellId);
			mr.setTime(timeStamp);
			mr.setRscp(power);
			
			mrs.add(mr);
			cells.add(cellId);
		}
		
		//now we have a set of ordered MRs. and unique cells on the road
		if(cells.size() >= minMatch) {//minimal of matching unique cell ids
			logger.info("imsi={}, cell size={}", imsi, cells.size());
			Collections.sort(mrs);//sorted (by time)
			doMrList(roadId, mrs);
			
			Collections.sort(mrs, Collections.reverseOrder());//reverse sorted (by time), i.e. other direction
			doMrList(roadId, mrs);
		}
	}

	private void doMrList(int roadId, List<Mr> mrs) throws IOException, InterruptedException {
		logger.info("mr-size={}", mrs.size());
		for(Mr mr : mrs){
			logger.info(mr);
		}
		List<MrOnRoad> mors = findMrOnRoad(mrs, roadId);
		
		//save MrOnRoad to HBase 
		for(MrOnRoad mor : mors) {
			logger.info("mrOnRoadDao.upsert(mor, mor.getTime()) {}", mor);
			mrOnRoadDao.insert(mor, mor.getTime());
		}		
	}
	
	//find mr on road
	private List<MrOnRoad> findMrOnRoad(List<Mr> mrList, int roadId) throws IOException {
		//if a user passes cells in the order of that of a road test (or reserves), we know that the user is on the road
		//for each Mr, convert it to MrOnRoad. based on the timestamps of entering, exiting of the cell and the mr timestamp, and road's cell gps data.
		//we can determine the location of the mr.
						
		List<UserCell> cells = new ArrayList<UserCell>();
		
		//convert mrList to a list of UserCell.
		for(int i = 0; i < mrList.size(); i++) {
			Mr mr = mrList.get(i);
			int cellId = mr.getCellId();
			Date date = mr.getTime();
			
			//last added cell
			UserCell lastCell = cells.size()>0?cells.get(cells.size()-1):null;
			
			if(lastCell == null || lastCell.getCellId() != cellId) {//init cell or a new cell
				lastCell = new UserCell();
				lastCell.setCellId(cellId);
				lastCell.setStartId(i);
				lastCell.setEndId(i);
				lastCell.setEnteringDate(date);
				cells.add(lastCell);
			} else {
				lastCell.setEndId(i);
				lastCell.setExitingDate(date);
			}
		}
		
		for(UserCell cell :cells) {
			logger.info(cell);
		}
		
		Road road = roadDao.getRoad(roadId);//both road and road_cell tables are in-memory, still need to cache it TODO

		//check the list of UserCell, 
		//1. continue cells on the road (match that of road test)
		//2. speed > 40 km/h
		List<UserCell> resultCells = road.findMatchCell(cells, minMatch);
		
		//3. convert Mr in resultCells to MrOnRoad
		List<MrOnRoad> ret = new ArrayList<MrOnRoad>();
		
		for(UserCell cell : resultCells) {
			int iStart = cell.getStartId();
			int iEnd= cell.getEndId();

			logger.info("loop ucell={} iStart={} iEnd={}", cell, iStart, iEnd);
			for(int i=iStart; i<=iEnd; i++){
				Mr mr = mrList.get(i);
				MrOnRoad mrOnRoad = new MrOnRoad(roadId, cell, mr);
				ret.add(mrOnRoad);

				logger.info("add mrOnRoad={}  ", mrOnRoad);
			}
		}

		logger.info("mrOnRoad size{}  ", ret.size());
		return ret;
	}

	@Override
	protected String getSignature() {
		return ROAD_TEST;
	}

}
