package com.cmti.analytics.hbase.mapping.mapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.hbase.util.HBaseUtil;
/**
 * 
 * @author Guobiao Mo
 *
 */
public class CompositeMapper extends Mapper<Object[]> {
	protected static final Logger logger = LogManager.getLogger(CompositeMapper.class);
	
		public static final ByteFormatter formatter = new ByteFormatter(HBaseUtil.DELIMITER);

		protected Mapper[] mappers;
		
		public CompositeMapper(Mapper[] mappers) {
			if(ArrayUtils.isEmpty(mappers)) {
				throw new IllegalArgumentException("Input mappers cannot be null or empty");
			}
			this.mappers = mappers;
		}
		
		@Override
		public byte[] toBytes(Object[] t) {
			if(ArrayUtils.isEmpty(t)) {
				return null;
			} else if(t.length != mappers.length) {
				throw new RuntimeException("input length does not match the number of mappers");
			}
			byte[][] mappedBytes = new byte[t.length][];
			for(int i = 0; i < t.length; i++) {
				mappedBytes[i] = mappers[i].toBytes(t[i]);
			}
			return formatter.format(mappedBytes);
		}

		@Override
		public Object[] fromBytes(byte[] bytes, int offset, int length) {
			if(bytes == null) {
				return null;
			}
			Object[] ret = new Object[mappers.length];

			byte[][] splitBytes = formatter.parse(bytes);

			if(splitBytes.length != ret.length) {
				for(Mapper mapper : mappers){
					logger.error(mapper);
				}
				for(byte[] bs : splitBytes){
					logger.error(new String(bs));
				}
				
				throw new RuntimeException("Failed to resolve all fields. Resolved = " 
						+ splitBytes.length + ", expected = " + ret.length);
			}
			
			for(int i = 0; i < splitBytes.length; i++) {
				ret[i] = mappers[i].fromBytes(splitBytes[i]);
			}			
			
			return ret;
		}
		
}