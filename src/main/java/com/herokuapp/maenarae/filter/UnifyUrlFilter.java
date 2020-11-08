package com.herokuapp.maenarae.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnifyUrlFilter extends OncePerRequestFilter {

	private String redirectHost;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestHost = getHost(request);
		if (!StringUtils.isEmpty(redirectHost) && !StringUtils.equals(requestHost, redirectHost)) {
			redirect(request, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private String getHost(HttpServletRequest request) {
		UriComponentsBuilder builder = getRequestUriBuilder(request);
		String host = builder.build().getHost();
		return host;
	}

	private void redirect(HttpServletRequest request, HttpServletResponse response) {
		String url = getRedirectUrl(request);
		response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
		response.setHeader(HttpHeaders.LOCATION, url);
	}

	private String getRedirectUrl(HttpServletRequest request) {
		UriComponentsBuilder builder = getRequestUriBuilder(request);
		builder.host(redirectHost);
		setParams(request, builder);
		String url = builder.build().toString();
		return url;
	}

	private UriComponentsBuilder getRequestUriBuilder(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		return builder;
	}

	private UriComponentsBuilder setParams(HttpServletRequest request, UriComponentsBuilder builder) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (String name : parameterMap.keySet()) {
			Object[] values = parameterMap.get(name);
			builder.queryParam(name, values);
		}
		return builder;
	}
}
