package com.twi.wx.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.base.util.PropertiesMsg;
import com.twi.base.util.StringUtils;
import com.twi.wechat.domain.UserOrder;
import com.twi.wx.services.WxOrderServices;

@Controller
@RequestMapping("/wx/wxOrder")
public class WxOrderController extends BaseController {

	@Resource(name = "wxOrderServices")
	private WxOrderServices wxOrderServices;

	// 微信用户订单
	@ResponseBody
	@RequestMapping(value = "getWxUserOrder", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getWxUserOrder(HttpServletRequest request,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, 
			HttpServletResponse response) {
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		String openId = (String) request.getSession().getAttribute("wxOpenId");
		if (openId == null) {
			return createErrorResult("未绑定微信号！");
		}
		String orgId = (String) request.getSession().getAttribute("orgId");
		if (orgId == null) { //无orgId
			return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
		}
		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<UserOrder> page = new Page<UserOrder>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		//String openId="oFlAN1PuP1Xm8cg5Xus86kwqtse4";
		page = wxOrderServices.getUserOrderPage(page, openId, orgId);
		
		//List<Map<String, Object>> orderList = wxOrderServices.getOrderList(openId);
		List<UserOrder> orderList = page.getResult();
		for (UserOrder userOrder : orderList) {
			String poId = userOrder.getPoId();
			List<Map<String, Object>> itemList = wxOrderServices.getOrderItemList(poId);
			userOrder.setPoItemList(itemList);
		}
		
		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("count", page.getTotalCount());
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());
		return createSuccessResult(contentMap);
	}

	// 根据缴费明细ID获取订单项列表
	@ResponseBody
	@RequestMapping(value = "getPoItemList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getPoItemList(@RequestParam(value = "detailedId") String detailedId,
			HttpServletRequest request, HttpServletResponse response) {
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}

		String openId = (String) request.getSession().getAttribute("wxOpenId");
		if (openId == null) {
			return createErrorResult("未绑定微信号！");
		}
		if (StringUtils.isEmpty(detailedId)) {
			return createErrorResult("参数错误！");
		}
		List<Map<String, Object>> itemList = wxOrderServices.getPoItemList(openId, detailedId);

		for (Map<String, Object> map : itemList) {
			if (StringUtils.equals(map.get("count").toString(), "1")) {
				map.put("isMergePay", 0);
			} else {
				map.put("isMergePay", 1);
			}
		}

		return createSuccessResult(itemList);
	}

	// 微信用户查询绑定学生明细
	@ResponseBody
	@RequestMapping(value = "getStuDetailedList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getStuDetailedList(@RequestParam(value = "bindId") String bindId,
			HttpServletRequest request, HttpServletResponse response) {
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}

		String openId = (String) request.getSession().getAttribute("wxOpenId");
		if (openId == null) {
			return createErrorResult("未绑定微信号！");
		}
		String orgId = (String) request.getSession().getAttribute("orgId");
		if (orgId == null) { //无orgId
			return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
		}
		List<Map<String, Object>> detailedList = wxOrderServices.getStuDetailedList(openId, bindId,orgId);

		return createSuccessResult(detailedList);
	}

	// 查询绑定学生所有订单
	@ResponseBody
	@RequestMapping(value = "getStuOrderList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getStuAllDetailedList(@RequestParam(value = "bindId") String bindId,
			HttpServletRequest request, HttpServletResponse response) {
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
//		String openId = (String) request.getSession().getAttribute("wxOpenId");
//		if (openId == null) {
//			return createErrorResult("未绑定微信号！");
//		}
		String orgId = (String) request.getSession().getAttribute("orgId");
		if (orgId == null) { //无orgId
			return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
		}
		List<Map<String, Object>> orderList = wxOrderServices.getStuOrderList(bindId,orgId);
		for (Map<String, Object> map : orderList) {
			String poId = map.get("poId").toString();
			List<Map<String, Object>> itemList = wxOrderServices.getOrderItemList(poId);
			map.put("poItemList", itemList);
		}
		return createSuccessResult(orderList);
	}

}
