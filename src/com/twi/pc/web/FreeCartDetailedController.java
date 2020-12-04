package com.twi.pc.web;

import java.io.BufferedOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.base.util.ExcelUtils;
import com.twi.base.util.StringUtils;
import com.twi.freecartdetailed.domain.DetailedItemReport;
import com.twi.freecartdetailed.domain.DetailedSummary;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.freecartdetailed.services.FreeCartDetailedServices;
import com.twi.security.domain.SSCUserDetails;

//账套费用明细
@Controller
@RequestMapping("/admin_back/freeCartDetailed")
public class FreeCartDetailedController extends BaseController {

	@Resource(name = "freeCartDetailedServices")
	private FreeCartDetailedServices freeCartDetailedServices;

	private String getOrgId() {
		SSCUserDetails user = WebHelper.getUser();
		if (user == null || !StringUtils.isNotEmpty(user.getOrgId())) {
			return null;
		}
		return user.getOrgId();
	}

	@ResponseBody
	@RequestMapping(value = "getPageList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getPageList(HttpServletRequest request,
			@RequestParam(value = "freeCartId", required = false) String freeCartId,
			@RequestParam(value = "studentCode", required = false) String studentCode,
			@RequestParam(value = "studentName", required = false) String studentName,
			@RequestParam(value = "clazzName", required = false) String clazzName,
			@RequestParam(value = "majorName", required = false) String majorName,
			@RequestParam(value = "payTypeId", required = false) String payTypeId,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<FreeCartDetailed> page = new Page<FreeCartDetailed>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(freeCartId)) {
			pMap.put("freeCartId", freeCartId);
		}
		if (StringUtils.strIsNotNull(studentCode)) {
			pMap.put("studentCode", studentCode);
		}
		if (StringUtils.strIsNotNull(studentName)) {
			pMap.put("studentName", studentName);
		}
		if (StringUtils.strIsNotNull(clazzName)) {
			pMap.put("clazzName", clazzName);
		}
		if (StringUtils.strIsNotNull(majorName)) {
			pMap.put("majorName", majorName);
		}
		if (StringUtils.strIsNotNull(payTypeId)) {
			pMap.put("payTypeId", payTypeId);
		}
		pMap.put("orgId", getOrgId());
		page = freeCartDetailedServices.getPageList(page, pMap);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("count", page.getTotalCount());
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());

		return createSuccessResult(contentMap);
	}

	@ResponseBody
	@RequestMapping(value = "batchDelete", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> batchDelete(HttpServletRequest request, @RequestParam String ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] idArr = ids.split(",");
		map.put("count", freeCartDetailedServices.canDeleteCount(idArr));
		if (!freeCartDetailedServices.batchDelete(idArr)) {
			return createErrorResult("出错了，请检查参数是否正确！");
		}
		return createSuccessResult(map);
	}

	//导出缴费明细
	@RequestMapping(value = "/exportExcel")
	public void exportExcel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "freeCartId", required = false) String freeCartId,
			@RequestParam(value = "studentCode", required = false) String studentCode,
			@RequestParam(value = "studentName", required = false) String studentName,
			@RequestParam(value = "clazzName", required = false) String clazzName,
			@RequestParam(value = "majorName", required = false) String majorName,
			@RequestParam(value = "payTypeId", required = false) String payTypeId) {

		logger.info("-------start--------exportExcel");
		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(freeCartId)) {
			pMap.put("freeCartId", freeCartId);
		}
		if (StringUtils.strIsNotNull(studentCode)) {
			pMap.put("studentCode", studentCode);
		}
		if (StringUtils.strIsNotNull(studentName)) {
			pMap.put("studentName", studentName);
		}
		if (StringUtils.strIsNotNull(clazzName)) {
			pMap.put("clazzName", clazzName);
		}
		if (StringUtils.strIsNotNull(majorName)) {
			pMap.put("majorName", majorName);
		}
		if (StringUtils.strIsNotNull(payTypeId)) {
			pMap.put("payTypeId", payTypeId);
		}
		pMap.put("orgId", getOrgId());

		List<Map<String, Object>> rowlist = freeCartDetailedServices.getConditionList(pMap);
		String[] titles = { "学号", "姓名", "专业", "班级", "缴费账套ID","缴费账套名称", "缴费类型ID", "缴费类型名称", "应缴费用", "已缴费用" };
		String[] columnKey = { "Student_code", "Student_name", "major_name", "clazz_name", "free_Cart_id","free_cart_name","pay_type_id","pay_type_name", "money_", "pay_money" };
		String sheetName = "费用明细";
		
		try {
			HSSFWorkbook workbook = ExcelUtils.eqEcel(rowlist, titles, sheetName, columnKey);
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			// 设置输出的格式
			response.setContentType("application/x-excel");
			String fileName = "FreeCartDetailed_" + CalendarUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
			fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------end--------exportExcel");
	}

	//缴费汇总报表
	@ResponseBody
	@RequestMapping(value = "getSummaryList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getSummaryList(HttpServletRequest request,
			@RequestParam(value = "freeCartId", required = false) String freeCartId,
			@RequestParam(value = "clazzName", required = false) String clazzName,
			@RequestParam(value = "majorName", required = false) String majorName,
			@RequestParam(value = "payTypeId", required = false) String payTypeId,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<DetailedSummary> page = new Page<DetailedSummary>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(freeCartId)) {
			pMap.put("freeCartId", freeCartId);
		}
		if (StringUtils.strIsNotNull(clazzName)) {
			pMap.put("clazzName", clazzName);
		}
		if (StringUtils.strIsNotNull(majorName)) {
			pMap.put("majorName", majorName);
		}
		if (StringUtils.strIsNotNull(payTypeId)) {
			pMap.put("payTypeId", payTypeId);
		}
		pMap.put("orgId", getOrgId());
		page = freeCartDetailedServices.getSummaryList(page, pMap);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("count", page.getTotalCount());
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());

		return createSuccessResult(contentMap);
	}
	
	//缴费报表
	@ResponseBody
	@RequestMapping(value = "getItemReportList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getItemReportList(HttpServletRequest request,
			@RequestParam(value = "freeCartId", required = false) String freeCartId,
			@RequestParam(value = "clazzName", required = false) String clazzName,
			@RequestParam(value = "majorName", required = false) String majorName,
			@RequestParam(value = "payTypeId", required = false) String payTypeId,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<DetailedItemReport> page = new Page<DetailedItemReport>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(freeCartId)) {
			pMap.put("freeCartId", freeCartId);
		}
		if (StringUtils.strIsNotNull(clazzName)) {
			pMap.put("clazzName", clazzName);
		}
		if (StringUtils.strIsNotNull(majorName)) {
			pMap.put("majorName", majorName);
		}
		if (StringUtils.strIsNotNull(payTypeId)) {
			pMap.put("payTypeId", payTypeId);
		}
		pMap.put("orgId", getOrgId());
		page = freeCartDetailedServices.getItemReportList(page, pMap);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("count", page.getTotalCount());
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());

		return createSuccessResult(contentMap);
	}
	
	//导出缴费报表
	@RequestMapping(value = "/exportPayReport")
	public void exportPayReport(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "freeCartId", required = false) String freeCartId,
			@RequestParam(value = "clazzName", required = false) String clazzName,
			@RequestParam(value = "majorName", required = false) String majorName,
			@RequestParam(value = "payTypeId", required = false) String payTypeId) {

		logger.info("-------start--------exportExcel");
		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(freeCartId)) {
			pMap.put("freeCartId", freeCartId);
		}
		if (StringUtils.strIsNotNull(clazzName)) {
			pMap.put("clazzName", clazzName);
		}
		if (StringUtils.strIsNotNull(majorName)) {
			pMap.put("majorName", majorName);
		}
		if (StringUtils.strIsNotNull(payTypeId)) {
			pMap.put("payTypeId", payTypeId);
		}
		pMap.put("orgId", getOrgId());

		List<Map<String, Object>> rowlist = freeCartDetailedServices.getPayReportList(pMap);
		String[] titles = { "专业", "班级", "缴费项目","人数", "缴费笔数", "应缴金额", "实缴金额","欠费金额","缴费状态" };
		String[] columnKey = { "majorName", "clazzName", "payTypeName", "tpeopleCount", "tpayCount","tmoney","tpayMoney","tlessMoney","payState" };
		String sheetName = "缴费报表";
		
		try {
			HSSFWorkbook workbook = ExcelUtils.eqEcel(rowlist, titles, sheetName, columnKey);
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			// 设置输出的格式
			response.setContentType("application/x-excel");
			String fileName = "PayReport_" + CalendarUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
			fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------end--------exportExcel");
	}
}
