package com.taotao.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.mapper.TestMapper;
import com.taotao.service.TestService;

/**
 * 1.注入mapper
 * 2.调用mapper#queryNow方法
 * @author 一万年行不行
 *
 */
@Service
public class TestServiceImpl implements TestService {

	@Autowired
	private TestMapper testMapper;    	//1.注入mapper
	
	public String queryNow(){
		return testMapper.queryNow();   //2.调用mapper#queryNow方法
	}
}
