package com.cmti.analytics.app.station.olapdatabase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmti.analytics.app.station.olapdatabase.dao.ISigHistoryDao;
import com.cmti.analytics.app.station.olapdatabase.domain.SigHistory;

@Service
public class SigHistoryService{
	protected static final Logger logger = LogManager.getLogger(SigHistoryService.class);

	@Autowired
	protected ISigHistoryDao dao;	 
	
	@Transactional
	public void save(SigHistory f){
		dao.save(f);
	}
 
}
