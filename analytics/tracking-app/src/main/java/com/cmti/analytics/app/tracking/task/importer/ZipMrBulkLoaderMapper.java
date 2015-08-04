package com.cmti.analytics.app.tracking.task.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.cmti.analytics.app.tracking.hbase.dao.MrDao;
import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;
import com.cmti.analytics.util.StringUtil;

/**
 * parse MR txt and generate MR puts
 * 
 * @author Guobiao Mo
 *
 */

public class ZipMrBulkLoaderMapper extends BulkLoaderMapper<Mr, Object> {

		protected final Logger logger = LogManager.getLogger(ZipMrBulkLoaderMapper.class); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");  
	
		@Override
		protected void setup(Context context) throws IOException {
			dao = new MrDao();
		}

		@Override
		public void map(LongWritable offset, Text value, Context context) throws IOException, InterruptedException {
			String txt = value.toString();
			BufferedReader in = new BufferedReader(new StringReader(txt));
			
			String line=null;
			int n=0;
			while((line=in.readLine())!=null) {
				n++;
				doLine(line, context);
			}
    	    logger.info("got {} MRs", n);
		}
		
		private void doLine(String line, Context context) {//copied from BulkLoaderMapper TODO

			if(StringUtils.isBlank(line)){
				logger.error("got an empty line.");
				return;
			}			

			Mr t = dao.parseLine(line, context);
				
	//		logger.error(t);
			
			if(t == null){
				logger.debug("parse result == null for line:{}", line);
				return;
			}
				
			byte[] bRowKey = dao.getKey(t);
			ImmutableBytesWritable rowKey = new ImmutableBytesWritable(bRowKey);
			try{
				Put p = dao.getPut(t);
				context.write(rowKey, p);
			}catch(Exception e){
				logger.error(line, e);
			} 
    	    
		}
}
