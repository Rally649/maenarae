package com.herokuapp.maenarae.json;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StaffCall {
	private String seat;

	private String callTime;

	public StaffCall(String seat, Date callTime) {
		this.seat = seat;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.callTime = df.format(callTime);
	}

	public String getSeat() {
		return seat;
	}

	public String getCallTime() {
		return callTime;
	}
}
