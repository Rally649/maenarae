package com.herokuapp.maenarae;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.herokuapp.maenarae.jpa.StaffCallService;

@Component
public class Cron {
	@Autowired
	StaffCallService service;

	@Scheduled(fixedRate = 3600000)
	public void takeInventory() {
		service.takeInventory();
	}
}
