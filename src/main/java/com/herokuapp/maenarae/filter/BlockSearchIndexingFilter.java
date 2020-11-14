package com.herokuapp.maenarae.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class BlockSearchIndexingFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String path = request.getRequestURI();
		boolean isRoot = StringUtils.equals(path, "/");
		response.setHeader("X-Robots-Tag", isRoot ? "all" : "noindex");
		filterChain.doFilter(request, response);
	}
}
