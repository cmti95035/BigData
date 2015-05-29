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
import com.cmti.analytics.app.tracking.hbase.domain.UserCell;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.task.mapreduce.BaseMRHandler;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;

/**
 * 
key: 
value: sum user count

 * @author gmo
 *
 */
public class TrackingMRHandler extends BaseMRHandler<Mr> {

	protected static final Logger logger = LogManager.getLogger(TrackingMRHandler.class);
	//public static final String SIGNATURE = COUNT;

	RoadDao roadDao;
	MrOnRoadDao mrOnRoadDao;
	
	//protected RoadTestService roadTestService;
/*
	@Override
	public TrackingMRHandler initMap(){
		ApplicationContext springContext = SpringUtil.getApplicationContext();	
//		osVersionService = springContext.getBean("osVersionService", OsVersionService.class);		
		return this;
	}
	*/

	protected org.apache.commons.configuration.Configuration config = Config.getConfig();
	int minMatch = config.getInt("match.cell.min", 3); //minimal of matching cell
	
	@Override
	public TrackingMRHandler initReduce() {
		try {
			roadDao = new RoadDao();
			roadDao.open();
			
			mrOnRoadDao = new MrOnRoadDao();
			mrOnRoadDao.open();
		} catch (IOException e) {
			logger.error(roadDao, e);
		}
		return this;
	}

	@Override
	public void close() throws IOException {
		super.close();
		if(roadDao != null) {//roadDao is null for Map phase
			roadDao.close();
		}

		if(mrOnRoadDao != null) {//roadDao is null for Map phase
			mrOnRoadDao.close();
		}
	}
	
	@Override
	public void doMap(Mr mr, org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException {
		Date date = mr.getTime();
		int cell = mr.getCell(); 
		long imsi = mr.getImsi();
		Integer rscp = mr.getRscp();

		if(rscp == null){
			logger.error("rscp == null in "+mr);
			return;
		}
		
		//loop all roads, if a cell is in the road, 
		//for(Road road :allRoads){
			//if(road.getCells().contains(cell)){

				int roadId = 1028851;//road.getRoadId(); FIXME
//				String keyStr = MRUtil.buildKey(getSignature(), periodType, eventTypeId, dateKey, groupType);
				String keyStr = MRUtil.buildKey(roadId, imsi);//for now, don't use  Signature
				
				key.set(keyStr);
				value.set(date, cell, rscp);
				context.write(key, value);
			
			//}
		//}		
	}

	@Override
	public void doReduce(Text keyText, Iterable<StringArrayWritable> ivalues, Context context) throws IOException, InterruptedException {
		String[] keys = MRUtil.parseKey(keyText);
		int roadId = Integer.parseInt(keys[0]);  
		long imsi = Long.parseLong(keys[1]);  

		TreeSet<Mr> mrs = new TreeSet<Mr>();//store sorted (by time) Mr in ivalues.
		HashSet<Integer> cells = new HashSet<Integer>();//store unique cells
		
		Iterator<StringArrayWritable> it=ivalues.iterator();
		
		//add all MRs into a sorted set
		while(it.hasNext()){
			StringArrayWritable array = it.next();
			String[] value = array.toStrings();

			long timeStamp = Long.parseLong(value[0]);
			Integer cell = Integer.parseInt(value[1]);
			Integer power = Integer.parseInt(value[2]);
			Mr mr = new Mr();
			mr.setImsi(imsi);
			mr.setCell(cell);
			mr.setTime(timeStamp);
			mr.setRscp(power);
			
			mrs.add(mr);
			cells.add(cell);
		}
		
		//now we have a set of ordered Mr's. and unique cells on the road
		if(cells.size() >= minMatch) {//minimal of matching cell
			logger.info(imsi +" mr-size="+mrs.size()+" cell size="+cells.size());
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
	}

	//find mr on road
	private List<MrOnRoad> findMrOnRoad(TreeSet<Mr> mrs, int roadId) throws IOException {
		//if a user passes cells in the order of that of a road test (or reserves), we know that the user is on the road
		//for each Mr, convert it to MrOnRoad. based on the timestamps of entering, exiting of the cell and the mr timestamp, and road's cell gps data.
		//we can determine the location of the mr.
		
		//convert ordered Set mrs to a List to have index
		List<Mr> mrList = new ArrayList<Mr>(mrs);
				
		List<UserCell> cells = new ArrayList<UserCell>();
		
		//convert mrList to a list of UserCell.
		for(int i = 0; i < mrList.size(); i++) {
			Mr mr = mrList.get(i);
			int cellId = mr.getCell();
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
			logger.error(cell);
		}
		
		Road road = roadDao.getRoad(roadId);//both road and road_cell tables are in-memory, no need to cache it

		//check the list of UserCell, 
		//1. continue cells on the road (match that of road test)
		//2. speed > 40 km/h
		List<UserCell> resultCells = road.findMatchCell(cells, minMatch);
		
		//3. convert Mr in resultCells to MrOnRoad
		List<MrOnRoad> ret = new ArrayList<MrOnRoad>();
		
		for(UserCell cell : resultCells) {
			int iStart = cell.getStartId();
			int iEnd= cell.getEndId();

			logger.error("loop ucell={} iStart={} iEnd={}", cell, iStart, iEnd);
			for(int i=iStart; i<=iEnd; i++){
				Mr mr = mrList.get(i);
				MrOnRoad mrOnRoad = new MrOnRoad(roadId, cell, mr);
				ret.add(mrOnRoad);

				logger.error("add mrOnRoad={}  ", mrOnRoad);
			}
		}

		logger.error("mrOnRoad size{}  ", ret.size());
		return ret;
	}

	@Override
	protected String getSignature() {
		return ROAD_TEST;
	}

}
