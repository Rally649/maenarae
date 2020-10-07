package com.example.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class QueueDAO extends AbstractDAO {

	public QueueDAO(DataSource dataSource) throws SQLException {
		super(dataSource);
		createTable();
	}

	private void createTable() throws SQLException {
		executeFlow(new Flow<Void>() {
			@Override
			public Void execute() throws SQLException {
				StringBuffer sql = new StringBuffer();
				sql.append("CREATE TABLE IF NOT EXISTS `queue` (");
				sql.append("`group` VARCHAR(100) NOT NULL,");
				sql.append("`seat` VARCHAR(100) NOT NULL,");
				sql.append("`call_time` TIMESTAMP NOT NULL,");
				sql.append("PRIMARY KEY (`group`, `seat`),");
				sql.append("UNIQUE INDEX `group_UNIQUE` (`group` ASC),");
				sql.append("UNIQUE INDEX `seat_UNIQUE` (`seat` ASC));");
				executeUpdate(sql.toString());
				return null;
			}
		});
	}

	public void recordCall(String group, String seat) throws SQLException {
		executeFlow(new Flow<Void>() {
			@Override
			public Void execute() throws SQLException {
				if (!isContained(group, seat)) {
					String sql = "INSERT queue VALUES ( ?, ?, now());";
					executeUpdate(sql, group, seat);
				}
				return null;
			}

			private boolean isContained(String group, String seat) throws SQLException {
				StringBuffer sql = new StringBuffer();
				sql.append("select count(*) from `queue`");
				sql.append("where `queue`.`group` = ? and `queue`.`seat` = ?;");
				Converter<Boolean> converter = new Converter<Boolean>() {
					@Override
					public Boolean convert(ResultSet rs) throws SQLException {
						rs.next();
						int num = rs.getInt(1);
						return num > 0;
					}
				};
				return executeQuery(converter, sql.toString(), group, seat);
			}
		});
	}

	public int getNumberOfWaiting(String group, String seat) throws SQLException {
		int num = executeFlow(new Flow<Integer>() {
			@Override
			public Integer execute() throws SQLException {
				StringBuffer sql = new StringBuffer();
				sql.append("select count(*) from `queue`");
				sql.append("where `queue`.`call_time` <= (");
				sql.append("select `queue`.`call_time` from `queue`");
				sql.append("where `queue`.`group` = ? and `queue`.`seat` = ?);");
				Converter<Integer> converter = new Converter<Integer>() {
					@Override
					public Integer convert(ResultSet rs) throws SQLException {
						rs.next();
						int num = rs.getInt(1);
						return num;
					}
				};
				int num = executeQuery(converter, sql.toString(), group, seat);
				return num;
			}
		});
		return num;
	}

}
