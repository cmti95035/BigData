package com.cmti.analytics.app.station.hbase.dao;

import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.hbase.dao.ExportDao;
import com.cmti.analytics.hbase.util.HBaseUtil;
import com.cmti.analytics.util.RandomGenInt;
import com.cmti.analytics.util.RandomGenLong;
import com.cmti.analytics.util.StringUtil;

/**
 * Dao for 'sig'
 * @author gmo
 *
 */
public class RecordSigDao extends ExportDao<RecordSig, Integer> {

	//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
	
	private static RecordSig mockup(){		
    	RandomGenInt eventTypeGen = new RandomGenInt(0, 4); //0-4
    	RandomGenInt imsiGen = new RandomGenInt(1000, 2000); //0-1000
    	RandomGenInt resultGen = new RandomGenInt(0, 1); //0-1
    	RandomGenInt cellGen = new RandomGenInt(0, 10); //0-10
    	
    	long current = System.currentTimeMillis();
    	
    	RandomGenLong dateGen = new RandomGenLong(current-90L*24L*3600000L, current); //last 90 days
    	
		RecordSig sig = new RecordSig();
		sig.setId(current);
		sig.setEventType(eventTypeGen.next());
		sig.setImsi(String.valueOf(imsiGen.next()));
		sig.setResult(resultGen.next());
		sig.setCell(cellGen.next());
		
		sig.setEventDate(new Date(dateGen.next()));
		
		return sig;
	}

	@Override
	public RecordSig parseLine(String line, Context context) {
		RecordSig sig = new RecordSig();
		
		StrTokenizer st = new StrTokenizer(line, ",");		
		st.setIgnoreEmptyTokens(false);
		
		String[] data = st.getTokenArray();		

//		logger.error("{} data.length   {}", data.length, line);//return 12 or 13, Hadoop removes the last ",", WTF
		if(data.length <18){
			logger.error("{} data.length < 18 {}", data.length, line);
			return null;
		}
		
		sig.setId(StringUtil.getLong(data[0]));
		
		if(sig.getId() == null){
			return null;
		}
		
		sig.setEventDate(StringUtil.getLong(data[1]));

		sig.setImsi(data[2]);
		sig.setSigSession(data[3]);
		sig.setEventType(StringUtil.getInt(data[4]));

		sig.setLac(StringUtil.getInt(data[5]));
		sig.setCell(StringUtil.getInt(data[6]));

		sig.setSubSession(StringUtil.getInt(data[7]));
		sig.setPcu(StringUtil.getInt(data[8]));
		sig.setRac(StringUtil.getInt(data[9]));
		sig.setResult(StringUtil.getInt(data[10]));
		sig.setFailReason(StringUtil.getInt(data[11]));
		sig.setSigDuration(StringUtil.getInt(data[12]));
		sig.setChannelType(StringUtil.getInt(data[13]));
		sig.setBusinessType(StringUtil.getInt(data[14]));
		sig.setApn(data[15]);
		sig.setApnIpv4(data[16]);
		sig.setHour(StringUtil.getInt(data[17])); 

		return sig;
	}
	
	@Override
	public Scan getExportScan(Configuration config){
		Scan scan = HBaseUtil.newOnePassMassScan();
		return scan;
	}
	
}
