package com.cmti.analytics.app.tracking.task.mapreduce.mr.mrhandler;

import static com.cmti.analytics.app.tracking.task.mapreduce.mr.SignatureConstant.ROAD_TEST;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.DriveTestDataDao;
import com.cmti.analytics.app.tracking.hbase.dao.RoadDao;
import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.app.tracking.hbase.domain.Road;
import com.cmti.analytics.app.tracking.hbase.domain.RoadCell;
import com.cmti.analytics.app.tracking.hbase.domain.UserCell;
import com.cmti.analytics.app.tracking.hbase.domain.VirtualRoadCell;
import com.cmti.analytics.app.tracking.util.TrackingUtil;
import com.cmti.analytics.conf.Config;
import com.cmti.analytics.hbase.task.mapreduce.BaseMRHandler;
import com.cmti.analytics.hbase.task.mapreduce.util.MRUtil;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
import com.cmti.analytics.util.GeoUtil;

/**
 * 
 * @author Guobiao Mo
 *
 */
public class TrackingMRHandler extends BaseMRHandler<Mr> {
	//public class TrackingMRHandler extends RelayCombineMRHandler<Mr> {
	protected static final Logger logger = LogManager.getLogger(TrackingMRHandler.class);

	RoadDao roadDao;
//	MrOnRoadDao mrOnRoadDao;
	DriveTestDataDao driveTestDataDao;
	
	List<Road> allRoads;
	int minMatch;
	double minSpeed;
	
	//protected RoadTestService roadTestService;

	@Override
	public TrackingMRHandler initMap() throws IOException, InterruptedException {
		RoadDao tmpRoadDao = new RoadDao();
		tmpRoadDao.open();
		
		allRoads = tmpRoadDao.getAllRoads();
		logger.info("Total road size={}", allRoads.size());
		for(Road road : allRoads) {
			logger.info(road);
		}
		
		tmpRoadDao.close();
		
		return this;
	}
	
	@Override
	public TrackingMRHandler initReduce() throws IOException, InterruptedException {
		org.apache.commons.configuration.Configuration config = Config.getConfig();
		minMatch = config.getInt("match.cell.min", 3); //minimal of matching cell
		minSpeed = config.getDouble("match.speed.min", 40.); //minimal of matching cell
		
		roadDao = new RoadDao();
		roadDao.open();
					
		driveTestDataDao = new DriveTestDataDao();
		driveTestDataDao.open();		
		
		return this;
	}

	@Override
	public void close() throws IOException {
		super.close();
		if(roadDao != null) {//roadDao is null for Reduce phase
			roadDao.close();
		}
		
		if(driveTestDataDao != null) {//mrOnRoadDao is null for Reduce phase
			driveTestDataDao.close();
		}
	}
	
	@Override
	public void doMap(Mr mr, Mapper<ImmutableBytesWritable, Result, Text, StringArrayWritable>.Context context) throws IOException, InterruptedException {
		Date date = mr.getTime();
		int cellId = mr.getCellId(); 
		long imsi = mr.getImsi();
		Integer rscp = mr.getRscp();

		if(rscp == null){
			logger.error("rscp == null in {}", mr);
			return;
		}
		
		//loop all roads, check if the cell of the MR is on any of them, 
		for(Road road : allRoads){
			List<RoadCell> roadCells = road.getRoadCells();
			if(roadCells == null) {
				continue;
			}

			for(RoadCell roadCell : roadCells) {
				if(cellId == roadCell.getCellId().intValue()){
					int roadId = road.getRoadId(); 
					String keyStr = MRUtil.buildKey(getSignature(), roadId, imsi); 
				
					key.set(keyStr);
					value.set(date, cellId, rscp);
					context.write(key, value);
					break;
				}
			}
		}		
	}

