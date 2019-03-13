package com.taotao.service;

import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;

/**
 * 商品分类的service的接口
 */
public interface ItemCatService {
	/**
	 * 根据父节点的id查询子节点的列表
	 * @param parentId
	 * @return
	 */
	public List<EasyUITreeNode> getItemCatListByParentId(Long parentId);
}
