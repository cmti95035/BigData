package com.cmti.analytics.app.station.olapdatabase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmti.analytics.app.station.olapdatabase.dao.IFactEventDao; 
import com.cmti.analytics.app.station.olapdatabase.domain.FactEvent;

@Service
public class FactEventService{
	protected static final Logger logger = LogManager.getLogger(FactEventService.class);

	@Autowired
	protected IFactEventDao factEventDao;	 

	@Transactional
	public void save(FactEvent f){
		factEventDao.save(f);
	}
 
}
