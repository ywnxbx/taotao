package com.taotao.common.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 需要web与service共用的pojo，放在conmmon中
 * 展示数据的pojo,
 * 实现序列化接口！
 *
 */
public class EasyUIDataGridResult implements Serializable {
	
	private Integer total;
	
	private List rows;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}
}
