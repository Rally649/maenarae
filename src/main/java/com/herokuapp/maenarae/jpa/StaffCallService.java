package com.herokuapp.maenarae.jpa;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herokuapp.maenarae.json.FormattedStaffCall;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffCallService {
	private final StaffCallRepository repository;

	public List<FormattedStaffCall> getCalls(String group) {
		List<StaffCall> calls = repository.findByGroupIdOrderByCallTime(group);
		List<FormattedStaffCall> formatted = calls.stream().map(this::format).collect(Collectors.toList());
		return formatted;
	}

	private FormattedStaffCall format(StaffCall call) {
		String group = call.getGroupId();
		String seat = call.getSeat();
		String sanitizedSeat = sanitize(seat);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String callTime = df.format(call.getCallTime());
		FormattedStaffCall formatted = new FormattedStaffCall(group, seat, sanitizedSeat, callTime);
		return formatted;
	}

	private String sanitize(String str) {
		String escapedNull = StringUtils.isEmpty(str) ? StringUtils.EMPTY : str;
		String sanitized = StringEscapeUtils.escapeHtml4(escapedNull);
		return sanitized;
	}

	public void deleteCall(String group, String seat) {
		StaffCallPK id = new StaffCallPK(group, seat);
		if (repository.existsById(id)) {
			repository.deleteById(id);
		}
	}

	public void recordCall(String group, String seat) {
		Calendar current = getCurrent();
		Date callTime = current.getTime();
		StaffCall call = new StaffCall(group, seat, callTime);
		repository.save(call);
	}

	public int getNumberOfWaiting(String group, String seat) {
		StaffCallPK id = new StaffCallPK(group, seat);
		Optional<StaffCall> call = repository.findById(id);
		if (!call.isPresent()) {
			return 0;
		}
		Date callTime = call.get().getCallTime();
		int num = repository.countByGroupIdAndCallTimeLessThanEqual(group, callTime);
		return num;
	}

	@Scheduled(cron = "0 0 * * * *", zone = "Asia/Tokyo")
	@Transactional
	public void takeInventory() {
		Calendar now = getCurrent();
		now.add(Calendar.DATE, -1);
		Date callTime = now.getTime();
		repository.deleteByCallTimeBefore(callTime);
	}

	@Value("${time.difference.hour}")
	private int timeDiff;

	private Calendar getCurrent() {
		Calendar current = Calendar.getInstance();
		current.add(Calendar.HOUR, timeDiff);
		return current;
	}
}
