package com.herokuapp.maenarae.json;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.herokuapp.maenarae.jpa.StaffCall;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormattedStaffCall {
	private String group;

	private String seat;

	private String sanitizedSeat;

	private String callTime;

	public FormattedStaffCall(StaffCall staffCall) {
		this.group = staffCall.getGroup();
		this.seat = staffCall.getSeat();
		this.sanitizedSeat = sanitize(seat);
		this.callTime = format(staffCall.getCallTime());
	}

	private String sanitize(String str) {
		return StringEscapeUtils.escapeHtml4(escapeNull(str));
	}

	private String escapeNull(String str) {
		return StringUtils.isEmpty(str) ? StringUtils.EMPTY : str;
	}

	private String format(Date time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(time);
	}

}
