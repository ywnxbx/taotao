package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;


@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper mapper;
	
	@Autowired
	private JedisClient jedisClient;	
	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;

	
	//保存内容
	public TaotaoResult saveContent(TbContent content) {
		//1.注入mapper
		//2.补全其他属性
		content.setCreated(new Date());  //设置内容表创建日期
		content.setUpdated(content.getCreated());  //设置内容表更新日期
		mapper.insertSelective(content);  //插入内容表
		
		//缓存同步
		try{
			jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());  //新填入内容时,需要清空
			System.out.println("插入时,清空缓存");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.ok();
	}

	//根据id得到内容
	public List<TbContent> getContentListByCatId(Long categoryId) {

		//查询缓存
		try {
			String json = jedisClient.hget(CONTENT_KEY, categoryId + "");
			//判断json是否为空
			if (StringUtils.isNotBlank(json)) {    //包为 long3下的包
				System.out.println("已有缓存");
				//把json转换成list
				return JsonUtils.jsonToList(json, TbContent.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//1.注入mapper
		//2.创建example
		TbContentExample example = new TbContentExample();
		//3.创建criteria
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);  //设置查询条件
		List<TbContent> list = mapper.selectByExample(example);  //执行查询条件
		
		//向缓存中添加数据
		try {
			jedisClient.hset(CONTENT_KEY, categoryId + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return list;  //返回查询结果
	}

	//内容显示
	public EasyUIDataGridResult getContentList(int page, int rows, Long categoryId) {
			
		//1.设置分页数据
		PageHelper.startPage(page, rows);
		
		//2.执行查询
		TbContentExample example = new TbContentExample();
		example.createCriteria().andCategoryIdEqualTo(categoryId);  //查询CategoryId下的内容
		List<TbContent> list = mapper.selectByExample(example);
		
		//取分页信息
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		
		//创建返回结果对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal((int) pageInfo.getTotal());
		result.setRows(list);
				
		return result;
	}

	//编辑内容
	public TaotaoResult editContent(TbContent content) {
		content.setUpdated(new Date());
		mapper.updateByPrimaryKeySelective(content);
		return TaotaoResult.ok();
	}

	//删除内容
	public TaotaoResult deleteContent(String ids) {
		String[] strArr = ids.split(","); //然后使用split方法将字符串拆解到字符串数组中
		
		Long[] longArr = new Long[strArr.length]; //定义一个长度与上述的字符串数组长度相通的整型数组
		
		for(int a=0;a<strArr.length;a++){
			longArr[a] = Long.valueOf(strArr[a]); //然后遍历字符串数组，使用包装类Integer的valueOf方法将字符串转为整型
		}
		
		for(int b=0;b<longArr.length;b++){
			mapper.deleteByPrimaryKey(longArr[b]);
		}
		return TaotaoResult.ok();
	}

}
