package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.pojo.TbContentCategory;

/**
 * controller层
 * 
 * 1.使用dubbo在springmvc中引用 在service层中暴露的服务	
 * 2.注入service
 * 3.调用方法实现功能
 * @author 一万年行不行
 *
 */
@Controller
public class ContentCategoryController {
	
	/*
	 * url : '/content/category/list',
	 * method : "GET",
	 * 参数：id
	 */
	@Autowired
	private ContentCategoryService service;
	
	@RequestMapping(value="/content/category/list",method=RequestMethod.GET)
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value="id",defaultValue="0") Long parentId){  //设置参数别名为id，默认值为0
		
		//调用方法并返回
		return service.getContentCategoryList(parentId);
	}
	
	
	/**
	 * 添加节点
	 * @param parentId
	 * @param name
	 * @return
	 */
	// .post
	// /content/category/create
	@RequestMapping(value="/content/category/create", method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult createContentCategory(Long parentId,String name){
		return service.creatContentCategory(parentId, name);
	}
	
	
	/**
	 * 重命名节点
	 * @param id
	 * @param next
	 * @return 
	 */
	// .post
	// /content/category/update
	@RequestMapping(value="/content/category/update", method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult renameContentCategory(Long id,String name){
		return service.renameContentCategory(id, name);
	}
	
	
	/**
	 * 删除节点
	 * @param id
	 * @return
	 */
//	请求的url：/content/category/delete/
//	参数：id，当前节点的id。
//	响应的数据：json。TaotaoResult。
	@RequestMapping(value="/content/category/delete/",method=RequestMethod.POST) 
	@ResponseBody
	public TaotaoResult deleteContentCategory(Long id){
		return service.deleteContentCategory(id);
	}
}
