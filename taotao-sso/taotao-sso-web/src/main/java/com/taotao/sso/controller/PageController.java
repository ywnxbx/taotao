package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 跳转页面使用的controller
 */
@Controller
public class PageController {
	
	@RequestMapping("/page/{page}")
	public String showPage(@PathVariable String page){
		return page;
	}
}
