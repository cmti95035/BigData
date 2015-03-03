package com.cmti.analytics.app.station.olapdatabase.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
 


import com.cmti.analytics.app.station.olapdatabase.domain.FactEvent;
import com.cmti.analytics.app.station.olapdatabase.domain.FactImsi;
import com.cmti.analytics.database.JPAGenericDao;

@Repository
public class FactImsiDao extends JPAGenericDao<FactImsi, Integer> implements IFactImsiDao {

	protected static final Logger logger = LogManager.getLogger(FactImsiDao.class);
		
}
