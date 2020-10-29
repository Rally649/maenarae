package com.herokuapp.maenarae.json;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "time.difference")
@Data
public class TimeDifferenceConfiguration {
	private int hour;
}
