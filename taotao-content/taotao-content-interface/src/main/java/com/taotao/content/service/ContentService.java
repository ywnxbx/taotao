package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

/**
 * 内容处理接口
 * @author 一万年行不行
 *
 */
public interface ContentService {
	//插入内容
	public TaotaoResult saveContent(TbContent content);
	
	//根据id显示内容
	public List<TbContent> getContentListByCatId(Long CategoryId);
	
	//显示内容
	public EasyUIDataGridResult getContentList(int page,int rows,Long categoryId);
	
	//编辑内容
	public TaotaoResult editContent(TbContent content);
	
	//删除内容
	public TaotaoResult deleteContent(String ids);
}
