package com.cmti.analytics.database;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cmti.analytics.conf.Config;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Serves as a pool of all data sources. 
 * Make sure you close the DataSources after you're done with it.
 *
 * @author Guobiao Mo
 */
public class DataSources {

	private static final Log logger = LogFactory.getLog(DataSources.class);
    private static ReentrantLock lock = new ReentrantLock();

    public static final String DEFAULT_DB = "default";

    private static final Map<String, DataSource> DATA_SOURCES = new HashMap<>();

    public static DataSource getDataSource() {
        return getDataSource(DEFAULT_DB);
    }

    public static DataSource getDataSource(String db) {
    	if(db == null){
    		db = DEFAULT_DB;
    	}
        DataSource existingDs = DATA_SOURCES.get(db);
        if (existingDs != null) {
            return existingDs;
        }

        lock.lock();
        try {
        	existingDs = DATA_SOURCES.get(db);
        	
            if (existingDs != null) {
            	return existingDs;
            }
            
            String dbSuffix = DEFAULT_DB.equals(db) ? "" : ("." + db);

            Configuration config = Config.getConfig();

            String jdbcDrive = config.getString("datasource.jdbc.driver" + dbSuffix);
            String jdbcUrl = config.getString("datasource.jdbc.url" + dbSuffix);
            String userName = config.getString("datasource.user" + dbSuffix);
            String password = config.getString("datasource.password" + dbSuffix);
            boolean poolPreparedStatement = config.getBoolean("datasource.poolPreparedStatements" + dbSuffix, true);
            int maxActive = config.getInt("datasource.maxActive" + dbSuffix, 40);
            int maxIdle = config.getInt("datasource.maxIdle" + dbSuffix, maxActive);
            boolean defaultAutoCommit = config.getBoolean("datasource.defaultAutoCommit" + dbSuffix, true);
            long maxWait = config.getLong("datasource.maxwait" + dbSuffix, 20000);
            long minEvictableIdleTime = config.getLong("datasource.minEvictableIdelTime" + dbSuffix, 1000 * 60 * 10);

            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(jdbcDrive);
            ds.setUrl(jdbcUrl);
            ds.setUsername(userName);
            ds.setPassword(password);
            ds.setPoolPreparedStatements(poolPreparedStatement);
            ds.setMaxActive(maxActive);
            ds.setMaxIdle(maxIdle);
            ds.setDefaultAutoCommit(defaultAutoCommit);
            ds.setMaxWait(maxWait);
            ds.setAccessToUnderlyingConnectionAllowed(true);
            ds.setMinEvictableIdleTimeMillis(minEvictableIdleTime);

            existingDs = ds;
            DATA_SOURCES.put(db, existingDs);
        }catch(Exception e){
        	logger.error(db, e);            
        } finally {
            lock.unlock();
        }

        return existingDs;
    }
}
