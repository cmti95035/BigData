package com.cmti.analytics.hbase.dao;

import java.io.IOException;
import java.util.*;

import org.apache.commons.configuration.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.cmti.analytics.hbase.export.ExportIterator;
import com.cmti.analytics.hbase.export.HBaseDataExport;
import com.cmti.analytics.hbase.util.HBaseUtil;
/**
 * On top of HBaseGenericDao. 
 * provide a ExportIterator for HBaseDataExport to loop and write to a file.
 * Subclass may override getExportScan().
 * 
 * this class is also super of all import Dao.
 * 
 * @author Guobiao Mo
 *
 * @param <T>
 * @param <P>
 */
public abstract class ExportDao<T extends HBaseObject, P> extends HBaseGenericDao<T, P>{
	
	public ExportIterator<T, P> export(Configuration config) throws IOException {
		Scan scan = getExportScan(config);
		scan.setCacheBlocks(false);
		DaoScanner<T> scanner = getDaoScanner(scan);
		return new ExportIterator<T, P>(super.getMapping(), scanner, (List<String>)(List<?>)config.getList(HBaseDataExport.COLUMNS), config.getBoolean(HBaseDataExport.INCLUDE_UNMAPPED, false));
	}
	 
	public Scan getExportScan(Configuration config){
		Scan scan = HBaseUtil.newOnePassMassScan();
		return scan;
	}
	
	public abstract T parseLine(String line, Context context) ;//for import, used in BulkLoaderMapper.java
}