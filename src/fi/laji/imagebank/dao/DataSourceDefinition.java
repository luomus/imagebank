package fi.laji.imagebank.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import fi.luomus.commons.db.connectivity.ConnectionDescription;

public class DataSourceDefinition {

	public static HikariDataSource initDataSource(ConnectionDescription desc) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(desc.url());
		config.setUsername(desc.username());
		config.setPassword(desc.password());
		config.setDriverClassName(desc.driver());

		config.setAutoCommit(false); // transaction mode: all inserts, updates, deletes etc must be committed

		config.setConnectionTimeout(15000); // 15 seconds
		config.setMaximumPoolSize(20);
		config.setIdleTimeout(60000); // 1 minute
		config.setMaxLifetime(300000); // 5 minutes

		return new HikariDataSource(config);
	}

}
