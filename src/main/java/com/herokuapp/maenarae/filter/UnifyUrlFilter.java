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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnifyUrlFilter extends OncePerRequestFilter {

	private boolean isSecure;
	private String redirectHost;

	private final String HTTP = "http";
	private final String HTTPS = "https";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		UriComponents uri = getUriComponents(request);
		String scheme = uri.getScheme();
		String requestHost = uri.getHost();

		boolean isRedirectHostEmpty = StringUtils.isEmpty(redirectHost);
		boolean isValidSecureRedirect = isSecure && StringUtils.equals(scheme, HTTP);
		boolean equalsHosts = StringUtils.equals(requestHost, redirectHost);

		if (!isRedirectHostEmpty && (isValidSecureRedirect || !equalsHosts)) {
			redirect(request, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private UriComponents getUriComponents(HttpServletRequest request) {
		UriComponentsBuilder builder = getRequestUriBuilder(request);
		UriComponents uri = builder.build();
		return uri;
	}

	private void redirect(HttpServletRequest request, HttpServletResponse response) {
		String url = getRedirectUrl(request);
		response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
		response.setHeader(HttpHeaders.LOCATION, url);
	}

	private String getRedirectUrl(HttpServletRequest request) {
		UriComponentsBuilder builder = getRequestUriBuilder(request);
		builder.scheme(isSecure ? HTTPS : HTTP);
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
		parameterMap.forEach((name, values) -> {
			builder.queryParam(name, (Object[]) values);
		});
		return builder;
	}
}
