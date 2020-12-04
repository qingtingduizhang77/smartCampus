package com.twi.freepo.services;

import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.freepo.domain.FreePo;
import com.twi.wechat.domain.FreeDayTradingSum;
import com.twi.wechat.domain.FreeWxPay;

public interface FreePoServices {

	/**
	 * 交易明细
	 * @param page
	 * @param orgId
	 * @param studentCode
	 * @param studentName
	 * @param startDate
	 * @param endDate
	 * @param majorName
	 * @param className
	 * @param orderType
	 * @return
	 */
	Page<FreeWxPay> getDealPage(Page<FreeWxPay> page, String orgId, String studentCode, String studentName, String startDate, String endDate,  String majorName, String className, String orderType, String payTypeId);
	
	/**
	 * 交易明细
	 * @param orgId
	 * @param studentCode
	 * @param studentName
	 * @param startDate
	 * @param endDate
	 * @param majorName
	 * @param className
	 * @param orderType
	 * @return
	 */
	List<Map<String, Object>> getDealList(String orgId, String studentCode, String studentName, String startDate, String endDate,  String majorName, String className, String orderType);
	
	/**
	 * 每日交易汇总
	 * @param page
	 * @param orgId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Page<FreeDayTradingSum> getDealSummaryPage(Page<FreeDayTradingSum> page, String orgId, String startDate, String endDate);
	
	/**
	 * 每日交易汇总
	 * @param page
	 * @param orgId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> getDealSummaryList(String orgId, String startDate, String endDate);
	
	/**
	 * 每日交易汇总
	 * @param orgId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	FreeDayTradingSum getDealSummary(String orgId, String startDate, String endDate);
	
	/**
	 * 订单信息
	 * @param id
	 * @return
	 */
	FreePo getEntityById(String id);
	
	/**
	 *  订单信息
	 * @param id
	 * @return
	 */
	FreePo getFreePoById(String id, Integer state);
}
