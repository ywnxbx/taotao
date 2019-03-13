package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.manager.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	//注入代理对象
	@Autowired
	private TbItemMapper tbItemMapper;
	
	@Autowired
	private TbItemDescMapper descmapper;
	
	@Autowired
	private JedisClient client;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Resource(name="topicDestination")
	private Destination destination;
	
	@Value("${ITEM_INFO_KEY}")
	private String ITEM_INFO_KEY;

	@Value("${ITEM_INFO_KEY_EXPIRE}")
	private Integer ITEM_INFO_KEY_EXPIRE;
	
	
	
	/**
	 * 查询商品信息
	 */
	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		
		//1.设置分页数据
		PageHelper.startPage(page, rows);
		
		//2.执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = tbItemMapper.selectByExample(example);
		
		//取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		
		//创建返回结果对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal((int) pageInfo.getTotal());
		result.setRows(list);
		
		return result;
	}
	
	
	/**
	 * 保存商品，使用消息队列
	 */
	@Override
	public TaotaoResult saveItem(TbItem item, String desc) {
		//生成商品的id
		final long itemId = IDUtils.genItemId();
		//1.补全item 的其他属性
		item.setId(itemId);
		item.setCreated(new Date());
		//1-正常，2-下架，3-删除',
		item.setStatus((byte) 1);
		item.setUpdated(item.getCreated());
		//2.插入到item表 商品的基本信息表
		tbItemMapper.insertSelective(item);
		//3.补全商品描述中的属性
		TbItemDesc desc2 = new TbItemDesc();
		desc2.setItemDesc(desc);
		desc2.setItemId(itemId);
		desc2.setCreated(item.getCreated());
		desc2.setUpdated(item.getCreated());
		//4.插入商品描述数据
			//注入tbitemdesc的mapper
		descmapper.insertSelective(desc2);
		
		
		//添加发送消息的业务逻辑
		jmsTemplate.send(destination, new MessageCreator(){
			@Override
			public Message createMessage(Session session) throws JMSException {
				System.out.println("保存订单，发送商品id"+itemId);
				//发送的消息
				return session.createTextMessage(itemId+"");
			}
		});
		
	
		//5.返回taotaoresult
		return TaotaoResult.ok();
	}

	
	/**
	 * 根据id查询商品信息
	 */
	@Override
	public TbItem getItemById(Long itemId) {
		// 添加缓存

		// 1.从缓存中获取数据，如果有直接返回
		try {
			String jsonstr = client.get(ITEM_INFO_KEY + ":" + itemId + ":BASE");

			if (StringUtils.isNotBlank(jsonstr)) {
				// 重新设置商品的有效期
				System.out.println("有缓存");
				client.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItem.class);

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 2如果没有数据

		// 注入mapper
		// 调用方法
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		// 返回tbitem

		// 3.添加缓存到redis数据库中
		// 注入jedisclient
		// ITEM_INFO:123456:BASE
		// ITEM_INFO:123456:DESC
		try {
			System.out.println("没有缓存，加入缓存");
			client.set(ITEM_INFO_KEY + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItem));
			// 设置缓存的有效期
			client.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItem;
	}

	
	/**
	 * 根据id查询商品描述
	 */
	@Override
	public TbItemDesc getItemDescById(Long itemId) {
		// 添加缓存

		// 1.从缓存中获取数据，如果有直接返回
		try {
			String jsonstr = client.get(ITEM_INFO_KEY + ":" + itemId + ":DESC");

			if (StringUtils.isNotBlank(jsonstr)) {
				// 重新设置商品的有效期
				System.out.println("有缓存");
				client.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItemDesc.class);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//如果没有查到数据 从数据库中查询
		TbItemDesc itemdesc = descmapper.selectByPrimaryKey(itemId);
		//添加缓存
		// 3.添加缓存到redis数据库中
		// 注入jedisclient
		// ITEM_INFO:123456:BASE
		// ITEM_INFO:123456:DESC
		try {
			System.out.println("没有缓存，加入缓存");
			client.set(ITEM_INFO_KEY + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemdesc));
			// 设置缓存的有效期
			client.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemdesc;
	}
}
