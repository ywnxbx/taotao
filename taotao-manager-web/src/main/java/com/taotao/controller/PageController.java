package com.taotao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	
	/**
	 * 配置返回主页
	 * @return
	 */
	@RequestMapping("/")
	public String showIndex(){
		return "index";
	}
	
	
	/**
	 * 使用@PathVariable标签，可将传递的字符串看作为路径变量。
	 * 功能：将路径变量 同时当作 访问路径与返回路径 使用，
	 * 便利：多个功能中，当每个功能的 页面访问路径名 与 页面返回路径名 相同时，可同时篇配置多个调度控制。省时省力， 
	 * @param page
	 * @return
	 */
	@RequestMapping("/{page}")
	public String showPage(@PathVariable String page){
		return page;
	}
}
