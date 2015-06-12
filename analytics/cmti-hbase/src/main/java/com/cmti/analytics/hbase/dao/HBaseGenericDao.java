package com.cmti.analytics.hbase.dao;
 
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.genericdao.dao.DAOUtil;
import com.cmti.analytics.hbase.mapping.IColumn;
import com.cmti.analytics.hbase.mapping.MappingMetaData;
import com.cmti.analytics.hbase.util.FamilyColumn;
import com.cmti.analytics.hbase.util.HBaseConfig;
import com.cmti.analytics.hbase.util.HBaseUtil;
import com.cmti.analytics.util.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * To be extended by all Dao
 * @author Guobiao Mo
 *
 * @param <T> target domain
 * @param <P> key
 */
public class HBaseGenericDao<T extends HBaseObject, P> implements Closeable {

	protected static final Logger logger = LogManager.getLogger(HBaseGenericDao.class);

	protected MappingMetaData<T, P> mapping;
	
	protected Configuration config;
	protected HTable table;

	protected boolean autoFlush;//this controls table.setAutoFlush(autoFlush); and is also used for batch flush

	protected int batchSize = HBaseUtil.BATCH_SIZE;//batch size for update and delete
	protected int currentBatchedSize;
	protected List<Mutation> batch =new ArrayList<Mutation>(batchSize);

	/*
	 * if this class is directly instanced, have to pass in the Class<T> 
	 */
	public HBaseGenericDao(Class<T> clazz) {
		init(clazz, null);
	}

	public HBaseGenericDao(Class<T> clazz, Configuration config) {
		init(clazz, config);
	}

	/*
	 * We are able to get Class<T> for a subclass
	 */
	protected HBaseGenericDao() {
		init(null, null);
	}

	protected HBaseGenericDao(Configuration config) {
		init(null, config);
	}

	protected void init(Class<T> clazz, Configuration config){
		Class<T> persistentClass;
		if(clazz!=null){
			persistentClass = clazz;			
		}else{
			persistentClass = (Class<T>) DAOUtil.getTypeArguments(HBaseGenericDao.class, this.getClass()).get(0);
		}

		if(config!=null){
			this.config = config;			
		}else{
			this.config = HBaseConfig.getConfig();
		}
		
		mapping = new MappingMetaData<T, P>(persistentClass);
	}

	public void truncateAndOpen() throws IOException {//be careful when calling this in multiple threads/processes like MapReduce.
		currentBatchedSize = 0;

		String tableName = getTableName();
		table = new HTable(config, tableName);
		HTableDescriptor tableDescriptor  = table.getTableDescriptor();
		
		HBaseAdmin hbaseAdmin = new HBaseAdmin(config);
		hbaseAdmin.disableTable(tableName);
		hbaseAdmin.deleteTable(tableName);
		
		hbaseAdmin.createTable(tableDescriptor);
		
		hbaseAdmin.close();

		logger.info(String.format("truncateAndOpen new %s  (batchSize = %s, autoFlush = %s)", getClass().getSimpleName() , batchSize, autoFlush));
	}

	/**
	 * before calling open(), we can set table name
	 * @throws IOException
	 */
	public void open() throws IOException {
		currentBatchedSize = 0;

		table = new HTable(config, getTableName());
		table.setAutoFlush(autoFlush);

		logger.info(String.format("Opened new %s  (batchSize = %s, autoFlush = %s)", getClass().getSimpleName() , batchSize, autoFlush));
	}
 	
	public void close() {
		try {
			flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.closeQuietly(table);
			logger.info(getClass().getSimpleName() + " closed.");
		}
	}
	
	public void flush() throws IOException, InterruptedException {
		if(!batch.isEmpty()) {
			table.batch(batch);
			batch.clear();
		}

		currentBatchedSize = 0;
	}

	public List<IColumn<T>> getMappedColumns(){
		return mapping.getMappedColumns();
	}
	
	public T getByKey(P keys) throws IOException{		
		byte[] rowKey = keyToBytes(keys);
		return getByKey(rowKey);
	}

