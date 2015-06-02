package com.cmti.analytics.hbase.mapping;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.cmti.analytics.hbase.annotation.Column;
import com.cmti.analytics.hbase.annotation.CompositeKey;
import com.cmti.analytics.hbase.annotation.Key;
import com.cmti.analytics.hbase.annotation.Table;
import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.util.AnnotationUtil;
import com.cmti.analytics.hbase.util.FamilyColumn;
import com.cmti.analytics.hbase.util.ReflectUtil;

/**
 * Maintain meta data of a domain mapping.
 * @author Guobiao Mo
 *
 * @param <T> domain object
 * @param <P> row key
 */
public class MappingMetaData<T extends HBaseObject, P> {	

	private static final Logger logger = LogManager.getLogger(MappingMetaData.class);

	protected Class<T> targetClass;
	protected String tableName;
	protected String defaultCf;
	protected boolean defaultReadVersion;	
	protected boolean hasUnmapped;
	
	protected IKey<T, P> mappedKey;
	protected List<IColumn<T>> mappedColumns;

	protected Map<FamilyColumn, IColumn<T>> mappedColumnMap;

	public MappingMetaData(Class<T> targetClass) {
		this.targetClass = targetClass;

		logger.info("Building Mapping for " + targetClass);

		buildTableInfo();//table name is read from mapping, but can be over rode later.

		buildKey();//setup mappedKey
		
		buildColumns();//setup mappedColumns and mappedColumnMap
	}
	
	/**
	 * get row key as a array of object
	 * used in ExportIterator for data export
	 * @param t
	 * @return
	 */
	public Object[] getRowKeys(T t) {
		Object objs = mappedKey.getValueObject(t);		
		
		if(objs instanceof Object[]){
			return (Object[]) objs;
		}else{
			return new Object[]{objs};
		}
	}
	
	/**
	 * used in ExportIterator
	 * @param t
	 * @param columns
	 * @return
	 */
	
	public List<Object> getColumnValues(T t, List<String> columns){
		List<Object> ret=new ArrayList<Object>();
		
		if(columns==null || columns.size()==0){
			for(IColumn<T> mc : mappedColumns){//all mapped columns
				Object obj = mc.getValueObject(t); 
				ret.add(obj);
			}
		}else{
			for(String column : columns) {
				IColumn<T> mc = mappedColumnMap.get(new FamilyColumn(column));
				if(mc==null){
					logger.error("Can not find column "+column+" in "+getTableName());
				}else{
					Object obj = mc.getValueObject(t); 
					ret.add(obj);				
				}
			}
		}
		
		return ret;
	}

	/**
	 * convert row keys p (in Object) to byte[]
	 * @param p
	 * @return
	 */
	public byte[] keyToBytes(P p){
		return getMappedKey().keyToBytes(p);
	}

	/**
	 * 
	 * @param t
	 * @return t's row key in byte[]
	 */
	public byte[] getKey(T t){
		return getMappedKey().getKey(t);
	}

	/**
	 * Parse the input HBase result and set related info in the returned object
	 * @param obj to which the info is going to be set
	 */
	public T parse(Result r) {
		if(r == null || r.size() == 0) {
			return null;
		}

		T t = newTargetObject();
		
		//key
		byte[] rowKey = r.getRow();
		
		try{
			mappedKey.populate(t, rowKey);
		}catch(Exception e) {
			logger.error(r, e);
			return null;
		}
		
		//mapped column
		for(IColumn<T> mc : mappedColumns) {
			KeyValue kv = r.getColumnLatest(mc.getColumnFamilyBytes(), mc.getColumnBytes());
			if(kv == null){
				continue;
			}
			byte[] value = kv.getValue();
			mc.populate(t, value);

			if(mc.readVersion()) {
				t.setVersion(mc.getFamilyColumn(), kv.getTimestamp());
			}
		}
		
		if(hasUnmapped){
			parseUnmapped(r, t);
		}
		
		t.clearDirty();

		return t;
	}

