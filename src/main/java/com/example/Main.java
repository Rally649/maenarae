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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.dao.QueueDAO;

@Controller
@SpringBootApplication
@EnableScheduling
public class Main {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}

	@RequestMapping("/")
	String index(HttpServletRequest request, Model model) throws URISyntaxException {
		String scheme = request.getScheme();
		String host = request.getServerName();
		int port = request.getServerPort();
		List<String> paths = Arrays.asList("user", "staff");
		String query = "group=";
		for (String path : paths) {
			URI uri = new URI(scheme, null, host, port, "/" + path, query, null);
			model.addAttribute(path, uri);
		}
		return "index";
	}

	@RequestMapping("/user")
	String user(@RequestParam String group, String seat, Model model) {
		model.addAttribute("group", group);
		model.addAttribute("seat", seat);
		return "user";
	}

	@RequestMapping("/staff")
	String staff(@RequestParam String group, Model model) {
		model.addAttribute("group", group);
		return "staff";
	}

	@Autowired
	private QueueDAO dao;

	@RequestMapping("/getCalls")
	@ResponseBody
	List<StaffCall> getCalls(@RequestParam String group) {
		List<StaffCall> calls = dao.getCallList(group);
		return calls;
	}

	@RequestMapping("/deleteCall")
	@ResponseBody
	String deleteCall(@RequestParam String group, @RequestParam String seat) {
		dao.deleteCall(group, seat);
		return "Done.";
	}

	@RequestMapping("/callStaff")
	@ResponseBody
	String callStaff(@RequestParam String group, @RequestParam String seat) {
		dao.recordCall(group, seat);
		return "Done.";
	}

	@RequestMapping("/getNumberOfWaiting")
	@ResponseBody
	int getNumberOfWaiting(@RequestParam String group, @RequestParam String seat) {
		int num = dao.getNumberOfWaiting(group, seat);
		return num;
	}
}
