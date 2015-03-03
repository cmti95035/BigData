package com.cmti.analytics.app.station.olapdatabase.service;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmti.analytics.app.station.olapdatabase.dao.IDimStationDao;
import com.cmti.analytics.app.station.olapdatabase.domain.DimStation;
import com.cmti.analytics.util.SpringUtil;
 
@Service
public class DimStationService {
	protected static final Logger logger = LogManager.getLogger(DimStationService.class);
	
	List<DimStation> cache;

	@Autowired
	protected IDimStationDao dimStationDao;
	/*
	public IDimStationDao getDimStationDao() {
		return dimStationDao;
	}

	@Required
	public void setDimStationDao(IDimStationDao dimStationDao) {
		this.dimStationDao = dimStationDao;
	}
*/
	public DimStation getDimStation(int id){
		return dimStationDao.find(id);
	} 

	protected void init(){
		cache = dimStationDao.findAll();
	}	
	
	public List<DimStation> findAll(){
		return dimStationDao.findAll();
	}

	public void save(DimStation DimStation){
		//dimStationDao.persist(DimStation);
		dimStationDao.save(DimStation);
	}

	public static void main(String[] args){

		DimStationService service = SpringUtil.getApplicationContext().getBean("dimStationService", DimStationService.class);

		List<DimStation> all = service.findAll();
		
		for(DimStation s : all){
			p("<entry><content type=\"html\">&lt;pr&gt;");
			logger.info(s);
			

			p("&lt;/pr&gt;</content>");

		    p("<geo:lat>"+s.getLat()+"</geo:lat>");
		    p("<geo:long>"+s.getLng()+"</geo:long>");

			p("</entry>");
		}
 
	}
	
	public static void p(String s){
		System.out.println(s);
	}
}










