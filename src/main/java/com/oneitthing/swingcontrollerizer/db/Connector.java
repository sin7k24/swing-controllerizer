package com.oneitthing.swingcontrollerizer.db;


import java.sql.Connection;

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
public abstract class Connector{

	/**  */
	private String datasource;

	/**  */
	private String driverFqcn;

	/**  */
	private String url;

	/**  */
	private String user;

	/**  */
	private String password;

	/**  */
	private boolean autoCommit;


	/**
	 *
	 * @return
	 */
	public String getDatasource() {
		return datasource;
	}

	/**
	 *
	 * @param datasource
	 */
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	/**
	 *
	 * @return
	 */
	public String getDriverFqcn() {
		return driverFqcn;
	}

	/**
	 *
	 * @param driverFqcn
	 */
	public void setDriverFqcn(String driverFqcn) {
		this.driverFqcn = driverFqcn;
	}

	/**
	 *
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 *
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 *
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 *
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 *
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 *
	 * @return
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 *
	 * @param autoCommit
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 *
	 * @return
	 */
	public abstract Connection getConnection();

	/**
	 *
	 * @param conn
	 */
	public abstract void setConnection(Connection conn);

	/**
	 *
	 * @throws Exception
	 */
	public abstract void open() throws Exception;

	/**
	 *
	 * @throws Exception
	 */
	public abstract void close() throws Exception;

	/**
	 *
	 * @throws Exception
	 */
	public abstract void commit() throws Exception;

	/**
	 *
	 * @throws Exception
	 */
	public abstract void rollback() throws Exception;
}
