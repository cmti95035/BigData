package com.cmti.analytics.app.tracking.olapdatabase.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
 
import com.cmti.analytics.app.tracking.olapdatabase.domain.RoadTest;
import com.cmti.analytics.database.JPAGenericDao;

@Repository
public class RoadTestDao extends JPAGenericDao<RoadTest, Integer> implements IRoadTestDao {

	protected static final Logger logger = LogManager.getLogger(RoadTestDao.class);
		
}
