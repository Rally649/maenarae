package com.herokuapp.maenarae.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormattedStaffCall {
	private String group;

	private String seat;

	private String sanitizedSeat;

	private String callTime;
}
