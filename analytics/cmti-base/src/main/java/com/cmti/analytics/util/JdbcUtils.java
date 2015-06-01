package com.cmti.analytics.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * JDBC util
 * @author Guobiao Mo
 *
 */
public class JdbcUtils {
	
	private static final Logger logger = LogManager.getLogger(JdbcUtils.class);

	public static final void closeQuietly(Connection con) {
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.error("Error when closing connection", e);
			}
		}
	}

	public static final void closeQuietly(PreparedStatement ps) {
		if(ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error("Error when closing prepared statement", e);
			}
		}
	}

	public static final void closeQuietly(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("Error when closing resultset", e);
			}
		}
	}

	public static final void closeQuietly(ResultSet rs, PreparedStatement ps) {
		closeQuietly(rs);
		closeQuietly(ps);
	}

	public static final void closeQuietly(ResultSet rs, PreparedStatement ps, Connection con) {
		closeQuietly(rs);
		closeQuietly(ps);
		closeQuietly(con);
	}
}
