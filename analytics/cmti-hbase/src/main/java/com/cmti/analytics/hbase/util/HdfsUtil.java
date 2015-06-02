package com.cmti.analytics.hbase.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
/**
 * 
 * @author Guobiao Mo
 *
 */
public class HdfsUtil{ 

	private static Logger logger = Logger.getLogger(HdfsUtil.class);	

	public static List<Long> getLongFromFile(String path) throws IOException {
		ArrayList<Long> ret = new ArrayList<Long>();

        Path pt=new Path("hdfs:"+path);
            //Path pt=new Path("hdfs:/user/hbase/input/07-26/mongo1-07-26.txt");
        //Path pt=new Path("hdfs://npvm11.np.wc1.yellowpages.com:9000/user/john/abc.txt");
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
        String line;
        while ((line=br.readLine()) != null){
                	ret.add(Long.parseLong(line.trim()));
        }
        return ret;
	}

	public static List<String> getStringFromFile(String path) throws IOException {
		ArrayList<String> ret = new ArrayList<String>();

        Path pt=new Path("hdfs:"+path);
            //Path pt=new Path("hdfs:/user/hbase/input/07-26/mongo1-07-26.txt");
        //Path pt=new Path("hdfs://npvm11.np.wc1.yellowpages.com:9000/user/john/abc.txt");
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
        String line;
        while ((line=br.readLine()) != null){
        	ret.add(line);
        }
        return ret;
	}
}







