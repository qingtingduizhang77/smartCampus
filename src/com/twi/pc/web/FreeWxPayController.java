package com.twi.pc.web;

import java.util.Calendar;
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
import com.twi.security.domain.SSCUserDetails;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.services.SysOrgServices;
import com.twi.wechat.domain.FreeDayTradingSum;
import com.twi.wechat.domain.FreeWxPayInfo;
import com.twi.wechat.services.WxPayServices;

/**
 * 微信缴费单
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/admin_back/free/pay")
public class FreeWxPayController extends BaseController{

	@Autowired
	private WxPayServices wxPayServicesImp;
	
	@Autowired
	private SysOrgServices sysOrgServicesImp;
	
	/**
	 * 微信--每日对账列表
	 * @param freePoNo 商户订单号
	 * @param studentName 学生姓名（付款方）
	 * @param startDate
	 * @param endDate
	 * @param studentCode
	 * @param payTypeId
	 * @return
	 */
	@RequestMapping(value="/list")
	public Map<String, Object> everyDayBillList(HttpServletRequest request,
			@RequestParam(value="freePoNo", defaultValue = "") String freePoNo,
			@RequestParam(value="studentName", defaultValue = "") String studentName,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate,
			@RequestParam(value="studentCode", defaultValue = "") String studentCode,
			@RequestParam(value="payTypeId", defaultValue = "") String payTypeId){
		logger.info("------start------everyDayBillList");
		Map<String, Object> result = new HashMap<String, Object>();
		boolean payTypeFlag = getBoolean(request, "payTypeFlag", false); // 是否是通过缴费类型来group by
		
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		
		// 分页参数：
		int pageNo = getInt(request, "pageNo", 1); // 当前面
		int pageSize = getInt(request, "pageSize", 10); // 每页显示条数数
		boolean autoCount = getBoolean(request, "autoCount", true); // 是否显示总条数
		
		Page<FreeWxPayInfo> page = new Page<FreeWxPayInfo>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		
		try {
			page = wxPayServicesImp.getEveryDayBillPage(page, orgId, freePoNo, studentName, startDate, endDate, studentCode, payTypeId, payTypeFlag);
		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResult("数据异常");
		}
		
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		logger.info("------end------everyDayBillList");
		return createSuccessResult(result);
	}
	
	/**
	 * 微信--每日交易汇总
	 * @param payNumber 交易笔数
	 * @param sumMoney 交易金额
	 * @param numFlag 交易笔数判断标识
	 * @param amountFlag 交易金额判断标识
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="/summary/list")
	public Map<String, Object> everyDaySummaryList(HttpServletRequest request,
			@RequestParam(value="payWxNumber", defaultValue = "-1") String payWxNumber,
			@RequestParam(value="sumWxMoney", defaultValue = "-1") String sumWxMoney,
			@RequestParam(value="numFlag", defaultValue = ">") String numFlag,
			@RequestParam(value="amountFlag", defaultValue = ">") String amountFlag,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate){
		logger.info("------start------everyDaySummaryList");
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
			page = wxPayServicesImp.getEveryDaySummaryPage(page, orgId, numFlag, payWxNumber, amountFlag, sumWxMoney, startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResult("数据异常");
		}
		
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		
		logger.info("------end------everyDaySummaryList");
		return createSuccessResult(result);
	}
	
	/**
	 * 微信对账数据导出
	 * @param request
	 * @param response
	 * @param payWxNumber
	 * @param sumWxMoney
	 * @param numFlag
	 * @param amountFlag
	 * @param startDate
	 * @param endDate
	 */
	@RequestMapping(value="/exportExcel")
	public void exportExcel(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="payWxNumber", defaultValue = "-1") String payWxNumber,
			@RequestParam(value="sumWxMoney", defaultValue = "-1") String sumWxMoney,
			@RequestParam(value="numFlag", defaultValue = ">") String numFlag,
			@RequestParam(value="amountFlag", defaultValue = ">") String amountFlag,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate){
		logger.info("------start------exportExcel");
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		
		List<Map<String, Object>> rowlist = wxPayServicesImp.getEveryDaySummaryList(orgId, numFlag, payWxNumber, amountFlag, sumWxMoney, startDate, endDate);
		String[] titles={"对账日期","笔数","成交金额","退款金额","手续费总额","是否对账"};
		String[] columnKey={"re_date","pay_wx_number","sum_wx_money","refund_wx_money","Service_wx_money","check_wx_state"};
		try {
			
			request.setCharacterEncoding("utf-8");
			String fileName ="wx_"+CalendarUtils.format(new Date(), "yyyyMMddHHmmss")+".xls";
		 	fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
		 	HSSFWorkbook  workbook=ExcelUtils.eqEcel(rowlist, titles, "微信对账", columnKey);
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
		logger.info("-------end--------exportExcel");
	}
	
	/**
	 * 下载对账单
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/download/bill")
	public Map<String, Object> downloadBill(
			@RequestParam(value="startDate", defaultValue = "", required=true) String startDate,
			@RequestParam(value="endDate", defaultValue = "", required=true) String endDate){
		try {
			SSCUserDetails user = WebHelper.getUser();
			String orgId = "";
			if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
				orgId = user.getOrgId();
			}
			SysOrgInfo info = sysOrgServicesImp.getOrgInfoById(orgId);
			if (info == null) {
				return createErrorResult("机构不存在");
			}
			Date start = CalendarUtils.dateAddOrSub(CalendarUtils.simpleparse(startDate), Calendar.DAY_OF_MONTH, 1);
			Date end = CalendarUtils.dateAddOrSub(CalendarUtils.simpleparse(endDate), Calendar.DAY_OF_MONTH, 1);
			List<String> dateList = CalendarUtils.getDayList(start, end, "yyyy-MM-dd");
			if (dateList != null && dateList.size() > 0) {
				for (String date : dateList) {
					
					Date reDate = CalendarUtils.dateAddOrSub(CalendarUtils.simpleparse(date), Calendar.DAY_OF_MONTH, -1);
					List<Map<String, Object>> listMap = wxPayServicesImp.getSummaryList(reDate,orgId);
					if (listMap != null && listMap.size() > 0) {
						for (Map<String, Object> map : listMap) {
							
							wxPayServicesImp.addOrUpdTradingSum(reDate, orgId, map);
						}
					}else{
						wxPayServicesImp.addOrUpdTradingSum(reDate,orgId, new HashMap<String, Object>());
					}
					
					
					wxPayServicesImp.downloadBill(info, CalendarUtils.simpleparse(date));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResult("fail");
		}
		return createSuccessResult(null, "success");
	}
}
