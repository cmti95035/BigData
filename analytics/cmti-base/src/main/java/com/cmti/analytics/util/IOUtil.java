package com.cmti.analytics.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * IO utility
 * @author Guobiao Mo
 *
 */
public class IOUtil extends org.apache.commons.io.IOUtils {
	private static final Logger logger = LogManager.getLogger(IOUtil.class);

	/**
	 * Quietly close all closables
	 * @param closables
	 */
	public static final void closeQuietly(Closeable... closables) {
		if(closables == null) {
			return;
		}
		for(Closeable c : closables) {
			if(c == null) {
				continue;
			}
			try {
				c.close();
			} catch(Throwable t) {
				// log and continue
				logger.error("Failed to close " + c, t);
			}
		}
	}

	public static String getUrlContent(String urlstr) throws IOException{
		InputStream in = new URL(urlstr).openStream();
		try {
			return IOUtils.toString( in ) ;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}
