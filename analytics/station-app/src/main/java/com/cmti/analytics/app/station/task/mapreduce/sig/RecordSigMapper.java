package com.cmti.analytics.app.station.task.mapreduce.sig;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.station.hbase.dao.RecordSigDao;
import com.cmti.analytics.app.station.hbase.domain.RecordSig;
import com.cmti.analytics.hbase.task.mapreduce.FullScanMapper;
 

/**
 * this MR full scans event table.
 * can filter on both event and receive dates
 * 
 * @author Guobiao Mo
 *
 */
public class RecordSigMapper extends FullScanMapper<RecordSig>  {

	protected static final Logger logger = LogManager.getLogger(RecordSigMapper.class); 

	@Override
	protected RecordSigDao setupDao() throws IOException{
		RecordSigDao dao = new RecordSigDao();
			//dao.open();this dao is not opened, since it is for parsing result only.
			return dao;
	}
 

}