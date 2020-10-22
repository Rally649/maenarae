package com.herokuapp.maenarae.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * DAOの抽象クラス。
 * {@link Flow}を実装し、{@link #executeFlow(Flow)}で実行。<br>
 * {@link Connection} {@link PreparedStatement} {@link ResultSet}などのcloseは、このクラス内で実施している。
 */
@Repository
public abstract class AbstractDAO {

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.hikari.max-lifetime}")
	private long maxLifetimeMs;

	@Autowired
	private DataSource dataSource;

	@Bean
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(dbUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setMaxLifetime(maxLifetimeMs);
		return new HikariDataSource(config);
	}

	/**
	 * flowで実装された処理内容を実施する。
	 * @param flow 処理内容
	 */
	@Transactional
	protected <T> T executeFlow(Flow<T> flow) {
		try (Connection con = dataSource.getConnection()) {
			flow.setConnection(con);
			return flow.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 同一の{@link Connection}で実施する処理を実装する
	 */
	protected abstract class Flow<T> {
		private Connection con;

		public void setConnection(Connection con) {
			this.con = con;
		}

		public abstract T execute();

		protected <U> U executeQuery(Converter<U> converter, String sql, String... parameters) {
			try (PreparedStatement smt = createStatement(sql, parameters);
					ResultSet rs = smt.executeQuery()) {
				return converter.convert(rs);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		protected void executeUpdate(String sql, String... parameters) {
			try (PreparedStatement smt = createStatement(sql, parameters)) {
				smt.executeUpdate();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		private PreparedStatement createStatement(String sql, String... parameters) throws SQLException {
			PreparedStatement smt = con.prepareStatement(sql);
			int index = 1;
			for (String parameter : parameters) {
				smt.setString(index++, parameter);
			}
			return smt;
		}
	}

	/**
	 * 実行結果の{@link ResultSet}に関する処理を実装する。
	 * @param <T> 戻り値の型
	 */
	protected interface Converter<T> {
		T convert(ResultSet rs) throws SQLException;
	}
}
