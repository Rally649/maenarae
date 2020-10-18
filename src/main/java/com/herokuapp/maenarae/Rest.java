package com.herokuapp.maenarae;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.herokuapp.maenarae.dao.QueueDAO;
import com.herokuapp.maenarae.json.StaffCall;

@RestController
public class Rest {
	@Autowired
	private QueueDAO dao;

	@RequestMapping("/getCalls")
	List<StaffCall> getCalls(@RequestParam String group) {
		List<StaffCall> calls = dao.getCallList(group);
		return calls;
	}

	@RequestMapping("/deleteCall")
	String deleteCall(@RequestParam String group, @RequestParam String seat) {
		dao.deleteCall(group, seat);
		return "Done.";
	}

	@RequestMapping("/callStaff")
	String callStaff(@RequestParam String group, @RequestParam String seat) {
		dao.recordCall(group, seat);
		return "Done.";
	}

	@RequestMapping("/getNumberOfWaiting")
	int getNumberOfWaiting(@RequestParam String group, @RequestParam String seat) {
		int num = dao.getNumberOfWaiting(group, seat);
		return num;
	}
}
