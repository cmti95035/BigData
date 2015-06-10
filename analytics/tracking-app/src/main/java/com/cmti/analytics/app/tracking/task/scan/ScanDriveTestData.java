package com.cmti.analytics.app.tracking.task.scan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.DriveTestDataDao;
import com.cmti.analytics.app.tracking.hbase.domain.DriveTestData;
import com.cmti.analytics.app.tracking.task.scan.scanhandler.DriveTestRoadCellHandler;
import com.cmti.analytics.hbase.dao.DaoScanner;
import com.cmti.analytics.hbase.dao.IDaoScanner;
import com.cmti.analytics.hbase.task.scan.IHandler;
import com.cmti.analytics.hbase.task.scan.ScanTable;
import com.cmti.analytics.hbase.util.HBaseUtil;
 

/**
 * scan RoadTestData table and populate RoadCell table (in RoadTestRoadCellHandler)
 * java -cp tracking-app-1.0-SNAPSHOT.jar -Dsite=gmo -Dlog4j.configurationFile=log4j2/log4j2_prod.xml com.cmti.analytics.app.tracking.task.scan.ScanRoadTestData
 * 
 * AWS
 cd ~/gmo
 java -cp tracking-app-1.0-SNAPSHOT.jar:/home/hadoop/lib/hbase.jar -Djava.ext.dirs=/home/hadoop/lib/lib -Dsite=aws -Dlog4j.configurationFile=log4j2/log4j2_prod.xml com.cmti.analytics.app.tracking.task.scan.ScanRoadTestData
  
 * log: /tracking-app/src/main/my_note/ScanRoadTestData.log
 * 
 * @author Guobiao Mo
 *
 */
public class ScanDriveTestData extends ScanTable<DriveTestData> {
	protected static final Logger logger = LogManager.getLogger(ScanDriveTestData.class); 
	
	public ScanDriveTestData() throws IOException {
	}

	@Override
	protected List<IHandler<DriveTestData>> getHandlers(){
		ArrayList<IHandler<DriveTestData>> ret = new ArrayList<IHandler<DriveTestData>>();
		ret.add(new DriveTestRoadCellHandler());
		return ret;
	}
	
	@Override
	protected IDaoScanner<DriveTestData> getDaoScanner() throws IOException{
		Scan scan = HBaseUtil.newOnePassMassScan();
		
		DriveTestDataDao dao = new DriveTestDataDao();
		dao.open();
		
		DaoScanner<DriveTestData> scanner = dao.getDaoScanner(scan);
		
		return scanner;
	}
	
	public static void main(String args[]) throws Exception {
		ScanDriveTestData er = new ScanDriveTestData();
		er.scan();
		er.close();
	}
}