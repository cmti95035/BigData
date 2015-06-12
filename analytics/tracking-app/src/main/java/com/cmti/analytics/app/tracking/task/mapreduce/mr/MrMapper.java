package com.cmti.analytics.app.tracking.task.mapreduce.mr;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.app.tracking.hbase.dao.MrDao;
import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hbase.task.mapreduce.FullScanMapper;
 

/**
 * this MR scans mr table.
 * 
 * @author Guobiao Mo
 *
 */
public class MrMapper extends FullScanMapper<Mr>  {

//	protected static final Logger logger = LogManager.getLogger(MrMapper.class); 

	@Override
	protected MrDao setupDao() throws IOException{
		MrDao dao = new MrDao();
			//dao.open();this dao is not opened, since it is for parsing result only.
			return dao;
	}
 

}