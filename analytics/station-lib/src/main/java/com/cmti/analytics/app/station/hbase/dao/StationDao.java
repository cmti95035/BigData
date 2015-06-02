package com.cmti.analytics.app.station.hbase.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.cmti.analytics.app.station.hbase.domain.Station;
import com.cmti.analytics.hbase.dao.ExportDao;
import com.cmti.analytics.hbase.util.HBaseUtil;
import com.cmti.analytics.util.StringUtil;
 

/**
 * Dao for 'Station'
 * @author gmo
 *
 */
public class StationDao extends ExportDao<Station, Integer> {

	//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");

	@Override
	public Station parseLine(String line, Context context) {
		Station station = new Station();
		
		StrTokenizer st = new StrTokenizer(line, ",");		
		st.setIgnoreEmptyTokens(false);
		
		String[] data = st.getTokenArray();		

		logger.error("{} data.length   {}", data.length, line);//return 12 or 13, Hadoop removes the last ",", WTF
		if(data.length <12){
			logger.error("{} data.length < 12 {}", data.length, line);
			return null;
		}
		
		station.setId(StringUtil.getInt(data[0]));
		
		if(station.getId() == null){
			return null;
		}

		station.setBsc(data[2]);
		station.setCgi(data[3]);

		station.setLongitude(StringUtil.getDouble(data[4]));
		station.setLatitude(StringUtil.getDouble(data[5]));

		station.setType(data[6]);

		station.setLac(StringUtil.getInt(data[8]));
		station.setCi(StringUtil.getInt(data[9]));

		station.setAngle(StringUtil.getInt(data[10]));
		
		if("否".equals(data[11])){//FIXME this does not work.
			station.setRoom(false);
		}else if("是".equals(data[11])){
			station.setRoom(true);
		}else{
			logger.error(data[11]);
		}
			 
		return station;
	}
	
	@Override
	public Scan getExportScan(Configuration config){
		Scan scan = HBaseUtil.newOnePassMassScan();
		return scan;
	}
	
}
