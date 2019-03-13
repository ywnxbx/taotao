package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {
	
	//查询商品列表
	public EasyUIDataGridResult getItemList(int page,int rows);
	
	
	//添加商品基本数据和描述数据
	public TaotaoResult saveItem(TbItem item,String desc);
	
	//根据id查询商品信息
	public TbItem getItemById(Long itemId);
	
	//根据商品id查询商品描述
	public TbItemDesc getItemDescById(Long itemId);
}
