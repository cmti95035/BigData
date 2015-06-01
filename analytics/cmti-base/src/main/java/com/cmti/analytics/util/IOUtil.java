package com.cmti.analytics.util;

import java.io.Closeable;

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
}
