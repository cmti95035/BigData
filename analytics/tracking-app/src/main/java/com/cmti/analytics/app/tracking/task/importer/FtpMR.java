package com.cmti.analytics.app.tracking.task.importer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.cmti.analytics.app.tracking.hbase.dao.MrDao;
 

/**
 * @author gmo
 *  
 *  put a zip file containing MR xml files in ftp server.
 C:\git\cmti2\cmti\analytics>java -cp C:\git\cmti2\cmti\analytics\tracking-app\target\tracking-app-1.0-SNAPSHOT.jar com.cmti.analytics.app.tracking.task.importer.FtpMR  quickstart.cloudera cloudera cloudera .
 */
public class FtpMR  {

//	protected static final Logger logger = LogManager.getLogger(FtpMR.class); 
		 
	public FtpMR() throws IOException{ 
	}


	public static void main(String[] args) throws Exception { 
		FTPClient ftp = new FTPClient();

		String host =args[0];//ftp host
		String username =args[1];//ftp login
		String password =args[2];//ftp password
		String remote =args[3];//pass "." for current dir
		
		ftp.connect(host);//connect to ftp server
		
		ftp.login(username, password);//login to ftp
		ftp.setFileType(FTP.BINARY_FILE_TYPE);//set ftp file type to binary
		FTPFileFilter filter = new FTPFileFilter() {//filter ftp file names, only retrieve zip files			 
		    @Override
		    public boolean accept(FTPFile ftpFile) {		 
		        return (ftpFile.isFile() && ftpFile.getName().endsWith(".zip"));
		    }
		};
		
		FTPFile [] fs = ftp.listFiles(remote, filter);//list all zip files in folder 'remote'
        for (FTPFile f : fs) {//for each zip files

//        	logger.error(f.getRawListing());
  //      	logger.error(f.getName());
    //    	logger.error(f.toFormattedString());
        	
        	InputStream is = ftp.retrieveFileStream(f.getName()); //retrieve the zip file as an InputStream
        	ZipInputStream zis = new ZipInputStream(is);//convert to ZipInputStream
        	
        	ZipEntry ze = null;

        	while((ze=zis.getNextEntry())!=null){//for each xml file in the zip file
        		int size = (int)ze.getSize();//size of the xml file
        		byte[] buffer = new byte[size];
        		IOUtils.readFully(zis, buffer);//read the xml into 'buffer'
        		

        	    SAXBuilder builder = new SAXBuilder();//new a JDOM builder 
        	    Document doc = builder.build(new ByteArrayInputStream(buffer));//parse the xml into a JDOM Document
        	    Element root = doc.getRootElement();//root Element 'bulkPmMrDataFile' of the xml
        	    @SuppressWarnings("unchecked")
        		List<Element> objectElements = (List<Element>)root.getChild("rnc").getChild("class").getChild("measurement").getChildren("object");//retrieve all 'object' elements
        	            	    
        	    for(Element objectElement : objectElements){//for each 'object' element
        	    	String id = objectElement.getAttributeValue("id"); //cell id
        	    	String time = objectElement.getAttributeValue("TimeStamp");//TimeStamp
        	    	String imsi = objectElement.getAttributeValue("IMSI");//imsi

        	    	String v = objectElement.getChildText("v");
        	    	String mrTdScPccpchRscp = v.split(" ")[0];//extract MR.TdScPccpchRscp from the 1st 'v' element 
//            		logger.error(ze.getName()+id+" "+time+" "+mrTdScPccpchRscp+" "+ imsi);
            		System.out.println(imsi+" "+id+" "+time+" "+mrTdScPccpchRscp);//print
        	    }        		
        	}
            
        }
        ftp.noop(); // check that control connection is working OK

        ftp.logout();//logout ftp 
//        System.exit(0);
	}

}
