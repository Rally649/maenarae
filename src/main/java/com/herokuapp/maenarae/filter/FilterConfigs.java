package com.herokuapp.maenarae.filter;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfigs {
	@Value("redirect.host.from")
	String fromHost;

	@Value("redirect.host.to")
	String toHost;

	@Bean
	public FilterRegistrationBean<Filter> redirectFilter() {
		Filter filter = new RedirectHostFilter(fromHost, toHost);
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(filter);
		return bean;
	}
}