	@Override
	public void doReduce(Text keyText, Iterable<StringArrayWritable> ivalues, Reducer<Text, StringArrayWritable, ImmutableBytesWritable, Mutation>.Context context) throws IOException, InterruptedException {
		String keyStr = keyText.toString();
		if(keyStr.startsWith(getSignature()) == false) {
			return;
		}

		String[] keys = MRUtil.parseKey(keyStr);
		int roadId = Integer.parseInt(keys[1]);
		long imsi = Long.parseLong(keys[2]);

		List<Mr> mrs = new ArrayList<>();//store MRs from ivalues.
		HashSet<Integer> cells = new HashSet<>();//store unique cell ids
		
		Iterator<StringArrayWritable> it=ivalues.iterator();
		
		//add all MRs into mrs, and store unique cell IDs in cells
		while(it.hasNext()){
			StringArrayWritable array = it.next();
			String[] value = array.toStrings();

			long timeStamp = Long.parseLong(value[0]);
			Integer cellId = Integer.parseInt(value[1]);
			Integer rscp = Integer.parseInt(value[2]);
			Mr mr = new Mr();
			mr.setImsi(imsi);
			mr.setCellId(cellId);
			mr.setTime(timeStamp);
			mr.setRscp(rscp);
			
			mrs.add(mr);
			cells.add(cellId);
		}
		
		//now we have a list of MRs. and unique cells on the road
		logger.info("imsi={}, cell size={}", imsi, cells.size());
		if(cells.size() >= minMatch) {//minimal of matching unique cell ids
			Collections.sort(mrs);//sorted (by time)
			doMrList(roadId, mrs);
			
			Collections.sort(mrs, Collections.reverseOrder());//reverse sorted (by time), i.e. other road direction
			doMrList(roadId, mrs);
		}
	}

	private void doMrList(int roadId, List<Mr> mrList) throws IOException, InterruptedException {
		logger.info("starting doMrList(int roadId, List<Mr> mrs), mr-size={}", mrList.size());
		for(Mr mr : mrList){
			logger.debug(mr);
		}

		//if a user passes cells in the order of that of a road test (or reserves), we know that the user is on the road
		//for each Mr, convert it to MrOnRoad. based on the timestamps of entering, exiting of the cell and the mr timestamp, and road's cell gps data.
		//we can determine the location of the mr.
						
		List<UserCell> userCells = new ArrayList<>();
		
		//convert mrList to a list of UserCell.		
		UserCell lastUserCell = null;//last added cell
		for(int i = 0; i < mrList.size(); i++) {
			Mr mr = mrList.get(i);
			int cellId = mr.getCellId();
						
			if(lastUserCell == null || lastUserCell.getCellId() != cellId) {//init cell or a new cell
				lastUserCell = new UserCell();
				lastUserCell.setCellId(cellId);
				userCells.add(lastUserCell);
			}
			
			lastUserCell.addMr(mr);
		}

		logger.info("Got UserCells, size={}", userCells.size());
		for(UserCell cell : userCells) {
			logger.debug(cell);
		}
		logger.info("Going to see any portion of it matches road cell string");
		
		List<VirtualRoadCell> blockVRoadCells = new ArrayList<>();

		VirtualRoadCell lastMatchVRoadCell = null; //last matched virtual roadCell 
		Road road = roadDao.getRoad(roadId);//should cache it TODO 
		
		for (int i = 0; i < userCells.size(); i++) {
			UserCell ucell = userCells.get(i);

			logger.info("i={}, lastMatchVRoadCell={}, ucell={}", i, lastMatchVRoadCell, ucell);
			
			if (lastMatchVRoadCell == null) {//new life
				lastMatchVRoadCell = newLife(road, ucell, blockVRoadCells); 
				continue;
			}
			
			if(lastMatchVRoadCell.covers(ucell)) {
				lastMatchVRoadCell.addUserCell(ucell);
				continue;
			}
			
			//if lastMatchVRoadCell and next are in 2 separated sessions, we can not tell. FIXME
			VirtualRoadCell next = lastMatchVRoadCell.getNext();
			if(next == null) {//not apply for loop
				finishBlock(blockVRoadCells);
				lastMatchVRoadCell = newLife(road, ucell, blockVRoadCells); 
				continue;
			}

			if(next.covers(ucell)) {
				//TODO potentially, this could be a new life if lastMatchVRoadCell and next are in 2 separated user sessions
				lastMatchVRoadCell = next;
				lastMatchVRoadCell.addUserCell(ucell);
				blockVRoadCells.add(lastMatchVRoadCell);
				logger.info("found match, ucell={}, blockVRoadCells={}", ucell, blockVRoadCells);
				continue;
			}else{
				logger.info("block end here by ucell={}, blockVRoadCells={}", ucell, blockVRoadCells);
				finishBlock(blockVRoadCells);
				lastMatchVRoadCell = newLife(road, ucell, blockVRoadCells); 
				continue;
			}
		}
		
		finishBlock(blockVRoadCells);
	}
	
