package com.herokuapp.maenarae.jpa;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffCallService {
	private final StaffCallRepository repository;

	public List<StaffCall> getCalls(String group) {
		List<StaffCall> calls = repository.findByGroupIdOrderByCallTime(group);
		return calls;
	}

	public void deleteCall(String group, String seat) {
		StaffCallPK id = new StaffCallPK(group, seat);
		if (repository.existsById(id)) {
			repository.deleteById(id);
		}
	}

	public void recordCall(String group, String seat) {
		StaffCallPK id = new StaffCallPK(group, seat);
		if (!repository.existsById(id)) {
			Calendar current = Calendar.getInstance();
			Date callTime = current.getTime();
			StaffCall call = new StaffCall(group, seat, callTime);
			repository.save(call);
		}
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
		Calendar current = Calendar.getInstance();
		current.add(Calendar.DATE, -1);
		Date callTime = current.getTime();
		repository.deleteByCallTimeBefore(callTime);
	}
}