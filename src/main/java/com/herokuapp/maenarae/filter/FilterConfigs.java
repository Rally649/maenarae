package com.herokuapp.maenarae.filter;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfigs {
	@Value("${redirect.secure}")
	boolean secure;

	@Value("${redirect.host}")
	String redirectHost;

	@Bean
	public FilterRegistrationBean<Filter> redirectFilter() {
		Filter filter = new UnifyUrlFilter(secure, redirectHost);
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(filter);
		return bean;
	}
}
