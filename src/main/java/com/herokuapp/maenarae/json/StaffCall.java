package com.herokuapp.maenarae.json;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StaffCall {
	private String group;

	private String seat;

	private String callTime;

	public StaffCall(String group, String seat, Date callTime) {
		this.group = group;
		this.seat = seat;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.callTime = df.format(callTime);
	}

	public String getGroup() {
		return group;
	}

	public String getSeat() {
		return seat;
	}

	public String getCallTime() {
		return callTime;
	}
}