	public T getByKey(byte[] rowKey) throws IOException{
		Get get = new Get(rowKey);
		Result r = table.get(get);		
		T t = parseResult(r); 		
		return t;
	}
/*
	//return all rows the the key prefix
	public DaoScanner<T> getByKeyPrefix(byte[] rowKeyPrefix) throws IOException{
		byte[] end = rowKeyPrefix+1;
		return getByKeyRange(rowKeyPrefix, end);
	}
	*/
	public DaoScanner<T> getByKeyRange(byte[] startRow, byte[] stopRow) throws IOException{
		Scan scan = HBaseUtil.newOnePassMassScan();
		scan.setStartRow(startRow);
		scan.setStopRow(stopRow);
		
		return getDaoScanner(scan);
	}

	public long increment(byte[] rowKey, byte[] family, byte[] qualifier, long amount) throws IOException{		
		return  getTable().incrementColumnValue(rowKey, family, qualifier, amount);	
//		return  getTable().incrementColumnValue(rowKey, family, qualifier, amount, Durability.SKIP_WAL);
	}
	
	public void upsert(Collection<T> ts) throws IOException, InterruptedException {
		upsert(ts, null);
	}

	public void upsert(Collection<T> ts, Long timestamp) throws IOException, InterruptedException {
		if(ts==null){
			return;
		}
		
		for(T t : ts) {
			upsert(t, timestamp);
		}
	}

	//treat all columns dirty, and insert them
	public void insert(T t) throws IOException, InterruptedException {
		insert(t, (Long)null);
	}

	public void insert(T t, Long timestamp) throws IOException, InterruptedException {
		if(t==null){
			return;
		}
		t.setAllDirty();
		upsert(t, timestamp);
	}

	public void insert(T t, Date date) throws IOException, InterruptedException {
		insert(t, date==null? (Long)null:date.getTime());
	}

	//check if dirty, except for unmapped
	public void upsert(T t) throws IOException, InterruptedException {
		upsert(t, true);
	}

	public void upsert(T t, boolean doUnmapped) throws IOException, InterruptedException {
		upsert(t, null, doUnmapped);
	}

	public Put getPut(T t) throws IOException, InterruptedException {
		return getPut(t, null);
	}

	public Put getPut(T t, Long ts) throws IOException, InterruptedException {
		Pair<Put, Delete> pair = getUpdate(t, ts);
		return pair==null?null:pair.getLeft();
	}

	private Pair<Put, Delete> getUpdate(T t, Long timestamp) throws IOException, InterruptedException {
		return getUpdate(t, timestamp, true);
	}
	
	private Pair<Put, Delete> getUpdate(T t, Long timestamp, boolean doUnmapped) throws IOException, InterruptedException {
		if(t==null){
			return null;
		}
		
		byte[] key = getKey(t); 
		Put put = new Put(key);
		Delete delete = new Delete(key);
		mapping.getUpdate(put, delete, t, timestamp);
		
		Map<FamilyColumn, String> unmapped = t.getUnmapped();
		
		if(put.size()==0 && delete.size()==0 && (unmapped==null || unmapped.isEmpty())) {
			logger.debug("Skip upsert. Not dirty: "+t);
			return null;
		}
		
		//note that we don't check if unmapped is dirty TODO
		if(doUnmapped && unmapped != null && unmapped.isEmpty() == false) {
			Set<Map.Entry<FamilyColumn, String>> entries = unmapped.entrySet();
			for(Map.Entry<FamilyColumn, String> entry : entries){
				String valueObject = entry.getValue();
/*
				if(valueObject==null){//FIXME should allow null be inserted
					//logger.warn("valueObject==null for "+entry.getKey());	//example: "item_position":null			
					continue;
				}*/
				byte[] value = valueObject==null? null : Bytes.toBytes(valueObject);//all unmapped is stored in string format

				FamilyColumn fc = entry.getKey();
			
				byte[] cf = fc.getFamilyBytes();
				byte[] k = fc.getColumnBytes();
			
				if(timestamp==null){
					put.add(cf, k, value);
				}else{
					put.add(cf, k, timestamp, value);
				}
			}
		}

		return Pair.of(put, delete);
	}

