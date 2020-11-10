package com.herokuapp.maenarae.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "ajax.refresh")
@Data
public class AjaxRefreshConfig {
	private int cycle;
}
