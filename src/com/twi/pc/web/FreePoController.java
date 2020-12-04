package com.twi.pc.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.base.util.ExcelUtils;
import com.twi.freepo.services.FreePoServices;
import com.twi.security.domain.SSCUserDetails;
import com.twi.wechat.domain.FreeDayTradingSum;
import com.twi.wechat.domain.FreeWxPay;
import com.twi.wechat.services.WxPayServices;

/**
 * 订单
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/admin_back/free/po")
public class FreePoController extends BaseController{

	@Autowired
	private FreePoServices freePoServicesImp;
	
	@Autowired
	private WxPayServices wxPayServicesImp;
	
	/**
	 * 交易明细
	 * @param request
	 * @param studentCode 学号
	 * @param studentName 学生姓名（付款方）
	 * @param startDate
	 * @param endDate
	 * @param majorName 专业
	 * @param className 班级
	 * @param orderType 排序方式（date:日期；studentCode:学号；studentName:姓名）
	 * @return
	 */
	@RequestMapping(value="/list")
	public Map<String, Object> getDealList(HttpServletRequest request,
			@RequestParam(value="studentCode", defaultValue = "") String studentCode,
			@RequestParam(value="studentName", defaultValue = "") String studentName,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate,
			@RequestParam(value="majorName", defaultValue = "") String majorName,
			@RequestParam(value="className", defaultValue = "") String className,
			@RequestParam(value="payTypeId", defaultValue = "") String payTypeId,
			@RequestParam(value="orderType", defaultValue = "") String orderType){
		logger.info("------start------getDealList");
		Map<String, Object> result = new HashMap<String, Object>();
		
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		
		// 分页参数：
		int pageNo = getInt(request, "pageNo", 1); // 当前面
		int pageSize = getInt(request, "pageSize", 10); // 每页显示条数数
		boolean autoCount = getBoolean(request, "autoCount", true); // 是否显示总条数
		
		Page<FreeWxPay> page = new Page<FreeWxPay>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		
		try {
			page = freePoServicesImp.getDealPage(page, orgId, studentCode, studentName, startDate, endDate, majorName, className, orderType, payTypeId);
		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResult("数据异常");
		}
		
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		logger.info("------end------getDealList");
		return createSuccessResult(result);
	}
	
	/**
	 * 交易明细导出
	 * @param request
	 * @param response
	 * @param studentCode
	 * @param studentName
	 * @param startDate
	 * @param endDate
	 * @param majorName
	 * @param className
	 * @param orderType
	 */
	@RequestMapping(value="/deals/exportExcel")
	public void dealsExportExcel(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="studentCode", defaultValue = "") String studentCode,
			@RequestParam(value="studentName", defaultValue = "") String studentName,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate,
			@RequestParam(value="majorName", defaultValue = "") String majorName,
			@RequestParam(value="className", defaultValue = "") String className,
			@RequestParam(value="orderType", defaultValue = "") String orderType){
		logger.info("------start------dealsExportExcel");
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		
		List<Map<String, Object>> rowlist = freePoServicesImp.getDealList(orgId, studentCode, studentName, startDate, endDate, majorName, className, orderType);
		//String[] titles={"微信订单号","商户订单号","学生编号","学生姓名","专业","班级","缴费类型","金额","用户标识","货币种类","付款银行","交易类型","交易时间"};
		//String[] columnKey={"wxOrderNo","freePoNo","studentCode","studentName","majorName","className","payTypeName","payMoney","openId","moneyType","payBank","payType","payTime"};
		
		String[] titles={"微信订单号","商户订单号","学生编号","学生姓名","专业","班级","缴费类型","金额","支付方式","用户标识","交易时间"};
		String[] columnKey={"wxOrderNo","freePoNo","studentCode","studentName","majorName","className","payTypeName","payMoney","payModeStr","openId","payTime"};
		
		try {
			
			request.setCharacterEncoding("utf-8");
			String fileName ="deal_detail_"+CalendarUtils.format(new Date(), "yyyyMMddHHmmss")+".xls";
		 	fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
		 	HSSFWorkbook  workbook=ExcelUtils.eqEcel(rowlist, titles, "交易明细表", columnKey);
            response.reset();  
            response.setContentType("contentType=application/vnd.ms-excel");  
            response.setHeader("Content-disposition","attachment;filename="+fileName);  
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Connection","close");
            workbook.write(response.getOutputStream());  
            response.flushBuffer();  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------end--------dealsExportExcel");
	}
	
	
	/**
	 * 每日交易汇总
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="/summary/list")
	public Map<String, Object> getDealSummaryList(HttpServletRequest request,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate){
		logger.info("------start------getDealSummaryList");
		Map<String, Object> result = new HashMap<String, Object>();
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		// 分页参数：
		int pageNo = getInt(request, "pageNo", 1); // 当前面
		int pageSize = getInt(request, "pageSize", 10); // 每页显示条数数
		boolean autoCount = getBoolean(request, "autoCount", true); // 是否显示总条数
		
		Page<FreeDayTradingSum> page = new Page<FreeDayTradingSum>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		
		try {
			page = freePoServicesImp.getDealSummaryPage(page, orgId, startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResult("数据异常");
		}
		
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		
		logger.info("------end------getDealSummaryList");
		return createSuccessResult(result);
	}
	
	/**
	 * 交易汇总导出
	 * @param request
	 * @param response
	 * @param startDate
	 * @param endDate
	 */
	@RequestMapping(value="/summary/exportExcel")
	public void summaryExportExcel(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate){
		logger.info("------start------summaryExportExcel");
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		
		List<Map<String, Object>> rowlist = freePoServicesImp.getDealSummaryList(orgId, startDate, endDate);
		String[] titles={"汇总日期"," 总交易笔数","总交易额","微信交易笔数","微信总交易额","微信退款金额","微信手续费","微信是否已对帐","微信错误信息"};
		String[] columnKey={"re_date","pay_number","sum_money","pay_wx_number","sum_wx_money","refund_wx_money","Service_wx_money","wx_check_state","error_wx_msg"};
		try {
			
			request.setCharacterEncoding("utf-8");
			String fileName ="summary_"+CalendarUtils.format(new Date(), "yyyyMMddHHmmss")+".xls";
		 	fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
		 	HSSFWorkbook  workbook=ExcelUtils.eqEcel(rowlist, titles, "交易每日汇总", columnKey);
            response.reset();  
            response.setContentType("contentType=application/vnd.ms-excel");  
            response.setHeader("Content-disposition","attachment;filename="+fileName);  
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Connection","close");
            workbook.write(response.getOutputStream());  
            response.flushBuffer();  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------end--------summaryExportExcel");
	}
	
	/**
	 * 交易明细
	 * @param request
	 * @param studentCode 学号
	 * @param studentName 学生姓名（付款方）
	 * @param majorName 专业
	 * @param className 班级
	 * @param payTypeId 缴费项目id
	 * @return
	 */
	@RequestMapping(value="/today/list")
	public Map<String, Object> getTodayDealList(HttpServletRequest request,
			@RequestParam(value="studentCode", defaultValue = "") String studentCode,
			@RequestParam(value="studentName", defaultValue = "") String studentName,
			@RequestParam(value="majorName", defaultValue = "") String majorName,
			@RequestParam(value="className", defaultValue = "") String className,
			@RequestParam(value="payTypeId", defaultValue = "") String payTypeId){
		logger.info("------start------getTodayDealList");
		Map<String, Object> result = new HashMap<String, Object>();
		
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		
		// 分页参数：
		int pageNo = getInt(request, "pageNo", 1); // 当前面
		int pageSize = getInt(request, "pageSize", 10); // 每页显示条数数
		boolean autoCount = getBoolean(request, "autoCount", true); // 是否显示总条数
		
		Page<FreeWxPay> page = new Page<FreeWxPay>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		String today = CalendarUtils.format(new Date(), "yyyy-MM-dd");
		try {
			page = freePoServicesImp.getDealPage(page, orgId, studentCode, studentName, today, today, majorName, className, "", payTypeId);
		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResult("数据异常");
		}
		List<Map<String, Object>> listMap = wxPayServicesImp.getSummaryList(new Date(), orgId);
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		if (listMap != null && listMap.size() > 0){
			result.put("payNumber", listMap.get(0).get("payNumber"));
			result.put("sumMoney", listMap.get(0).get("sumMoney"));
		}else{
			result.put("payNumber", 0);
			result.put("sumMoney", 0);
		}
		logger.info("------end------getTodayDealList");
		return createSuccessResult(result);
	}
}