	public void upsert(T t, Long timestamp) throws IOException, InterruptedException {
		upsert(t, timestamp, true);
		
	}
	public void upsert(T t, Date date) throws IOException, InterruptedException {
		upsert(t, date.getTime());
		
	}
		//this method is called for brand new row
		//In update/insert, i.e. upsert(), if a field is null, we need to clean it in HBase. Here we don't care.
	public void upsert(T t, Long timestamp, boolean doUnmapped) throws IOException, InterruptedException {
		Pair<Put, Delete> pair = getUpdate(t, timestamp, doUnmapped);
		if(pair==null){
			return;
		}
		updateHBase(pair.getLeft(), pair.getRight());
	}

	private void updateHBase(Mutation... hbaseUpdate) throws IOException, InterruptedException {
		for(Mutation row : hbaseUpdate) {
			if(row.size()!=0){
				batch.add(row);
				currentBatchedSize++;
			}
		}
		
		if(autoFlush || currentBatchedSize >= batchSize) {
			flush();
		}
	}

	public void delete(Collection<T> ts) throws IOException, InterruptedException {
		delete(ts, null);
	}

	public void delete(Collection<T> ts, Long timestamp) throws IOException, InterruptedException {
		for(T t : ts) {
			delete(t, timestamp);
		}
	}

	public void delete(T t) throws IOException, InterruptedException {
		delete(t, null);
	}

	/**
	 * Delete the T at the input timestamp.
	 * @param t
	 * @param timestamp
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void delete(T t, Long timestamp) throws IOException, InterruptedException {
		byte[] row = getKey(t);
		deleteByKey(row, timestamp);
	}

	public void deleteByKey(byte[] rowKey) throws IOException, InterruptedException {
		deleteByKey(rowKey, null);
	}
	
	public void deleteByKey(byte[] rowKey, Long timestamp) throws IOException, InterruptedException {
		Delete delete = timestamp == null ? new Delete(rowKey) : new Delete(rowKey, timestamp);
		updateHBase(delete);	
	}
	
	/**
	example usage see ScanTable.java, or method getAll().
	*/	
	public DaoScanner<T> getDaoScanner(Scan scan) throws IOException {
		ResultScanner rs = table.getScanner(scan);
		return new DaoScanner<T>(rs, mapping);
	}

	/**
	 * don't use this when table is huge.
	 * open this dao before calling this.
	 * @return
	 * @throws IOException
	 */
	public List<T> getAll() throws IOException {
		Scan scan = HBaseUtil.newOnePassMassScan();
				
		DaoScanner<T> daoScanner = getDaoScanner(scan);

		List<T> ret = new ArrayList<T>();
		T t= null;
		while( (t = daoScanner.next()) != null){
			ret.add(t);
		}
		return ret;
	}

	public T parseResult(Result r) {
		return mapping.parse(r);
	}

	/**
	 * convert keys p (in Object) to byte[]
	 * @param p
	 * @return
	 */
	public byte[] keyToBytes(P p){
		return mapping.keyToBytes(p);
	}

	public byte[] getKey(T t){
		return mapping.getKey(t);
	}

	/**
	 * 
	 * @param batchSize updates are flushed to hbase if count of update in buff reaches this size. 
	 */
	public void setWriteBatchSize(int batchSize) {
		if(batchSize < 1) {
			throw new RuntimeException("batchSize="+batchSize);
		}
		this.batchSize = batchSize;
	}
	
	/** This method override the table name defined in domain, and should be called before open()
	 */
	public void setTableName(String tableName){
		mapping.setTableName(tableName);
	}

	public String getTableName(){
		return mapping.getTableName();
	}
	
	////////////auto generated getter and setter//////////////
	public MappingMetaData<T, P> getMapping() {
		return mapping;
	}

	public void setMapping(MappingMetaData<T, P> mapping) {
		this.mapping = mapping;
	}

	public boolean isAutoFlush() {
		return autoFlush;
	}

	/**
	 * 
	 * @param autoFlush true means any update is flushed to hbase immediately.
	 */
	public void setAutoFlush(boolean autoFlush) {
		this.autoFlush = autoFlush;
	}

	public int getWriteBatchSize() {
		return batchSize;
	}

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}

}
