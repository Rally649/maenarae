package com.herokuapp.maenarae.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StaffCallRepository extends CrudRepository<StaffCall, StaffCallPK> {

	List<StaffCall> findByGroupIdOrderByCallTime(String groupId);

	int countByGroupIdAndCallTimeLessThanEqual(String groupId, Date callTime);

	long deleteByCallTimeBefore(Date callTime);
}
