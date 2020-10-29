package com.herokuapp.maenarae.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.herokuapp.maenarae.json.FormattedStaffCall;

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
		executeFlow(new Flow<Void>() {
			@Override
			public Void execute() {
				String sql = String.format("insert ignore queue values ( ?, ?, now() + interval %d hour);", timeDiff);
				executeUpdate(sql, group, seat);
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

	public List<FormattedStaffCall> getCalls(String group) {
		List<FormattedStaffCall> calls = executeFlow(new Flow<List<FormattedStaffCall>>() {
			@Override
			public List<FormattedStaffCall> execute() {
				String sql = "select `group`, `seat`, `call_time` from `queue` where `group` = ? order by `call_time`;";
				Converter<List<FormattedStaffCall>> converter = new Converter<List<FormattedStaffCall>>() {
					@Override
					public List<FormattedStaffCall> convert(ResultSet rs) throws SQLException {
						List<FormattedStaffCall> calls = new ArrayList<>();
						while (rs.next()) {
							String group = rs.getString("group");
							String seat = rs.getString("seat");
							String sanitizedSeat = sanitize(seat);
							String callTime = format(rs.getTimestamp("call_time"));
							FormattedStaffCall call = new FormattedStaffCall(group, seat, sanitizedSeat, callTime);
							calls.add(call);
						}
						return calls;
					}

					private String sanitize(String str) {
						return StringEscapeUtils.escapeHtml4(escapeNull(str));
					}

					private String escapeNull(String str) {
						return StringUtils.isEmpty(str) ? StringUtils.EMPTY : str;
					}

					private String format(Date time) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						return df.format(time);
					}
				};
				List<FormattedStaffCall> calls = executeQuery(converter, sql, group);
				return calls;
			}
		});
		return calls;
	}

	public void deleteCall(String group, String seat) {
		executeFlow(new Flow<Void>() {
			@Override
			public Void execute() {
				String sql = "delete from `queue` where `queue`.`group` = ? and `queue`.`seat` = ?;";
				executeUpdate(sql, group, seat);
				return null;
			}
		});
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
