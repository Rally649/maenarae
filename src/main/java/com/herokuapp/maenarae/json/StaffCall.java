package com.herokuapp.maenarae.json;

public class StaffCall {
	private String group;

	private String seat;

	private String sanitizedSeat;

	private String callTime;

	public StaffCall(String group, String seat, String sanitizedSeat, String callTime) {
		this.group = group;
		this.seat = seat;
		this.sanitizedSeat = sanitizedSeat;
		this.callTime = callTime;
	}

	public String getGroup() {
		return group;
	}

	public String getSeat() {
		return seat;
	}

	public String getSanitizedSeat() {
		return sanitizedSeat;
	}

	public String getCallTime() {
		return callTime;
	}
}
