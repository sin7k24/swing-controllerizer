package com.oneitthing.swingcontrollerizer.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oneitthing.swingcontrollerizer.controller.ClientConfig;
import com.oneitthing.swingcontrollerizer.db.Connector;
import com.oneitthing.swingcontrollerizer.db.ConnectorFactory;
import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;

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
public class DatabaseCore extends BaseModel {

	/**  */
	public static final String DB_DATASOURCE = "db.datasource";

	/**  */
	public static final String DB_DRIVER_FQCN = "db.driver.fqcn";

	/**  */
	public static final String DB_URL = "db.url";

	/**  */
	public static final String DB_USER = "db.user";

	/**  */
	public static final String DB_PASSWORD = "db.password";

	/**  */
	public static final String DB_AUTO_COMMIT = "db.auto.commit";


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
	private boolean autoCommit = true;

	/**  */
	private Connector connector;

	/**  */
	private String sql;

	/**  */
	private List<Object> sqlParameters = new ArrayList<Object>();

	/**  */
	private PreparedStatement statement;

	/**  */
	private boolean asList;



	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getDatasource() {
		if(this.datasource == null) {
			ClientConfig config = getController().getClientConfig();
			return config.getDefaultDatabaseEnvironment().get(DB_DATASOURCE);
		}else{
			return this.datasource;
		}
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param datasource
	 */
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getDriverFqcn() {
		if(this.driverFqcn == null) {
			ClientConfig config = getController().getClientConfig();
			return config.getDefaultDatabaseEnvironment().get(DB_DRIVER_FQCN);
		}else{
			return this.driverFqcn;
		}
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param driverFqcn
	 */
	public void setDriverFqcn(String driverFqcn) {
		this.driverFqcn = driverFqcn;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getUrl() {
		if(this.url == null) {
			ClientConfig config = getController().getClientConfig();
			return config.getDefaultDatabaseEnvironment().get(DB_URL);
		}else{
			return this.url;
		}
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getUser() {
		if(this.user == null) {
			ClientConfig config = getController().getClientConfig();
			return config.getDefaultDatabaseEnvironment().get(DB_USER);
		}else{
			return this.user;
		}
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getPassword() {
		if(this.password == null) {
			ClientConfig config = getController().getClientConfig();
			return config.getDefaultDatabaseEnvironment().get(DB_PASSWORD);
		}else{
			return this.password;
		}
	}

	/**
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param autoCommit
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public List<Object> getSqlParameters() {
		return sqlParameters;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param sqlParameters
	 */
	public void setSqlParameters(List<Object> sqlParameters) {
		this.sqlParameters = sqlParameters;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameter
	 */
	public void addSqlParameter(Object parameter) {
		this.sqlParameters.add(parameter);
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public PreparedStatement getStatement() {
		return statement;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param statement
	 */
	public void setStatement(PreparedStatement statement) {
		this.statement = statement;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	public boolean isAsList() {
		return asList;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param asList
	 */
	public void setAsList(boolean asList) {
		this.asList = asList;
	}

	/**
 	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
     *
     */
	public DatabaseCore() throws Exception {
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
     *
     */
	public Connector getConnector() {
		return this.connector;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param jdbcconnector
	 */
	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 * @throws Exception
	 */
	protected boolean preproc() throws InstantiationException, IllegalAccessException,
		ClassNotFoundException, Exception
	{
		Connector connector = ConnectorFactory.getConnector(this);
		setConnector(connector);

		String sql = null;
		if((sql = presql()) != null) {
			setSql(sql);
		}

		getConnector().open();

		return true;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws SQLException
	 * @throws Exception
	 */
	protected void mainproc() throws SQLException, Exception {
		// データベース接続取得
		Connection con = getConnector().getConnection();
		// ステートメント作成
		PreparedStatement stmt = con.prepareStatement(getSql());
		// ステートメントにパラメータ付与
		sqlparam(stmt);
		setStatement(stmt);

		boolean bResult = execsql();

		Object result = null;

		if (bResult) {
			// 拡張モデルのフックポイント作成
			sqlresult();
			ResultSet resultSet = stmt.getResultSet();
			if(isAsList()) {
				List<Map<String, Object>> resultList = getRows(resultSet);
				result = resultList;
			}else{
				result = resultSet;
			}
		} else {
			// 拡張モデルのフックポイント作成
			sqlupdate();
			int updateCount = stmt.getUpdateCount();
			result = updateCount;
		}

		setResult(result);
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws Exception
	 */
	protected void postproc() throws Exception {
		getConnector().commit();

		ModelProcessEvent successEvent = new ModelProcessEvent(this);
		successEvent.setResult(getResult());
		fireModelSuccess(successEvent);

		fireModelFinished(new ModelProcessEvent(this));

	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws Exception
	 */
	protected void finalproc() {
		try {
			getConnector().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 */
	protected String presql() {
		return null;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param stmt
	 * @throws SQLException
	 * @throws Exception
	 */
	protected void sqlparam(Statement stmt) throws SQLException {
		List<Object> sqlParameters = getSqlParameters();

		for(int i=0; i<sqlParameters.size(); i++) {
			Object parameter = sqlParameters.get(i);
			((PreparedStatement)stmt).setObject(i+1, parameter);
		}
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	protected boolean execsql() throws SQLException  {
		return ((PreparedStatement)getStatement()).execute();
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws Exception
	 */
	protected void sqlresult() throws Exception {
	}

	/**
 	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
     *
     */
	protected void sqlupdate() {
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	protected List<Map<String, Object>> getRows(ResultSet rs) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> map = getRow(rs);
			list.add(map);
		}
		return list;
	}

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	protected Map<String, Object> getRow(ResultSet rs) throws Exception {
		Map<String, Object> map = null;

		if (rs != null) {
			map = new HashMap<String, Object>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int ii = 1; ii <= rsmd.getColumnCount(); ii++) {
				String columnname = rsmd.getColumnName(ii);
				Object columnvalue = rs.getObject(ii);

				map.put(columnname, columnvalue);
			}
		}

		return map;
	}
}
