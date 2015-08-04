package com.cmti.analytics.map.data;
 
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmti.analytics.app.tracking.hbase.dao.DriveTestDataDao;
import com.cmti.analytics.app.tracking.hbase.domain.DriveTestData;

/**
display drive test and MR rscp data on baidu map
 * 
 * @author Guobiao Mo
 *
 */

@Controller
@RequestMapping("/data")
public class DataController {
	static final double LON_CORRECT=0.0089;
	static final double LAT_CORRECT=0.0035;
			
	protected static final Logger logger = LogManager.getLogger(DataController.class);

	@RequestMapping(value="/sichuan/chengdu/3huan/combine", method = RequestMethod.GET)//TODO url 3huan ok? should pass road id
	public String combine(ModelMap model, HttpServletRequest request) throws IOException {
		List<DriveTestData> mrData = getMrData(0.001, 0.001);
		List<DriveTestData> dtData = getDriveTestData(); 

		model.addAttribute("drive_test_datas", dtData); 
		model.addAttribute("mr_datas", mrData); 

		return "combine-heatmap"; 
	}

	@RequestMapping(value="/sichuan/chengdu/3huan/mr", method = RequestMethod.GET)//TODO url 3huan ok? should pass road id
	public String mr(ModelMap model, HttpServletRequest request) throws IOException {
		List<DriveTestData> mrData = getMrData();		
		model.addAttribute("mr_datas", mrData);//TODO also pass in rscp limits, also make sure bad rscp is in red
		return "mr-heatmap"; 
	}

	@RequestMapping(value="/sichuan/chengdu/3huan/drive-test", method = RequestMethod.GET)
	public String driveTest(ModelMap model, HttpServletRequest request) throws IOException {//TODO combine w above method
//		public String driveTest(ModelMap model, HttpServletRequest request, @RequestParam double lon,@RequestParam  double lat) throws IOException {//TODO combine w above method
		List<DriveTestData> dtData = getDriveTestData(); 
		model.addAttribute("drive_test_datas", dtData);
		return "drive-test-heatmap"; 
	}


	public List<DriveTestData> getDriveTestData() throws IOException {
		DriveTestDataDao driveTestDataDao = new DriveTestDataDao();//this is not efficient, use ThreadLocal TODO
		driveTestDataDao.open();
		List<DriveTestData> all = driveTestDataDao.getAll();
						
		driveTestDataDao.close();
		 		
		Iterator<DriveTestData> itr=all.iterator();

		int i=0;
	    while (itr.hasNext()) {
	    	DriveTestData driveTestData = (DriveTestData) itr.next();
	    	if (driveTestData.getRscp()==null) {
	    		itr.remove();
	    		continue;
	     	}
	    	
	    	if(i++%100 !=1){//baidu map can not show too many points. so we pick 1% data to show. TODO
	    		itr.remove();	
	    		continue;    		
	    	}

	    	driveTestData.setLongitude(driveTestData.getLongitude()+LON_CORRECT);
	    	driveTestData.setLatitude(driveTestData.getLatitude()+LAT_CORRECT);
	    }

	    return all;
	}
 
	

	public List<DriveTestData> getMrData() throws IOException {
		return getMrData(0., 0.);
	}

	public List<DriveTestData> getMrData(double lonDel, double latDel) throws IOException {
		DriveTestDataDao driveTestDataDao = new DriveTestDataDao();//this is not efficient, use ThreadLocal TODO
		driveTestDataDao.open();
		List<DriveTestData> all = driveTestDataDao.getAll();			
		
		driveTestDataDao.close();
		 		
		Iterator<DriveTestData> itr=all.iterator();

	    while (itr.hasNext()) {
	    	DriveTestData driveTestData = (DriveTestData) itr.next();
	    	if (driveTestData.getMrCount()==null || driveTestData.getMrCount()==0L) {
	    		itr.remove();
	     	}else{
	     		logger.error("{} count={} sum={} AverageMrRscp={}", driveTestData, driveTestData.getMrCount(), driveTestData.getMrRscpSum(), driveTestData.getAverageMrRscp());
	     		if( driveTestData.getMrRscpSum()==null){
	     			//if value is null in hbase, and incrementColumnValue(amount=0), hbase value is still null, so we have to set it to 0 here.	
	     			driveTestData.setMrRscpSum(0L);    			
	     		}

		    	driveTestData.setLongitude(driveTestData.getLongitude()+LON_CORRECT+lonDel);
		    	driveTestData.setLatitude(driveTestData.getLatitude()+LAT_CORRECT+latDel);
	     	}
	    }
	    return all;
	}

}