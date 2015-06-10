package com.cmti.analytics.app.tracking.task.importer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
//import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;








import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.cmti.analytics.app.tracking.hbase.dao.MrDao;
import com.cmti.analytics.app.tracking.hbase.domain.Mr;
import com.cmti.analytics.hbase.dao.ExportDao;
//import com.cmti.analytics.app.device.task.importer.HdfsEventBulkLoader;
import com.cmti.analytics.hbase.dao.HBaseGenericDao;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.loader.BulkLoaderMapper;
import com.cmti.analytics.hbase.task.mapreduce.ConfiguredMR;
import com.cmti.analytics.hbase.util.HBaseConfig;
import com.cmti.analytics.hbase.util.ReflectUtil;
import com.cmti.analytics.util.StringUtil;

/**
 * 
 * Bulk loader to put data into HBase using MapReduce.
 * if outputPath != null, it generates HFile in hdfs outputPath.
 * else, it uses the TableOutputFormat API to insert data directly to HBase.
 * @author Guobiao Mo
 *
 * @param <T>
 * @param <P>
 */

public class MrXmlBulkLoaderMapper extends BulkLoaderMapper<Mr, Object> {

		protected final Logger logger = LogManager.getLogger(MrXmlBulkLoaderMapper.class); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");  

		public MrXmlBulkLoaderMapper(){
//			dao = new StationDao();
		}
/*
		public BulkLoaderMapper(Class<? extends ExportDao<T, P>> daoClass){
			dao = ReflectUtil.newInstance(daoClass);
		}
	*/		
		@Override
		protected void setup(Context context) throws IOException {
			dao = new MrDao();
		}
		 /*
		@Override
		protected void cleanup(Context context) {
			try{
//			dao.close();//FIXME
			}catch(Exception e){
				e.printStackTrace();
			}
		}*/

		@Override
		public void map(LongWritable offset, Text value, Context context) throws IOException, InterruptedException {
			String xml = value.toString();

    	    SAXBuilder builder = new SAXBuilder();//new a JDOM builder 
    	    Element root = null;
			try {
				Document doc = builder.build(new StringReader(xml));//parse the xml into a JDOM Document
	    	    root = doc.getRootElement();//root Element 'bulkPmMrDataFile' of the xml
			} catch (JDOMException e1) {
				logger.error(xml, e1);
				return;
			}
    	    @SuppressWarnings("unchecked")
    		List<Element> objectElements = (List<Element>)root.getChild("rnc").getChild("class").getChild("measurement").getChildren("object");//retrieve all 'object' elements
    	            	    
    	    for(Element objectElement : objectElements){//for each 'object' element
    	    	String id = objectElement.getAttributeValue("id"); //cell id
    	    	String time = objectElement.getAttributeValue("TimeStamp");//TimeStamp
    	    	String imsi = objectElement.getAttributeValue("IMSI");//imsi

    	    	String v = objectElement.getChildText("v");
    	    	String mrTdScPccpchRscp = v.split(" ")[0];//extract MR.TdScPccpchRscp from the 1st 'v' element 
//        		logger.error(ze.getName()+id+" "+time+" "+mrTdScPccpchRscp+" "+ imsi);
        		System.out.println(imsi+" "+id+" "+time+" "+mrTdScPccpchRscp);//print
        		
        		Mr mr = new Mr();

        		mr.setImsi(StringUtil.getLong(imsi));
        		mr.setCell(StringUtil.getInt(id));
        		
        		try {
        			mr.setTime(dateFormat.parse(time));
        		} catch (ParseException e) {
        			logger.error("SimpleDateFormat ParseException {}", time);
        		}

        		mr.setRscp(StringUtil.getInt(mrTdScPccpchRscp));

    			byte[] bRowKey = dao.getKey(mr);
    			ImmutableBytesWritable rowKey = new ImmutableBytesWritable(bRowKey);

				Put p = dao.getPut(mr);
				context.write(rowKey, p);
    	    }           	    
		}
}
