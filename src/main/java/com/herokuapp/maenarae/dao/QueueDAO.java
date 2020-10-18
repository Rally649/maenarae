package com.herokuapp.maenarae.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.herokuapp.maenarae.json.StaffCall;

@Component
public class QueueDAO extends AbstractDAO {

	@Value("${time.difference.hour}")
	private int timeDiff;

	@PostConstruct
	public void createTable() {
		executeFlow(new Flow<Void>() {
			@Override
			public Void execute() {
				StringBuffer sql = new StringBuffer();
				sql.append("CREATE TABLE IF NOT EXISTS `queue` (");
				sql.append("`group` VARCHAR(100) NOT NULL,");
				sql.append("`seat` VARCHAR(100) NOT NULL,");
				sql.append("`call_time` TIMESTAMP NOT NULL,");
				sql.append("PRIMARY KEY (`group`, `seat`),");
				sql.append("UNIQUE INDEX `group_seat_UNIQUE` (`group`, `seat`));");
				executeUpdate(sql.toString());
				return null;
			}
		});
	}

	public void recordCall(String group, String seat) {
		executeFlow(new FlowWithCheckingContain<Void>() {
			@Override
			public Void execute() {
				if (!isContained(group, seat)) {
					String sql = String.format("insert queue values ( ?, ?, now() + interval %d hour);", timeDiff);
					executeUpdate(sql, group, seat);
				}
				return null;
			}
		});
	}

	public int getNumberOfWaiting(String group, String seat) {
		int num = executeFlow(new Flow<Integer>() {
			@Override
			public Integer execute() {
				StringBuffer sql = new StringBuffer();
				sql.append("select count(*) from `queue` where `group` = ? and `call_time` <= (");
				sql.append("select `call_time` from `queue` where `group` = ? and `seat` = ?);");
				Converter<Integer> converter = new Converter<Integer>() {
					@Override
					public Integer convert(ResultSet rs) throws SQLException {
						rs.next();
						int num = rs.getInt(1);
						return num;
					}
				};
				int num = executeQuery(converter, sql.toString(), group, group, seat);
				return num;
			}
		});
		return num;
	}

	public List<StaffCall> getCallList(String group) {
		List<StaffCall> calls = executeFlow(new Flow<List<StaffCall>>() {
			@Override
			public List<StaffCall> execute() {
				String sql = "select `seat`, `call_time` from `queue` where `group` = ? order by `call_time`;";
				Converter<List<StaffCall>> converter = new Converter<List<StaffCall>>() {
					@Override
					public List<StaffCall> convert(ResultSet rs) throws SQLException {
						List<StaffCall> calls = new ArrayList<>();
						while (rs.next()) {
							String seat = rs.getString("seat");
							Date callTime = rs.getTimestamp("call_time");
							StaffCall call = new StaffCall(seat, callTime);
							calls.add(call);
						}
						return calls;
					}
				};
				List<StaffCall> calls = executeQuery(converter, sql, group);
				return calls;
			}
		});
		return calls;
	}

	public void deleteCall(String group, String seat) {
		executeFlow(new FlowWithCheckingContain<Void>() {
			@Override
			public Void execute() {
				if (isContained(group, seat)) {
					String sql = "delete from `queue` where `queue`.`group` = ? and `queue`.`seat` = ?;";
					executeUpdate(sql, group, seat);
				}
				return null;
			}
		});
	}

	private abstract class FlowWithCheckingContain<T> extends Flow<T> {
		protected boolean isContained(String group, String seat) {
			String sql = "select count(*) from `queue` where `group` = ? and `seat` = ?;";
			Converter<Boolean> converter = new Converter<Boolean>() {
				@Override
				public Boolean convert(ResultSet rs) throws SQLException {
					rs.next();
					int num = rs.getInt(1);
					return num > 0;
				}
			};
			return executeQuery(converter, sql, group, seat);
		}
	}

	@Scheduled(cron = "0 0 * * * *", zone = "Asia/Tokyo")
	public void takeInventory() {
		executeFlow(new Flow<Void>() {
			@Override
			public Void execute() {
				String sql = "delete from queue where now() + interval %d hour - interval 1 day > call_time";
				executeUpdate(String.format(sql, timeDiff));
				return null;
			}
		});
	}
}
