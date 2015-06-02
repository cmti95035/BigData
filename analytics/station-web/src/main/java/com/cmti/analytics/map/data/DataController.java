package com.cmti.analytics.map.data;
 
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import redis.clients.jedis.Jedis;

import com.cmti.analytics.app.station.olapdatabase.domain.DimStation;
import com.cmti.analytics.app.station.olapdatabase.service.DimStationService;
import com.cmti.analytics.app.station.redis.domain.EventCount;

/**
//http://localhost/map/html/data/welcome

//http://localhost/map/html/data

 * provide data to web client
 * 
 * @author Guobiao Mo
 *
 */

@Controller
@RequestMapping("/data")
public class DataController {
	protected static final Logger logger = LogManager.getLogger(DataController.class);


	@Autowired
	protected DimStationService dimStationService;

    Jedis jedis = new Jedis("quickstart.cloudera");
    
	@RequestMapping(value="/sichuan/station", method = RequestMethod.GET)
	public String allSichuanStation(ModelMap model, HttpServletRequest request) {

		List<DimStation> all = dimStationService.findAll();
		
//		for(DimStation s : all){
	//		logger.error(s);
		//}
 
		
		model.addAttribute("stations", all);
 
		//Spring uses InternalResourceViewResolver and return back index.jsp
		return "stations"; 
	}

	@RequestMapping(value="/sichuan/station/{id}", method = RequestMethod.GET)
	public String sichuanStation(@PathVariable int id, ModelMap model, HttpServletRequest request) {

		DimStation s = dimStationService.getDimStation(id);
		int cell = Math.abs(s.getId()%10);

				//Set<String> keys = jedis.keys("c~"+s.getId()+"~*~10");
		Set<String> keys = jedis.keys("c~"+cell+"~*~10");
	    for(String key:keys) {
//  	    	jedis.del(key);//.c.set(key, null);
	    	 
		    String value = jedis.get(key);
		    EventCount ec = new EventCount(key, value);
		    System.err.println("key="+key+" count="+value);
		    
//		    s.addEventCount(ec);//FIXME
	    }	   
				
		model.addAttribute("station", s);
 
		//Spring uses InternalResourceViewResolver and return back index.jsp
		return "station"; 
	}
 
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
 
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - welcome()");
 
		//Spring uses InternalResourceViewResolver and return back index.jsp
		return "index";
 
	}
 
	@RequestMapping(value="/welcome/{name}", method = RequestMethod.GET)
	public String welcomeName(@PathVariable String name, ModelMap model) {
 
		model.addAttribute("message", "Maven Web Project + Spring 3 MVC - " + name);
		return "index";
 
	}
 
}