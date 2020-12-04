package com.twi.wechat.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.freepo.domain.FreePo;
import com.twi.freepo.domain.FreePoItem;
import com.twi.user.domain.SysOrgInfo;
import com.twi.wechat.domain.FreeDayTradingSum;
import com.twi.wechat.domain.FreeWxPay;
import com.twi.wechat.domain.FreeWxPayInfo;

public interface WxPayServices {

	/**
	 * 定时下载微信对账单数据
	 */
	void quartzDownloadBill();
	
	/**
	 * 下载微信对账单数据
	 */
	void downloadBill(SysOrgInfo org, Date date);
	
	/**
	 * 每日对账列表
	 * @param page
	 * @param orgId
	 * @param freePoNo
	 * @param studentName
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Page<FreeWxPayInfo> getEveryDayBillPage(Page<FreeWxPayInfo> page, String orgId, String freePoNo, String studentName, String startDate, String endDate, String studentCode, String payTypeId, boolean payTypeFlag);
	
	/**
	 * 每日交易汇总
	 * @param page
	 * @param orgId
	 * @param payNumber
	 * @param sumMoney
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Page<FreeDayTradingSum> getEveryDaySummaryPage(Page<FreeDayTradingSum> page, String orgId, String numFlag, String payWxNumber, String amountFlag, String sumWxMoney, String startDate, String endDate);
	
	/**
	 * 每日交易汇总
	 * @param orgId
	 * @param numFlag
	 * @param payWxNumber
	 * @param amountFlag
	 * @param sumWxMoney
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> getEveryDaySummaryList(String orgId, String numFlag, String payWxNumber, String amountFlag, String sumWxMoney, String startDate, String endDate);
	
	/**
	 * 订单
	 * @param freePo
	 * @param detailedId
	 * @param amount
	 */
	void saveOrderInfo(FreePo freePo, String detailedId, String amount, FreeCartDetailed freeCartDetailed);
	
	boolean addEntity(FreePo freePo);
	
	boolean updateEntity(FreePo freePo);
	
	boolean addEntity(FreeWxPay freeWxPay);
	
	boolean updateEntity(FreeWxPay freeWxPay);
	
	FreeWxPay getEntityById(String id);
	
	boolean addEntity(FreePoItem item);
	
	FreeWxPay getFreeWxPayByPoId(String freePoId);
	
	FreeWxPay getFreeWxPay(String freePoId,String orgId);
	/**
	 * 更新金额和状态
	 * @param freePo
	 * @return
	 */
	boolean updateMoneyAndState(FreePo freePo);
	
	List<FreePoItem> getFreePoItemByPoId(String freePoId);
	
	/**
	 * 获取微信订单信息支付状态
	 * @param orderId
	 */
    String getWxOrderInfo(String orderId, String orgId);
    
    /**
     * 定时更新缴费明细状态和锁定价格
     */
    void quartzUpdateOrderState();
    
    /**
     * 定时统计昨日交易金额笔数
     */
    void statisticsDealData();
    
    void checkPayDate();
    
    /**
     * 手动生成交易汇总专用
     * @param now
     * @return
     */
    List<Map<String, Object>> getSummaryList(Date now, String orgId);
    
    boolean addWxTradeSummary(FreeDayTradingSum freeDayTradingSum);
    
    boolean updWxTradeSummary(FreeDayTradingSum freeDayTradingSum);
    
    FreeDayTradingSum getTradingSumByReDate(Date reDate, String orgId);
    
    void addOrUpdTradingSum(Date reDate, String orgId, Map<String, Object> map);
    
    void saveFreePoInfo(FreePo freePo, String[] detailedIds, String amount, Map<String, Object> map);
    
    FreePoItem newFreePoItem(FreePo freePo, String detailedId, Map<String, Object> map);
    
    // 取消支付
    void updateOrderState(List<FreePo> freePos);
}
