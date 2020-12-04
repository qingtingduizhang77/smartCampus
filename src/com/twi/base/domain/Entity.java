package com.twi.base.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

/**
 * 实体对象基类
 */
public class Entity {
	// 扩展属性Map，不受hibernate管理
	@Transient
	protected Map<String, Object> extendAttrs = new HashMap<String, Object>();

	/**
	 * 获取扩展属性Map。当需要返回一些附加对象时，可以使用此属性
	 * 
	 * @return
	 */
	public Map<String, Object> getExtendAttrs() {
		return extendAttrs;
	}

	/**
	 * 获取扩展属性
	 * 
	 * @param name
	 *            属性名
	 * @return
	 */
	public Object getExtendAttr(String name) {
		return extendAttrs.get(name);
	}

	/**
	 * 设置扩展属性
	 * 
	 * @param name
	 *            属性名
	 * @param value
	 *            属性值
	 */
	public void setExtendAttr(String name, Object value) {
		extendAttrs.put(name, value);
	}

}
