package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;

@Controller
public class ContentController {
	
	@Autowired
	private ContentService contentService;
	
	/**
	 * 新增内容
	 * @param content
	 * @return
	 */
	@RequestMapping(value="/content/save",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult saveContent(TbContent content){
		//1.dubbo中引入服务
		//2.注入service
		//3.调用功能
		return contentService.saveContent(content);
	}
	
	@RequestMapping(value="/content/query/list",method=RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult getContentList(Integer page,Integer rows,Long categoryId){
		return contentService.getContentList(page, rows, categoryId);
	}
	
	//编辑内容
	@RequestMapping(value="/rest/content/edit",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult editContent(TbContent content){
		return contentService.editContent(content);
	}
	
	//删除内容
	@RequestMapping(value="/content/delete",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult deleteContent(String ids){
		return contentService.deleteContent(ids);
	}	
}
