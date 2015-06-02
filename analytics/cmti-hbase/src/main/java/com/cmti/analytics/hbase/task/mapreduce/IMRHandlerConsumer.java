package com.cmti.analytics.hbase.task.mapreduce;

import java.util.List;

import com.cmti.analytics.hbase.dao.HBaseObject;
/**
 * 
 * @author Guobiao Mo
 *
 * @param <T>
 */
public interface IMRHandlerConsumer<T extends HBaseObject>{

   	List<String> getHandlerNames();
	//List<BaseMRHandler<T>> setupMRHandlers();
}
