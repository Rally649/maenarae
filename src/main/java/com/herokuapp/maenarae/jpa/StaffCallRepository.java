package com.herokuapp.maenarae.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffCallRepository extends JpaRepository<StaffCall, StaffCallPK> {

	List<StaffCall> findByGroupIdOrderByCallTime(String groupId);

	int countByGroupIdAndCallTimeLessThanEqual(String groupId, Date callTime);

	long deleteByCallTimeBefore(Date callTime);
}