	private boolean mappedContain(FamilyColumn fc) {
		for(IColumn<T> mc : mappedColumns) {
			if(mc.getFamilyColumn().equals(fc)){
				return true;
			}
		}
		
		return false;
	}
	
	private void parseUnmapped(Result r, T t){
		KeyValue[] kvs = r.raw();
		if(kvs != null && kvs.length > 0) {
			Map<FamilyColumn, String> unmapped=t.getUnmapped();
			for(KeyValue kv : kvs){
				FamilyColumn fc = new FamilyColumn(kv);
				
				if(mappedContain(fc) == false){//if this is not a mapped column, add it to unmapped
					unmapped.put(fc, Bytes.toString(kv.getValue()));//if needed, we can have other unmapped groups like unmappedLong, etc.
				}
			}
		}		
	}
	
	public List<T> parse(Result[] rs) {
									
		if(rs == null || rs.length == 0) {
			return null;
		}

		List<T> ret = new ArrayList<T>();
		int i=0;
		for(Result r:rs){
			ret.add(parse(r));
		}
		
		return ret;
	}

	public void getUpdate(Put put, Delete delete, T t, Long timestamp) {
		if(t == null) {
			return;
		}

		byte[] key = mappedKey.getKey(t);
		if(key == null) {
			throw new RuntimeException("Row key is null " +t);
		}

		/*
		 * List<Field> fields = mappedKey.getFields();
		 * List<Object> keys = mappedKey.getKeyValues(t);
		 * for(int i =0 ; i++; i<fields.size()){
		 * 	if(field column is not empty){
		 * 		put.add(column,, keys.get(i));
		 *  } 
		 * }
		 * 	
		 */
		for(IColumn<T> mc : mappedColumns) {
			if(mc.isDirty(t)==false) {
				continue;
			}

			byte[] value = mc.getValueBytes(t);
			boolean isEmpty = ArrayUtils.isEmpty(value);
			
			if(timestamp != null) {
				if(isEmpty){
					delete.deleteColumn(mc.getColumnFamilyBytes(), mc.getColumnBytes(), timestamp);
				}else{
					put.add(mc.getColumnFamilyBytes(), mc.getColumnBytes(), timestamp, value);
				}
			} else {
				if(isEmpty){
					delete.deleteColumns(mc.getColumnFamilyBytes(), mc.getColumnBytes());
				}else{
					put.add(mc.getColumnFamilyBytes(), mc.getColumnBytes(), value);
				}
			}
		}		
	}

////////////building///////////////////////////
	protected void buildTableInfo() {
		tableName = (String) AnnotationUtil.getClassAnnotationValue(targetClass, Table.class, "name"); 
		defaultCf = (String) AnnotationUtil.getClassAnnotationValue(targetClass, Table.class, "defaultCf"); 
		defaultReadVersion = (Boolean) AnnotationUtil.getClassAnnotationValue(targetClass, Table.class, "readVersion"); 
		hasUnmapped = (Boolean) AnnotationUtil.getClassAnnotationValue(targetClass, Table.class, "hasUnmapped"); 
	}

	protected void buildKey() {
		List<Field> SingleKeyFields = AnnotationUtil.getAnnotationFields(targetClass, Key.class);
		List<Field> CompositeKeyFields = AnnotationUtil.getAnnotationFields(targetClass, CompositeKey.class);
		
		if(SingleKeyFields.size() > 1){
			throw new RuntimeException("Only 1 @Key is allowed. "+targetClass.getName());			
		}else if(SingleKeyFields.size() == 1){
			if(CompositeKeyFields.size() > 0){
				throw new RuntimeException("Cannot have both @Key and @CompositeKey in (including super classes) "+targetClass.getName());			
			}
			buildSingleKey(SingleKeyFields.get(0));
			return;
		}

		if(CompositeKeyFields.size() == 0){
			throw new RuntimeException("No row key is defined for "+targetClass.getName());			
		}else if(CompositeKeyFields.size() == 1){
			throw new RuntimeException("Only 1 @CompositeKey is defined. Use @Key "+targetClass.getName());			
		}else{
			SortedSet<Field> sortedCompositeKeyFields = sort(CompositeKeyFields);		
			buildCompositeKey(sortedCompositeKeyFields);
			return;
		}
	}

