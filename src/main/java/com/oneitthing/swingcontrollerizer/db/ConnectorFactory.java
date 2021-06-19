package com.oneitthing.swingcontrollerizer.db;


import com.oneitthing.swingcontrollerizer.model.DatabaseCore;

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
public class ConnectorFactory {

	public static String DATASOURCE_CONNECTOR_FQCN = "com.oneitthing.swingcontrollerizer.db.DataSourceConnector";

	public static String JDBC_CONNECTOR_FQCN = "com.oneitthing.swingcontrollerizer.db.JdbcConnector";


	public static Connector getConnector(DatabaseCore dbCore) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connector ret = null;

		ret = createConnector(dbCore);

		return ret;
	}

	private static Connector createConnector(DatabaseCore dbCore) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connector c = null;

		if(dbCore.getDatasource() != null) {
			c = (Connector)Class.forName(DATASOURCE_CONNECTOR_FQCN).newInstance();
			c.setDatasource(dbCore.getDatasource());
		}else{
			c = (Connector)Class.forName(JDBC_CONNECTOR_FQCN).newInstance();
			c.setDriverFqcn(dbCore.getDriverFqcn());
			c.setUrl(dbCore.getUrl());
		}

		c.setUser(dbCore.getUser());
		c.setPassword(dbCore.getPassword());
		c.setAutoCommit(dbCore.isAutoCommit());

		return c;
	}
}
