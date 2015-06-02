package com.cmti.analytics.hbase.task.scan;

import java.io.Closeable;
import java.io.IOException;

import com.cmti.analytics.hbase.dao.HBaseObject;

/**
 * Interface to be implemented by all scan handlers.
 * @author Guobiao Mo
 *
 * @param <T>
 */
public interface IHandler<T extends HBaseObject> extends Closeable {
	void handle(T t) throws IOException;
	void close() throws IOException;
}
