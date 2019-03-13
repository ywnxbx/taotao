package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContentCategory;

public interface ContentCategoryService {
	
	//通过节点的ID查询该节点的子节点列表 	
	public List<EasyUITreeNode> getContentCategoryList(Long parentId);
	
	/** 	
	 * @param parentId   父节点id
 	 * @param name 		新增节点id
	 * @return
	 */
	//添加内容分类
	public TaotaoResult creatContentCategory(Long parentId,String name);
	
	//重命名分类
	public TaotaoResult renameContentCategory(Long id,String name);
	
	//删除分类
	public TaotaoResult deleteContentCategory(Long id);
}
