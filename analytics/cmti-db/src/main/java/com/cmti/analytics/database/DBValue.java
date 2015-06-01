package com.cmti.analytics.database;

import com.cmti.analytics.util.DateUtil;
import com.cmti.analytics.util.JdbcUtils;

import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Utility class to keep track of system status/parameters.
 * @author Guobiao Mo
 */
public class DBValue {
//////get////////
	public static String getString(String name) {
		return getString(name, null);
	}
	
	public static String getString(String name, String defaultString) {
		if(StringUtils.isBlank(name)) {
			throw new RuntimeException("Input name is null");
		}
		Connection con = null;
		try {
			con = DataSources.getDataSource().getConnection();
			return getString(name, defaultString, con);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtils.closeQuietly(con);
		}
	}

	private static String getString(String name, String defaultString, Connection con) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("select * from db_value where name = ?");
			ps.setString(1, name);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString("value");
			}
			return defaultString;
		} finally {
			JdbcUtils.closeQuietly(rs);
			JdbcUtils.closeQuietly(ps);
		}
	}

	public static Date getDate(String name) {		
		String value = getString(name);
		if(value == null) {
			return null;
		}
		return DateUtil.parseGMTString(value);
	}

	public static Long getLong(String name, Long defaultValue) {
		Long ret = getLong(name);		
		return ret==null?defaultValue:ret;
	}

	public static Long getLong(String name) {		
		String value = getString(name);
		if(value == null) {
			return null;
		}
		return Long.parseLong(value);
	}

	public static Integer getInt(String name) {		
		String value = getString(name);
		if(value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}

	public static Integer getInt(String name, Integer defaultValue) {
		Integer ret = getInt(name);		
		return ret==null?defaultValue:ret;
	} 

	public static Boolean getBoolean(String name) {		
		String value = getString(name);
		if(value == null) {
			return null;
		}
		return Boolean.parseBoolean(value);
	}

	public static Boolean getBoolean(String name, Boolean defaultValue) {
		Boolean ret = getBoolean(name);		
		return ret==null?defaultValue:ret;
	}

    ///////////set////////////
	public static void setDate(String name, Date value) {
		setString(name, DateUtil.toGMTString(value));
	}

	public static void setLong(String name, Long value) {
		setString(name, String.valueOf(value));
	}

    public static void setInt(String name, Integer value) {
        setString(name, String.valueOf(value));
    }

    public static void setBoolean(String name, Boolean value) {
        setString(name, String.valueOf(value));
    }

	public static void setString(String name, String value) {
		if(StringUtils.isBlank(name)) {
			throw new RuntimeException("Input name is null");
		}
		if(value == null) {
			throw new RuntimeException("Input value is null");
		}
		Connection con = null;
		try {
			con = DataSources.getDataSource().getConnection();
			String oldValue = getString(name, null, con);
			if(oldValue == null) {
				insert(name, value, con);
			} else {
				update(name, value, con);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtils.closeQuietly(con);
		}
	}

	private static int insert(String name, String value, Connection con) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("insert into db_value(name, value) values(?, ?)");
			ps.setString(1, name);
			ps.setString(2, value);
			return ps.executeUpdate();
		} finally {
			JdbcUtils.closeQuietly(ps);
		}
	}

	/////update/////
	private static int update(String name, String value, Connection con) throws SQLException {//TODO other types
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("update db_value set value = ? where name = ?");
			ps.setString(1, value);
			ps.setString(2, name);
			return ps.executeUpdate();
		} finally {
			JdbcUtils.closeQuietly(ps);
		}
	}

	//////delete///////
	public static int delete(String name) {
		Connection con = null;
		try {
			con = DataSources.getDataSource
					().getConnection();
			return delete(name, con);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtils.closeQuietly(con);
		}
	}

	public static int delete(String name, Connection con) throws SQLException {
		if(StringUtils.isBlank(name)) {
			throw new RuntimeException("Input name is null");
		}
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("delete from db_value where name = ?");
			ps.setString(1, name);
			return ps.executeUpdate();
		} finally {
			JdbcUtils.closeQuietly(ps);
		}
	}
	public static void main(String[] args) {
		DBValue.setBoolean(new Date().toString(), true);//value);.insert(name, value, con)

		DBValue.setInt("nnn", 1);;//(new Date().toString(), true);//value);.insert(name, value, con)
		DBValue.setString("ddd", "value"+new Date());
	}
}