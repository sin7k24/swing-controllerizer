package com.oneitthing.swingcontrollerizer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <p>[概 要] </p>
 *
 * <p>[詳 細] </p>
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public class DataSourceConnector extends Connector {

	/**  */
	private Connection conn;


	/**
	 *
	 * @return
	 */
	public Connection getConnection() {
		return this.conn;
	}

	/**
	 *
	 * @param conn
	 */
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	/**
	 *
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	public void open() throws ClassNotFoundException, SQLException  {
		if (getConnection() == null) {
			Class.forName(getDriverFqcn());
			setConnection(DriverManager.getConnection(getUrl(), getUser(), getPassword()));
			getConnection().setAutoCommit(isAutoCommit());
		}
	}

	/**
	 *
	 * @throws SQLException
	 * @throws Exception
	 */
	public void close() throws SQLException {
		if (getConnection() != null) {
			getConnection().close();
			setConnection(null);
		}
	}

	/**
	 *
	 * @throws SQLException
	 * @throws Exception
	 */
	public void commit() throws SQLException {
		if (getConnection() != null) {
			getConnection().commit();
		}
	}

	/**
	 *
	 * @throws SQLException
	 * @throws Exception
	 */
	public void rollback() throws SQLException {
		if (getConnection() != null) {
			getConnection().rollback();
		}
	}
}