	private VirtualRoadCell newLife(Road road, UserCell ucell, List<VirtualRoadCell> blockVRoadCells) {
		VirtualRoadCell virtualRoadCell = road.findVirtualRoadCell(ucell); //ret should not be null
		virtualRoadCell.addUserCell(ucell);
		logger.info("start a new block,  lastMatchRoadCellId={}", virtualRoadCell);
		blockVRoadCells.add(virtualRoadCell); 
		
		return virtualRoadCell;
	}

	private void finishBlock(List<VirtualRoadCell> blockVRoadCells) throws IOException, InterruptedException {
		if (blockVRoadCells.size() >= minMatch) {
			double speed = speed(blockVRoadCells);
			if(speed >= minSpeed){
				logger.info("got blockVRoadCells.size()={}, speed={} add them to HBase", blockVRoadCells.size(), speed);
				insert(blockVRoadCells);
			}else{
				logger.info("skip speed = {} blockVRoadCells.size()={}", speed, blockVRoadCells.size());				
			}
		}else{
			logger.info("got blockVRoadCells.size()={}, skip them", blockVRoadCells.size());
		}
		clear(blockVRoadCells);
	}

	private void clear(List<VirtualRoadCell> blockVRoadCells) {
		for(VirtualRoadCell virtualRoadCell : blockVRoadCells) {
			virtualRoadCell.clearUserCell();
		}		
		blockVRoadCells.clear();
	}

	private double speed(List<VirtualRoadCell> blockVRoadCells) throws IOException, InterruptedException {
		int len = blockVRoadCells.size();
		VirtualRoadCell virtualRoadCell_1 = blockVRoadCells.get(1);
		Date startDate = virtualRoadCell_1.getUserCells().get(0).getMrList().get(0).getTime();
		
		VirtualRoadCell virtualRoadCell_last = blockVRoadCells.get(len-1);
		Date endDate = virtualRoadCell_last.getUserCells().get(0).getMrList().get(0).getTime();
		
		long time = Math.abs(endDate.getTime()-startDate.getTime());//for bi-direction match, one of which is negative
		
		List<Double> longitudeList = new ArrayList<>();
		List<Double> latitudeList = new ArrayList<>();
		
		for(int i=1; i<len-1; i++) {//cut head and tail
			VirtualRoadCell virtualRoadCell = blockVRoadCells.get(i);
			List<RoadCell> roadCells = virtualRoadCell.getRoadCells();
			for(RoadCell roadCell : roadCells){
				longitudeList.addAll(roadCell.getLongitudeList());
				latitudeList.addAll(roadCell.getLatitudeList());
			}
		}	
		
		double distance = GeoUtil.distance(latitudeList, longitudeList);

		double speed = distance*3600000./time;//return speed in KM/hr
		logger.debug("distance={}KM, time={}ms, speed={} KM/hr", distance, time, speed);
		
		return speed;
	}

	private void insert(List<VirtualRoadCell> blockVRoadCells) throws IOException, InterruptedException {
		int len = blockVRoadCells.size();
		for(int i=1; i<len-1; i++) {//cut head and tail
			VirtualRoadCell virtualRoadCell = blockVRoadCells.get(i);
			projectMrToDriveTestData(virtualRoadCell);
		}	
	}

	private void projectMrToDriveTestData(VirtualRoadCell virtualRoadCell)
			throws IOException, InterruptedException {
		List<Integer> driveTestDataFrameIds = virtualRoadCell.getAllDriveTestDataFrameIds();
		List<Mr> mrs = virtualRoadCell.getAllMrs();

		int m = driveTestDataFrameIds.size();
		int n = mrs.size();

		for (int i = 0; i < mrs.size(); i++) {
			Mr mr = mrs.get(i);
			int j = TrackingUtil.getProjectIndex(m, n, i);
			int driveTestDataFrameId = driveTestDataFrameIds.get(j);

			driveTestDataDao.addMr(virtualRoadCell.getRoadId(), virtualRoadCell.getDriveTestId(), driveTestDataFrameId, mr);
		}
	}

	@Override
	protected String getSignature() {
		return ROAD_TEST;
	}

}
