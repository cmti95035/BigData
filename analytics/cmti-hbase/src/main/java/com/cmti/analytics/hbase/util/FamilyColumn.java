package com.cmti.analytics.hbase.util;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Represent a key in a cell
 * @author Guobiao Mo
 *
 */
public class FamilyColumn implements Comparable<FamilyColumn>{

	private byte[] familyBytes;
	private byte[] columnBytes;
	private String family;
	private String column;

	/**
	 * fullName = "d:d_ean"
	 * @param fullName
	 */
	public FamilyColumn(String fullName) {		
		this(fullName.substring(0, fullName.indexOf(':')), fullName.substring(fullName.indexOf(':')+1));		
	}

	public FamilyColumn(String family, String column) {
		if(StringUtils.isBlank(family)) {
			throw new IllegalArgumentException("ColumnFamily cannot be null or empty");
		}

		if(StringUtils.isBlank(column)) {
			throw new IllegalArgumentException("Column cannot be null or empty");
		}
		
		this.family = family;
		this.column = column;

		familyBytes = Bytes.toBytes(family);
		columnBytes = Bytes.toBytes(column);		
	}
	public FamilyColumn(String family, byte[] columnBytes) {
		if(StringUtils.isBlank(family)) {
			throw new IllegalArgumentException("ColumnFamily cannot be null or empty");
		}
		
		this.family = family;
		this.column = Bytes.toString(columnBytes);

		this.familyBytes = Bytes.toBytes(family);
		this.columnBytes = columnBytes;		
	}

	public FamilyColumn(byte[] familyBytes, byte[] columnBytes) {//TODO do we need 2 copies?
		this.familyBytes = familyBytes;
		this.columnBytes = columnBytes;

		this.family = Bytes.toString(familyBytes);
		this.column = Bytes.toString(columnBytes);
	}

	public FamilyColumn(KeyValue cell) {
		this(cell.getFamily(), cell.getQualifier());
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

	public String getFullName() {
		return family +":"+ column;
	}

	@Override
	public int compareTo(FamilyColumn other) {
		int ret = getFamily().compareTo(other.getFamily());
		if(ret != 0) {
			return ret;
		}
		
		return getColumn().compareToIgnoreCase(other.getColumn());//when export, we don't want case sensitive ordering
	}

	@Override
	public int hashCode(){
		return (family+column).hashCode();
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof FamilyColumn){
			FamilyColumn other =(FamilyColumn)obj;
			return getFamily().equals(other.getFamily()) && getColumn().equals(other.getColumn());
		}else{
			return false;
		}
	}
	
//////////////////////////////auto gen//////////
	public byte[] getFamilyBytes() {
		return familyBytes;
	}

	public void setFamilyBytes(byte[] familyBytes) {
		this.familyBytes = familyBytes;
	}

	public byte[] getColumnBytes() {
		return columnBytes;
	}

	public void setColumnBytes(byte[] columnBytes) {
		this.columnBytes = columnBytes;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

}