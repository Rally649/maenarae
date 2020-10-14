/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.dao.QueueDAO;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//TODO コードを別クラスに分ける

@Controller
@SpringBootApplication
public class Main {

	private static final String GROUP = "group";

	private static final String SEAT = "seat";

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${time.difference.hour}")
	private int timeDiff;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private QueueDAO dao;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}

	@RequestMapping("/")
	String index(HttpServletRequest request, Map<String, Object> model) throws URISyntaxException {
		String scheme = request.getScheme();
		String host = request.getServerName();
		int port = request.getServerPort();
		List<String> paths = Arrays.asList("user", "staff");
		String query = GROUP + "=";
		for (String path : paths) {
			URI uri = new URI(scheme, null, host, port, "/" + path, query, null);
			model.put(path, uri);
		}
		return "index";
	}

	@RequestMapping("/user")
	String user(HttpServletRequest request, Map<String, Object> model) {
		setParameterToModel(request, model, GROUP, SEAT);
		return "user";
	}

	@RequestMapping("/staff")
	String staff(HttpServletRequest request, Map<String, Object> model) {
		setParameterToModel(request, model, GROUP);
		return "staff";
	}

	private void setParameterToModel(HttpServletRequest request, Map<String, Object> model, String... names) {
		for (String name : names) {
			String value = getParameter(request, name);
			model.put(name, value);
		}
	}

	@RequestMapping("/getCalls")
	@ResponseBody
	List<StaffCall> getCalls(@RequestParam(GROUP) String group) {
		List<StaffCall> calls = dao.getCallList(group);
		return calls;
	}

	@RequestMapping("/deleteCall")
	@ResponseBody
	void deleteCall(@RequestParam(GROUP) String group, @RequestParam(SEAT) String seat) {
		dao.deleteCall(group, seat);
	}

	private String getParameter(HttpServletRequest request, String name) {
		String parameter = request.getParameter(name);
		try {
			return StringUtils.isEmpty(parameter) ? StringUtils.EMPTY : URLDecoder.decode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//TODO エンコードできない場合の挙動を確認、仕様を吟味する
			return "エンコードできない文字が含まれています";
		}
	}

	@RequestMapping("/callStaff")
	@ResponseBody
	void callStaff(@RequestParam(GROUP) String group, @RequestParam(SEAT) String seat) {
		dao.recordCall(group, seat, timeDiff);
	}

	@RequestMapping("/getNumberOfWaiting")
	@ResponseBody
	int getNumberOfWaiting(@RequestParam(GROUP) String group, @RequestParam(SEAT) String seat) {
		int num = dao.getNumberOfWaiting(group, seat);
		return num;
	}

	@Bean
	public DataSource dataSource() {
		if (dbUrl == null || dbUrl.isEmpty()) {
			return new HikariDataSource();
		} else {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(dbUrl);
			config.setUsername(username);
			config.setPassword(password);
			return new HikariDataSource(config);
		}
	}

	@Bean
	public QueueDAO dao() {
		QueueDAO dao = new QueueDAO(dataSource);
		dao.createTable();
		return dao;
	}
}
