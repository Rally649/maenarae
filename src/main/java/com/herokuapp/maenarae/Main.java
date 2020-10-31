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

package com.herokuapp.maenarae;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@SpringBootApplication
@EnableScheduling
public class Main {
	private enum Param {
		GROUP, SEAT
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}

	@RequestMapping("/")
	String index(String group, Model model) {
		UUID uuid = UUID.randomUUID();
		addAttribute(model, Param.GROUP, StringUtils.isEmpty(group) ? uuid : group);
		return "index";
	}

	@RequestMapping("/user")
	String user(@RequestParam String group, String seat, Model model) {
		addAttribute(model, Param.GROUP, group);
		addAttribute(model, Param.SEAT, seat);
		return "user";
	}

	@RequestMapping("/staff")
	String staff(@RequestParam String group, Model model) {
		addAttribute(model, Param.GROUP, group);
		return "staff";
	}

	private void addAttribute(Model model, Param param, Object value) {
		String paramName = getName(param);
		model.addAttribute(paramName, value);
	}

	private String getName(Enum<?> e) {
		String name = e.name();
		String lowerCase = name.toLowerCase();
		return lowerCase;
	}
}
