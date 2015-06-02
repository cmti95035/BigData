package com.cmti.analytics.hbase.mapping;

import java.lang.reflect.Field;

import com.cmti.analytics.hbase.dao.HBaseObject;
import com.cmti.analytics.hbase.task.mapreduce.util.StringArrayWritable;
import com.cmti.analytics.hbase.util.FamilyColumn;
 
/**
 * Single value column.
 * @author Guobiao Mo
 *
 * @param <T>
 * @param <P>
 */
public class SingleColumn<T extends HBaseObject, P> extends SingleProperty<T, P> implements IColumn<T> {

	protected FamilyColumn familyColumn;
	
	protected boolean readVersion;
	
	public SingleColumn(String columnFamily, 
			String column,
			Field field,
			boolean readVersion) {
		super(field, false);
		
		familyColumn = new FamilyColumn(columnFamily, column);

		this.readVersion = readVersion;
	}

	@Override
	public String getColumnFamily() {
		return familyColumn.getFamily();
	}

	@Override
	public String getColumn() {
		return familyColumn.getColumn();
	}

	@Override
	public String getFullName() {
		return familyColumn.getFullName();
	}

	@Override
	public byte[] getColumnFamilyBytes() {
		return familyColumn.getFamilyBytes();
	}

	@Override
	public byte[] getColumnBytes() {
		return familyColumn.getColumnBytes();
	}

	@Override
	public boolean readVersion() {
		return readVersion;
	}

	@Override
	public String toString() {
		return String.format("columnFamily = %s, column = %s, %s", familyColumn.getFamily(), familyColumn.getColumn(), super.toString());
	}

	@Override
	public int hashCode() {
		return getFullName().hashCode();
	}

	@Override
	public boolean equals(Object obj) { 
        if (obj instanceof SingleColumn<?, ?>) {
    		SingleColumn<T, P> other = (SingleColumn<T, P>) obj;
    		return getFullName().equals(other.getFullName());    	
        }
        
        return false;
	}

	@Override
	public boolean isDirty(T t) {
		return t.isDirty(getPropertyName());
	}

	public String getPropertyName() {
		return field.getName();
	}

	@Override
	public FamilyColumn getFamilyColumn() {
		return familyColumn;
	}

	public void setFamilyColumn(FamilyColumn familyColumn) {
		this.familyColumn = familyColumn;
	}

	public boolean isReadVersion() {
		return readVersion;
	}

	public void setReadVersion(boolean readVersion) {
		this.readVersion = readVersion;
	}

}
