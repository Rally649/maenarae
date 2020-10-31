package com.herokuapp.maenarae.jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(StaffCallPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffCall {
	@Id
	private String groupId;
	@Id
	private String seatId;
	private Date callTime;
}
