package com.cmti.analytics.app.station.olapdatabase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmti.analytics.app.station.olapdatabase.dao.IFactEventDao; 
import com.cmti.analytics.app.station.olapdatabase.dao.IFactImsiDao;
import com.cmti.analytics.app.station.olapdatabase.domain.FactEvent;
import com.cmti.analytics.app.station.olapdatabase.domain.FactImsi;

@Service
public class FactImsiService{
	protected static final Logger logger = LogManager.getLogger(FactImsiService.class);

	@Autowired
	protected IFactImsiDao factImsiDao;	 
	
	@Transactional
	public void save(FactImsi f){
		factImsiDao.save(f);
	}
 
}
