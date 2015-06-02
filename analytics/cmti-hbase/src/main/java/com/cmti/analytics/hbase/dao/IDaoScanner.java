package com.cmti.analytics.hbase.dao;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

import com.cmti.analytics.hbase.mapping.MappingMetaData;
import com.cmti.analytics.util.IOUtil;

	/**
	 * Interface that provides ability to incrementally visit result returned by DAO.
	 * Known implementation: DaoScanner, EventDaoScannerFromReceive
	 *
	 * @author Guobiao Mo
	 *
	 * @param <T> return T type
	  */
	public interface IDaoScanner<T extends HBaseObject> extends Closeable {

		public T next();

		public List<T> next(int nRow);//should it be Collection?
	}