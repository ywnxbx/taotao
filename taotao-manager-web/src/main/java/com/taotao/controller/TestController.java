package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.service.TestService;

/**
 * 测试使用的Controller，测试查询当前时间
 * 
 * 1.在mvc.xml中使用dubbo 引入服务
 * 2.使用@Autowired标签注入服务
 * 3.调用服务方法
 * 
 * @author 一万年行不行
 *
 */
@Controller
public class TestController {
	
	@Autowired
	private TestService testService;
	
	/**
	 * 测试dubbo配置是否正常
	 * @return
	 */
	@RequestMapping("/test/queryNow")
	@ResponseBody
	public String queryNow(){
		return testService.queryNow();
	}

}
