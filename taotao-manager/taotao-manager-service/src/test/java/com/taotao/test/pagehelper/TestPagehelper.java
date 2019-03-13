package com.taotao.test.pagehelper;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;

public class TestPagehelper {
	
	/**
	 * 测试PageHelper
	 */
	@Test
	public void testhelper(){
		//1.初始化spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		
		//2.从容器中直接获得Mapper代理对象
		TbItemMapper itemMapper = context.getBean(TbItemMapper.class);
		
		//3.设置分页信息(起始页面，每页条目数)
		PageHelper.startPage(1, 4);   //此设置只对紧跟其后的第一条查询语句起作用  
		
		//4.执行查询（先创建条件对象，后执行查询）
		TbItemExample example = new TbItemExample(); //条件默认为 select * form tb_Item
		List<TbItem> list = itemMapper.selectByExample(example);  //执行查询
		

		//5.取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
		System.out.println(pageInfo.getTotal());
		
	
	}
}
