package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;



@Service
public class ContentCategoryServiceImpl  implements ContentCategoryService{
	
	//1.注入mapper
	@Autowired
	private TbContentCategoryMapper mapper;

	/**
	 * 内容分类列表展示
	 * 
	 * 实现步骤
	 * 1.注入mapper
	 * 2.创建example
	 * 3.设置条件
	 * 4.执行查询
	 * 5.转成EasyUITreeNode集合
	 * 6.返回
	 */
	public List<EasyUITreeNode> getContentCategoryList(Long parentId) {
	
		//2.创建example类
		TbContentCategoryExample example = new TbContentCategoryExample();
		
		//3.设置条件（使用example类创建条件容器，添加条件）
		Criteria criteria = example.createCriteria();  //使用example类创建条件容器
		criteria.andParentIdEqualTo(parentId);  //此方法相当于 select * from tbcontentcategory where parentId = ？
		
		//4.执行查询
		List<TbContentCategory> list = mapper.selectByExample(example);
		
		//5.将查询到的数据（TbContentCategory） 转成 树形节点（node）,并添加到树形集合中（EasyUITreeNode）
		//三种形式的变化  TbContentCategory --> node  --> EasyUITreeNode
		//创建树形集合
		List<EasyUITreeNode> nodes = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node =  new EasyUITreeNode();  //创建节点
			
			/*
			 * 设置节点数据
			 */
			node.setId(tbContentCategory.getId());  //设置节点id 分类id
			//判断此节点是否为父节点，并设置状态
			//使用三目运算符，若为父节点，状态为closed，否则状态为open
			node.setState(tbContentCategory.getIsParent()?"closed":"open");  
			//设置分类名称
			node.setText(tbContentCategory.getName());
			
			/*
			 * 将节点添加到集合中			
			 */
			nodes.add(node);
		}
		
		//6.返回树形结构
		return nodes;
	}

	
	/**
	 * 新建分类节点
	 */
	public TaotaoResult creatContentCategory(Long parentId, String name) {
		//1.创建TbContentCategory对象  补全属性
		TbContentCategory category = new TbContentCategory();
		category.setCreated(new Date());
		category.setIsParent(false);
		category.setName(name);
		category.setParentId(parentId);
		category.setSortOrder(1);
		category.setStatus(1);
		category.setUpdated(category.getCreated());
		
		//2.插入contentCategory表数据
		mapper.insertSelective(category);
	
		//判断当父节点为叶子节点时，需要更新为父节点
		TbContentCategory parent = mapper.selectByPrimaryKey(parentId);  //根据parentId得到父节点
		if(parent.getIsParent()==false){  //当父节点为叶子节点时
			parent.setIsParent(true);  //修改为父节点
			mapper.updateByPrimaryKeySelective(parent);  //更新修改的parent到数据库中
		}
		
		//3.返回taotaoResult 包含分类id，使用主键返回
		return TaotaoResult.ok(category);
	}


	/**
	 * 重命名节点
	 */
	public TaotaoResult renameContentCategory(Long id, String name) {
		TbContentCategory category = mapper.selectByPrimaryKey(id);  //根据节点id找到被修改的分类
		category.setName(name);   //设置名称
		mapper.updateByPrimaryKeySelective(category);  //保存到数据库
		return TaotaoResult.ok(category);
	}
	
	
	/**
	 * 删除节点
	 *  //判断是否为父节点  ，不是父节点，直接删除
	 *  	若为父节点，
	 *  		判断是否含有子节点。有，不允许删除。
	 *  	否则直接删除
	 * @param id
	 * @return
	 */
	public TaotaoResult deleteContentCategory(Long id){
		
		
		TbContentCategory category = mapper.selectByPrimaryKey(id);  //根据id找到分类
		
		if(!category.getIsParent()){  //不是父节点，直接删除该节点
			mapper.deleteByPrimaryKey(id);  
			return new TaotaoResult(200,"删除成功",null);
		}else{  //是父节点，查询是否含有子节点
			/*
			 * 使用exampl创建criteria容器，查询所有parentId=id的节点
			 */
			TbContentCategoryExample example = new TbContentCategoryExample();  //创建example类
			Criteria criteria = example.createCriteria();  //使用example类创建criteria容器
			criteria.andParentIdEqualTo(id);  //此方法相当于 select * from tbcontentcategory where parentId = ？
			List<TbContentCategory> list = mapper.selectByExample(example);  //执行查询 (查询所有父id = id的节点)
			
			if(list.isEmpty()){
				mapper.deleteByPrimaryKey(id);   //是父节点，但没有子节点，直接删除s
				return TaotaoResult.build(300, "删除失败");
			}  
			else{          //父节点，含有子节点，禁止删除 
				return TaotaoResult.build(300, "删除失败");
			}
		}
	}
}
