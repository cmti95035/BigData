package com.cmti.analytics.hbase.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection; 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import com.cmti.analytics.database.DBValue;
import com.cmti.analytics.hbase.dao.ExportDao;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.util.EmailUtil;
/**
 * Read a config file like \analytics\src\main\resources\event_export.properties
 * and out put a csv file.
 * 
 * java -cp analytics-1.0-SNAPSHOT.jar -Dsite=prod -Dlog4j.configurationFile=log4j2/log4j2_prod.xml  com.cmti.analytics.hbase.export.HBaseDataExport export/reading.properties 
 * 
 * TODO: Provide a UI interface to this data export feature.
 * @author Guobiao Mo
 *
 * @param <T>
 * @param <P>
 */
public class HBaseDataExport<T extends HBaseObject, P> {
	public static final String INCLUDE_UNMAPPED="include_unmapped";
	public static final String COLUMNS="columns";
	public static final String START_ROW ="start_row";
	public static final String STOP_ROW ="stop_row";

	protected static final Logger logger = LogManager.getLogger(HBaseDataExport.class);
	protected Connection con; 

	Class<? extends ExportDao<T, P>> exportDaoClass;
	File saveFile;
	Configuration config;
	int maxCount=-1;
	boolean zip;

	String[] emailTo;
	String emailSubject;
	String emailMsg;
	boolean emailAttachment;
	
	public HBaseDataExport(String configFileName) throws ConfigurationException{  
		this(new PropertiesConfiguration(configFileName));
	}
	
	/*
	 * setup parameters, TODO define the names in final static
	 */
	public HBaseDataExport(Configuration config){    
		this.config = config;
 		
		String exportClassName= config.getString("dao_class");
		try{
			exportDaoClass = (Class<? extends ExportDao<T, P>>)Class.forName(exportClassName);
		}catch(ClassNotFoundException e) {
			logger.error(exportClassName, e);			
		}
		
		String saveDirName = config.getString("save_dir", "/tmp");
		File saveDir = new File(saveDirName);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String dateString = sdf.format(new Date());
		String fileName = "hbase_data_" + exportDaoClass.getSimpleName()+"_"+dateString+".csv";
		
		zip = config.getBoolean("zip", false);
		if(zip){
			fileName += ".gz";
		}
		
		saveFile = new File(saveDir, fileName);
		
		maxCount = config.getInt("max_count", -1);	

		emailTo = config.getStringArray("emailTo");
		emailSubject = config.getString("emailSubject", null);
   		emailSubject = StringUtils.isBlank(emailSubject)?"Data Exported":emailSubject + " " + fileName;
		emailAttachment = config.getBoolean("emailAttachment", false);
		if(emailAttachment){
			emailMsg = "File is attached.";
		}else{
			emailMsg = "File Path: " + saveFile.getAbsolutePath();			
		}
		
		String emailMsg2 = config.getString("emailMsg", null);
		if(StringUtils.isNotBlank(emailMsg2)) {
			emailMsg += '\n' + emailMsg2;
		}
	}

	public String export() throws IOException, InstantiationException, IllegalAccessException {
		OutputStream outputStream = null;
		
		if(zip){
			outputStream = new GZIPOutputStream(new FileOutputStream(saveFile));
		}else{
			outputStream = new FileOutputStream(saveFile);
		}
		
    //    GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File("tmp.zip")));

  //      writer = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
        
        
//			FileWriter fw = new FileWriter(saveFile);
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));//FIXME need "UTF-8"));?
			logger.info("Data will be exported to "+saveFile.getAbsolutePath());
			
			ExportDao<T, P> dao = exportDaoClass.newInstance();   
			String tableName = config.getString("table_name");
			if(StringUtils.isNotBlank(tableName)){
				dao.setTableName(tableName);
			}
			dao.open();
			ExportIterator<T, P> it = dao.export(config);
		    
		    boolean showHeader = config.getBoolean("show_header", true);
		    
		    String item =null;
		    int count = 0;
		    while((item = it.next())!=null){
		    	if(showHeader){
		    		String[] header = config.getStringArray("header");
		    		if(header !=null && header.length > 0){
		    			out.println(StringUtils.join(header, ","));
		    		}
			    	showHeader = false;
		    	}
		    	out.println(item);
		    	
		    	if(maxCount>0 && ++count>=maxCount){
					logger.info("count="+count);
		    		break;
		    	}
		    }
		    it.close();
		    out.flush();
		    outputStream.flush();
		    outputStream.close();
		    out.close();

			logger.info("Data is exported to "+saveFile.getAbsolutePath());

		    if(emailTo!=null && emailTo.length>0){
		    	try {
					EmailUtil.sendEmail(emailSubject, emailMsg, emailAttachment?saveFile.getAbsolutePath():null, emailTo);
				} catch (EmailException e) {
					logger.error(emailTo[0], e);
				}
		    }
		    
			return saveFile.getAbsolutePath();
	}

	public static void main(String args[]) throws Exception{
		String path = new HBaseDataExport(args[0]).export();
		if(args[0].indexOf("reading.properties")>-1){//not a nice way FIXME
			logger.info("reading"+System.currentTimeMillis() + path);	
			//DBValue.setString("reading"+System.currentTimeMillis(), path);	
		}
	}
	 

}