package com.twi.paymenu.services;

import java.util.List;

import com.twi.base.domain.Page;
import com.twi.paymenu.domain.SysPayMenu;

public interface PayMenuServices {

	/**
	 * 获取缴费菜单列表
	 * @param page
	 * @param name
	 * @return
	 */
	Page<SysPayMenu> getPayMenuPage(Page<SysPayMenu> page, String orgId, String name);
	
	/**
	 * 缴费菜单保存
	 * @param payMenu
	 * @return
	 */
	boolean savePayMenu(SysPayMenu payMenu);
	
	/**
	 * 缴费菜单详细
	 * @param id
	 * @return
	 */
	SysPayMenu getPayMenuById(String id);
	
	/**
	 * 缴费菜单列表
	 * @param orgId
	 * @return
	 */
	List<SysPayMenu> getPayMenuList(String orgId);
}
