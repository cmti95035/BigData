package com.cmti.analytics.hbase.dao;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

import com.cmti.analytics.hbase.mapping.MappingMetaData;
import com.cmti.analytics.util.IOUtil;

	/**
	 * a wrapper on ResultScanner, with element being a <T>.
	 *
	 * @author Guobiao Mo
	 *
	 * @param <T> return T type
	  */
	public class DaoScanner<T extends HBaseObject> implements IDaoScanner<T> {
		private MappingMetaData<T, ?> mapping;
		private ResultScanner rs;
		private boolean hasMoreResults = true;

		DaoScanner(ResultScanner rs, MappingMetaData<T, ?> mapping) {
			this.rs = rs;
			this.mapping = mapping;
		}

		@Override
		public T next() {
			if(!hasMoreResults) {
				return null;
			}

			try {
				Result r = rs.next();
				if(r == null) {
					hasMoreResults = false;
					return null;
				}
				return mapping.parse(r);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} 

		@Override
		public List<T> next(int nRow) {
			if(!hasMoreResults) {
				return null;
			}

			try {
				Result[] r = rs.next(nRow);
				if(r == null || r.length == 0) {
					hasMoreResults = false;
					return null;
				}
				return mapping.parse(r);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} 

		@Override
		public void close() {
			IOUtil.closeQuietly(rs);
		}
	}