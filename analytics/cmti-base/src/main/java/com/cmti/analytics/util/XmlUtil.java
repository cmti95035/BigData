package com.cmti.analytics.util;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.*;
import org.jdom2.input.*; 

public class XmlUtil{ 

	private static Logger logger = Logger.getLogger(XmlUtil.class);
	

	public static Element getRootElement(String urlstr) throws IOException, JDOMException{
		SAXBuilder builder = new SAXBuilder();
		
		URL url = new URL(urlstr);        
		org.jdom2.Document document = builder.build(url);
		
		Element root = document.getRootElement();	
		 
		return root;
	}

	/*
	public static String getUrlContent(String urlstr) throws IOException{
		InputStream in = new URL(urlstr).openStream();
		try {
			return IOUtils.toString( in ) ;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static List<String> getFileContent(String fileName) throws IOException{ 
		File file = new File(fileName);
//		List<String> lines = FileUtils.readLines(file, "UTF-8");
		List<String> lines = FileUtils.readLines(file, (String)null);
		
		return lines;
	} 
	*/
}







