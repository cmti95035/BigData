package com.cmti.analytics.app.station.olapdatabase.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
 

import com.cmti.analytics.app.station.olapdatabase.domain.FactEvent;
import com.cmti.analytics.database.JPAGenericDao;

@Repository
public class FactEventDao extends JPAGenericDao<FactEvent, Integer> implements IFactEventDao {

	protected static final Logger logger = LogManager.getLogger(FactEventDao.class);
		
}
