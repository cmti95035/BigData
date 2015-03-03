package com.cmti.analytics.app.station.olapdatabase.dao;

import org.springframework.stereotype.Repository;

import com.cmti.analytics.app.station.olapdatabase.domain.DimStation;
import com.cmti.analytics.database.JPAGenericDao;

@Repository
public class DimStationDao extends JPAGenericDao<DimStation, Integer> implements IDimStationDao {
/*
	public DimDeviceGroup getDeviceGroup(String group){

		Search search = new Search();
//		Device Group number is unique
		search.addFilterEqual("modelGroup", group);

		List<DimDeviceGroup> list = search(search);
		
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Transactional
	public DimDeviceGroup insert(String nook, String modelGroup){
		DimDeviceGroup et = new DimDeviceGroup();
		et.setNook(nook); //Nook, Non-Nook
		et.setModelGroup(modelGroup); //iOS, HD/HD+, 
		
		super.persist(et);
		
		return et;
	}

	
	public static void main(String[] args){
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring/app.xml"});
		
		DeviceGroupDao dao = context.getBean("deviceGroupDao", DeviceGroupDao.class);

		DimDeviceGroup group = dao.find(1);
		System.out.println(group.getModelGroup()+group.getNook());
	}*/
}