	//sort CompositeKeys by "order"
	private SortedSet<Field> sort(List<Field> keyFields) {
		TreeSet<Field> orders = new TreeSet<Field>(new KeyOrderComparator());
		orders.addAll(keyFields);

		//sanity check
		//Prevent 0 1 1 2 (there are 2 same orders)
		if(orders.size()!=keyFields.size()){
			throw new RuntimeException("have same order");	
		}

		//Prevent 1 2 3 (order not starts from 0)
		if(orders.first().getAnnotation(CompositeKey.class).order() != 0){
			throw new RuntimeException("order must start from 0");	
		}

		//Prevent  0  3 6 (there is a gap)
		if(orders.last().getAnnotation(CompositeKey.class).order() != orders.size()-1){
			throw new RuntimeException("gap in orders");	
		}
		
		return orders;
	}

	private static class KeyOrderComparator implements Comparator<Field> {
		@Override
		public int compare(Field field0, Field field1) {
			CompositeKey key0 = field0.getAnnotation(CompositeKey.class);
			CompositeKey key1 = field1.getAnnotation(CompositeKey.class);
			
			return key0.order() - key1.order();
		}		
	}
	
	protected void buildSingleKey(Field field) {	
		mappedKey = new SingleKey(field);//got yellow line since we don't pass in T and P info
	}

	protected void buildCompositeKey(SortedSet<Field> orderedKeyFields) {
		mappedKey = new CompoKey<T, P>(orderedKeyFields);				
	}

	protected void buildColumns() {
		mappedColumns = new ArrayList<IColumn<T>>();
		mappedColumnMap = new HashMap<FamilyColumn, IColumn<T>>();

		List<Field> columnFields = AnnotationUtil.getAnnotationFields(targetClass, Column.class);

		for(Field field : columnFields) {
			IColumn<T> column = buildMappedColumn(field);
			mappedColumns.add(column);
			
			mappedColumnMap.put(column.getFamilyColumn(), column);
		}	 
	}

	private final IColumn<T> buildMappedColumn(Field field){
		Column colAnnotation = field.getAnnotation(Column.class);
		
		boolean thisReadVersion = defaultReadVersion;
		if(thisReadVersion == false){
			thisReadVersion = colAnnotation.readVersion();
		}		
		
		String thisCf = colAnnotation.cf();
		
		if(StringUtils.isBlank(thisCf)){
			thisCf = defaultCf;			
		}
		
		String col =colAnnotation.value();//Hbase column name
		
		return new SingleColumn(thisCf, col, field, thisReadVersion);
	}

	protected T newTargetObject() {
		return ReflectUtil.newInstance(targetClass);
	}
	///////////////auto gen ////////////////////
	public Class<T> getTargetClass() {
		return targetClass;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String name) {
		tableName = name;
	}

	public IKey<T, P> getMappedKey() {
		return mappedKey;
	}

	public List<IColumn<T>> getMappedColumns() {
		return mappedColumns;
	}
	

	public static Logger getLogger() {
		return logger;
	}

	public String getMappedTable() {
		return tableName;
	}

	public void setMappedTable(String mappedTable) {
		this.tableName = mappedTable;
	}

	public String getDefaultCf() {
		return defaultCf;
	}

	public void setDefaultCf(String defaultCf) {
		this.defaultCf = defaultCf;
	}

	public boolean isDefaultReadVersion() {
		return defaultReadVersion;
	}

	public void setDefaultReadVersion(boolean defaultReadVersion) {
		this.defaultReadVersion = defaultReadVersion;
	}

	public void setTargetClass(Class<T> targetClass) {
		this.targetClass = targetClass;
	}

	public void setMappedKey(IKey<T, P> mappedKey) {
		this.mappedKey = mappedKey;
	}

	public void setMappedColumns(List<IColumn<T>> mappedColumns) {
		this.mappedColumns = mappedColumns;
	}

	
}