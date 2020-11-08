package com.herokuapp.maenarae.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "redirect.host")
@Data
public class RedirectHostConfig {
	private String from;
	private String to;
}
