package com.cmti.analytics.hbase.util;

import java.util.List;

import org.apache.hadoop.hbase.client.Scan;

import com.cmti.analytics.hbase.dao.*;
import com.cmti.analytics.hbase.mapping.IColumn;

/**
 * HBase utility
 * @author Guobiao Mo
 *
 */
public class HBaseUtil {

	//private static final Logger logger = LogManager.getLogger(HBaseUtil.class);

	public static final char DELIMITER = '~';
	
	public static final int BATCH_SIZE =1000;	

	//for mass scan, and one pass only. Good for MapReduce job.
	public static Scan newOnePassMassScan() {
		Scan scan = new Scan();
		scan.setCaching(HBaseUtil.BATCH_SIZE);        //Mass: 1 is the default in Scan, ask server to batch send the results
		scan.setCacheBlocks(false);  // don't cache HDFS blocks in memory since we don't plan to access it again (One Pass)
		return scan;
	}
	
	//only mapped columns are retrieved--this is slower, don't use it.
	public static <T extends HBaseObject, P> Scan newOnePassMassScan(HBaseGenericDao<T, P> dao){
		Scan scan =  newOnePassMassScan();

		List<IColumn<T>> mappedColumns = dao.getMappedColumns();
		for(IColumn<T> c : mappedColumns) {
			scan.addColumn(c.getColumnFamilyBytes(), c.getColumnBytes());
		}		
		
		return scan;
	}
	
}