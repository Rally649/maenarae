package com.herokuapp.maenarae.jpa;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffCallPK implements Serializable {
	private String groupId;
	private String seat;
}
