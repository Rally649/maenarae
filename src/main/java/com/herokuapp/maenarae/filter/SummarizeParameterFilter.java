package com.herokuapp.maenarae.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

public class SummarizeParameterFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		Map<String, String[]> parameterMap = request.getParameterMap();
		boolean isContainsEmpty = parameterMap.values().stream().filter(this::containsEmpty).findAny().isPresent();
		boolean isDuppledParams = parameterMap.values().stream().anyMatch(params -> params.length > 1);

		if (isContainsEmpty || isDuppledParams) {
			MultiValueMap<String, String> summarizedParams = summarize(parameterMap);
			String requestUrl = request.getRequestURL().toString();
			String redirectUrl = getRedirectUrl(requestUrl, summarizedParams);
			redirect(redirectUrl, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private boolean containsEmpty(String[] params) {
		return Arrays.asList(params).stream().filter(StringUtils::isEmpty).findAny().isPresent();
	}

	private MultiValueMap<String, String> summarize(Map<String, String[]> parameterMap) {
		MultiValueMap<String, String> summarizedParams = CollectionUtils.toMultiValueMap(new HashMap<>());
		parameterMap.forEach((name, params) -> {
			String joinedParams = joinParams(params);
			if (StringUtils.isNotEmpty(joinedParams)) {
				summarizedParams.put(name, Arrays.asList(joinedParams));
			}
		});
		return summarizedParams;
	}

	private String joinParams(String[] params) {
		return Arrays.asList(params).stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(","));
	}

	private String getRedirectUrl(String requestUrl, MultiValueMap<String, String> summarizedParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
		builder.queryParams(summarizedParams);
		String redirectUrl = builder.build().toString();
		return redirectUrl;
	}

	private void redirect(String redirectUrl, HttpServletResponse response) {
		response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
		response.setHeader(HttpHeaders.LOCATION, redirectUrl);
	}
}