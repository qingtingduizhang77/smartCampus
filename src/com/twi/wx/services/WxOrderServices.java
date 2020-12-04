package com.twi.wx.services;

import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.wechat.domain.UserOrder;

public interface WxOrderServices {

	// 根据openid 获取订单列表
	List<Map<String, Object>> getOrderList(String openId);

	// 根据openid 获取订单分页列表
	Page<UserOrder> getUserOrderPage(Page<UserOrder> page, String openId);
	// 根据openid,orgid 获取订单分页列表
	Page<UserOrder> getUserOrderPage(Page<UserOrder> page, String openId, String orgId);

	// 根据订单ID获取订单项列表
	List<Map<String, Object>> getOrderItemList(String poId);

	// 根据缴费明细ID 获取订单项列表
	List<Map<String, Object>> getPoItemList(String openId, String detailedId);

	// 根据微信绑定ID+openId 获取学生明细列表列表
	List<Map<String, Object>> getStuDetailedList(String openId, String bindId);
	// 根据微信绑定ID+openId,orgid 获取学生明细列表列表
	List<Map<String, Object>> getStuDetailedList(String openId, String bindId, String orgId);

	// 根据微信绑定ID 获取学生订单列表
	List<Map<String, Object>> getStuOrderList(String bindId);
	// 根据微信绑定ID,orgid 获取学生订单列表
	List<Map<String, Object>> getStuOrderList(String bindId, String orgId);

}
