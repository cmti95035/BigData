package com.cmti.analytics.hbase.task.scan;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.dao.IDaoScanner;
import com.cmti.analytics.hbase.util.HBaseUtil;
/**
 * sub-class provides a IDaoScanner and a list of handlers.
 * This class loop all the items from IDaoScanner, and invokes all handlers' handle() method.
 * sub-class remember to call super.close()
 * @author Guobiao Mo
 *
 */
public abstract class ScanTable<T extends HBaseObject>  implements Closeable {

	protected static final Logger logger = LogManager.getLogger(ScanTable.class); 
	
	List<IHandler<T>> handlers;
	IDaoScanner<T> daoScanner;
	
	public ScanTable() throws IOException { 		
		handlers = getHandlers();
	}
		
	public void close() throws IOException{
		for(IHandler<T> eh : handlers) {
			eh.close();
		}
		daoScanner.close();
	}
	
	protected abstract IDaoScanner<T> getDaoScanner() throws IOException;
	protected abstract List<IHandler<T>> getHandlers();
	
	public void scan() throws IOException{	
		daoScanner = getDaoScanner();		
		
		T t= null;
		int n=0;
		while( (t = daoScanner.next()) != null){
			handleItem(t);

			if(n%100000==0){
				logger.info(n+":"+t);
			}
			
			n++;
		}
	}

	protected void handleItem(T t) throws IOException{	
		for(IHandler<T> eh : handlers) {
			eh.handle(t);
		}
	}
}
