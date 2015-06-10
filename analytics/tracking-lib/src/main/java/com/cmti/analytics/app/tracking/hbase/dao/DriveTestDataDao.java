package com.cmti.analytics.app.tracking.hbase.dao;
 
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.cmti.analytics.app.tracking.hbase.domain.MrOnRoad; 
import com.cmti.analytics.app.tracking.hbase.domain.DriveTestData;
import com.cmti.analytics.app.tracking.util.TrackingUtil;
import com.cmti.analytics.hbase.dao.ExportDao;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;
import com.cmti.analytics.hbase.util.HBaseUtil;
import com.cmti.analytics.util.StringUtil;
 

/**
 * Dao for 'RoadTestData'
 * @author Guobiao Mo
 *
 */
public class DriveTestDataDao extends ExportDao<DriveTestData, Object> {

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//2014/9/28 16:06:28

	//used by bulk loader, set hbase timestamp as RoadTestData time
	@Override
	public Put getPut(DriveTestData t) throws IOException, InterruptedException {
		return getPut(t, t.getTime().getTime());
	}
	
	@Override
//example line:
//14085,2014/9/28 16:16:13,103.995804,30.633093,30067,10104,32,75,-66,87,-71,91,-87,18,-89,120,-91,53,-91,83,-93,100,-95,45,-97

	public DriveTestData parseLine(String line, Context context) {
		DriveTestData driveTestData = new DriveTestData();
		
		StrTokenizer st = new StrTokenizer(line, ",");
		st.setIgnoreEmptyTokens(false);

		String[] data = st.getTokenArray();

		//logger.error("{} data.length   {}", data.length, line);//
		if(data.length < 25){
			logger.error("{} data.length < 25 {}", data.length, line);
			return null;
		}

		Integer cell = StringUtil.getInt(data[4]);
		if(cell==null) {
			logger.error("length={} cell==null {}", data.length, line);
			return null;
		}

		Double longitude = StringUtil.getDouble(data[2]);
		if(longitude==null) {
			logger.error("length={} longitude==null {}", data.length, line);
			return null;
		}

		Double latitude = StringUtil.getDouble(data[3]);
		
		
		Integer frame = StringUtil.getInt(data[0]);
		if(frame==null) {
			logger.error("length={} frame==null {}", data.length, line);
			return null;
		}

		Date date;
		try {
			date = dateFormat.parse(data[1]);
		} catch (ParseException e) {
			logger.error("data[1]={} line={}", data[1], line);
			return null;
		}

		Integer rscp = StringUtil.getInt(data[8]);

		String fileName =  ((FileSplit) context.getInputSplit()).getPath().getName();//use this as road test id 0102885120140928160610ms9.csv
		int roadId = TrackingUtil.extractRoadIdFromRoadMeasureId(fileName);

		driveTestData.setRoadId(roadId);
		driveTestData.setDriveTestId(fileName);
		driveTestData.setLatitude(latitude);
		driveTestData.setLongitude(longitude);
		driveTestData.setFrame(frame);
		driveTestData.setTime(date);				
		driveTestData.setCell(cell);
		driveTestData.setRscp(rscp);

		return driveTestData;
	}
	
}
