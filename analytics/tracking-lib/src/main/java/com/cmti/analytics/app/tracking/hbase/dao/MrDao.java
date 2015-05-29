package com.cmti.analytics.app.tracking.hbase.dao;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration; 
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.app.tracking.hbase.domain.RoadTestData;
import com.cmti.analytics.hbase.dao.ExportDao;
import com.cmti.analytics.hbase.util.HBaseUtil;
import com.cmti.analytics.util.StringUtil;
 

/**
 * Dao for 'MR'
 * @author gmo
 *
 */
public class MrDao extends ExportDao<Mr, Object> {

	//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); for parseLine2
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static List<Integer> cellList=Arrays.asList(
		61027
,61028
,15027
,35737
,15029
,35739
,41059
,9727
,57289
,40807
,40809
,52257
,40808
,52259
,3848
,42117
,42118
,62917
,62918
,60889
,60887
,31769
,31767
,31768
,53077
,30068
,30067
,40057
,40058
,40139
,40137
,1789
,41689
,6857
,37398
,31579
,31577
,8758
,2457
,34667
,2459
,2458
,41878
,41877
,3249
,3247
,4479
,469
,467
,58679
,2950
,2958
,31319
,31317
,40698
,497
,55788
,48547
,498
,48588
,48367
,2079
,15127
,61569
,61567
,12708
,12709
,12707
,1448
,9609
,9608
,9607
,12719
,12718
,12717
,29478
,29477
,52958
,52959
,13478
,13477
,62037
,62038
,4387
,61938
,61937
,60188
,5558
,5557
,42057
,49119
,51269
,51267
,7838
,7839
,6567
,6568
,61989
,54378
,54377
,2769
,51108
,2757
,2759
,31617
,49507
,31619
,31897
,31899
,49438
,49439
,40797
,40799
,49467
,49468
,49469
,41147
,41148
,41149
,4957
,4959
,63717
,29329
,58209
,58407
,40788
,40789
,60987
,60988
,6047
,1777
,30927
,1778
,30928
,55798
,28519
,55799
,5229
,37018
,41929
,42127
,42128
,62698
,62699
,1867
,1869
,61719);
	Set<Integer> cellSet = new HashSet<Integer>(cellList);//for fast search
	

	//used by bulk loader, set hbase timestamp as Mr time
	@Override
	public Put getPut(Mr t) throws IOException, InterruptedException {
		return getPut(t, t.getTime().getTime());
	}

	
	@Override
	public Mr parseLine(String line, Context context) {
		Mr mr = new Mr();
		
		StrTokenizer st = new StrTokenizer(line, ",");
		st.setIgnoreEmptyTokens(false);
		
		String[] data = st.getTokenArray();		

//		logger.error("{} data.length   {}", data.length, line);//return 12 or 13, Hadoop removes the last ","
		if(data.length < 11){
			logger.error("{} data.length < 11 {}", data.length, line);
			return null;
		}

		int cell = StringUtil.getInt(data[2]);
		
		if(!cellSet.contains(cell)){//TODO should load all MR
		//	logger.error("{} not in", cell);
			return null;
		}else{
			//logger.error("{} in", cell);
		}
		mr.setCell(cell);
		
		
		long imsi = StringUtil.getLong(data[1]);
		mr.setImsi(imsi);

		try {
			mr.setTime(dateFormat.parse(data[6]));
		} catch (ParseException e) {
			logger.error("SimpleDateFormat ParseException {}", line);
		}

		mr.setRscp(StringUtil.getInt(data[data.length-1]));
			 
		return mr;
	}

	public Mr parseLine2(String line) {//parse data files outputed from FtpMR.java, /tracking-app/src/main/java/com/cmti/analytics/app/tracking/task/importer/mr-data.txt
		Mr mr = new Mr();
		
		StrTokenizer st = new StrTokenizer(line, " ");
		st.setIgnoreEmptyTokens(false);
		
		String[] data = st.getTokenArray();		

		logger.error("{} data.length   {}", data.length, line);//return 12 or 13, Hadoop removes the last ",", WTF
		if(data.length < 4) {
			logger.error("{} data.length < 4 {}", data.length, line);
			return null;
		}
		
		mr.setImsi(StringUtil.getLong(data[0]));
		mr.setCell(StringUtil.getInt(data[1]));
		
		try {
			mr.setTime(dateFormat.parse(data[2]));
		} catch (ParseException e) {
			logger.error("SimpleDateFormat ParseException {}", line);
		}

		mr.setRscp(StringUtil.getInt(data[3]));
			 
		return mr;
	}
	
	@Override
	public Scan getExportScan(Configuration config){
		Scan scan = HBaseUtil.newOnePassMassScan();
		return scan;
	}
	
}
