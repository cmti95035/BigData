package com.cmti.analytics.hbase.mapping.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Utility HBase class to join (with escape) and split bytes (with escape) by using a byte delimiter.
 *
 * @author Guobiao Mo
 */
public class ByteFormatter {
	public static byte escape = Bytes.toBytes("\\")[0];

	private byte[] delBytes;
	private byte delimiter;

	public ByteFormatter(char del) {
		delBytes = Bytes.toBytes(String.valueOf(del));
		delimiter = delBytes[0];
	}

	public byte[] format(byte[]... bytesArray) {			
		byte[] ret = new byte[0];
		for(int i = 0; i < bytesArray.length; i++) {
			byte[] bytes = bytesArray[i];
			if(i > 0) {
				ret = Bytes.add(ret, delBytes);
			}
			if(ArrayUtils.isNotEmpty(bytes)) {
				ret = Bytes.add(ret, escape(bytes));
			}
		}
		return ret;
	}

	public byte[][] parse(byte[] bytes) {

		List<byte[]> ret = new ArrayList<byte[]>();

		int copyStart = 0;
		byte[] copied = null;

		for(int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			if(b == escape) {
				byte nextB = bytes[i + 1];
				if(nextB == delimiter || nextB == escape) {
					int len = i - copyStart;
					int copiedLen = (copied == null ?0:copied.length);

					copied = buildAndFill(copiedLen+len+1, copied, 0, copiedLen, bytes, copyStart, len);
					copied[copiedLen+len] = nextB;
					
					copyStart = i + 2;
					i++;
				} else {
					throw new RuntimeException(String.format("Unsupported escaped byte at index %s in [%s]", i+1, StringUtils.join(ArrayUtils.toObject(bytes),',')));					
				}
			} else if(b == delimiter) {
				int len = i - copyStart;
				int copiedLen = (copied == null ?0:copied.length);

				copied = buildAndFill(copiedLen+len, copied, 0, copiedLen, bytes, copyStart, len);
				
				copyStart = i + 1;
				ret.add(copied);
				copied = null; // reset
			}
		}
		// last block
		int len = bytes.length - copyStart;
		int copiedLen = (copied == null ?0:copied.length);

		copied = buildAndFill(copiedLen+len, copied, 0, copiedLen, bytes, copyStart, len);

		ret.add(copied);
		return ret.toArray(new byte[0][]);
	}

	private byte[] escape(byte[] bytes) {
		byte[] ret = null;
		int start = 0;
		for(int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			if(b == delimiter || b == escape) {
				int len = i - start;
				int retLen = (ret == null ?0:ret.length);

				ret = buildAndFill(retLen+len+2, ret, 0, retLen, bytes, start, len);
				
				ret[retLen+len] = escape;
				ret[retLen+len + 1] = b;
				
				start = i + 1;
			}
		}
		
		if(ret == null) { // nothing escaped
			return bytes;
		}
		
		//add the remaining
		int left = bytes.length - start;
		if(left > 0) {
			byte[] tmp = buildAndFill(ret.length+left, ret, 0, ret.length, bytes, start, left);
			return tmp;
		}else{
			return ret;
		}
	}
	
	//build a byte[len], fill it with src1 and src2
	private static byte[] buildAndFill(int len, byte[] src1, int pos1, int len1, byte[] src2, int pos2, int len2) {
		byte[] ret = new byte[len];
		if(len1>0) {
			System.arraycopy(src1, pos1, ret, 0,    len1);
		}
		if(len2>0) {
			System.arraycopy(src2, pos2, ret, len1, len2);
		}
		return ret;
	}
}



















