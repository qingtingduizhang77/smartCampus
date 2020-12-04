package com.twi.pc.web;

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
import com.twi.base.util.ExcelUtils;
import com.twi.report.services.ReportServices;
import com.twi.security.domain.SSCUserDetails;

@Controller
@RequestMapping("/admin_back/report")
public class ReportController extends BaseController{
	
	@Resource(name = "reportServices")
	private ReportServices reportServices;
	
	
	/**
	 * 根据付费类型id_取帐套
	 * @param payTypeId 付费类型id_
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getFreeCartByPayTypeId", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getFreeCartByPayTypeId(HttpServletRequest request, @RequestParam(value = "payTypeId") String payTypeId) {
		 Map<String, Object> result = new HashMap<String, Object>();
		SSCUserDetails user = WebHelper.getUser();
		List<Map<String, Object>> cartList = reportServices.getFreeCartByPayTypeId(payTypeId, user.getOrgId());
		
		result.put("rows", cartList);
		return createSuccessResult(result);
	}
	
	@ResponseBody
	@RequestMapping(value = "project", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> project(HttpServletRequest request, @RequestParam(value = "payTypeId") String payTypeId) {
		 Map<String, Object> result = new HashMap<String, Object>();
		SSCUserDetails user = WebHelper.getUser();
		// 分页参数：
		int pageNo = getInt(request, "pageNo", 1); // 当前面
		int pageSize = getInt(request, "pageSize", 10); // 每页显示条数数
		boolean autoCount = getBoolean(request, "autoCount", true); // 是否显示总条数
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		String studentCode=this.getString(request, "studentCode", null);
		String studentName=this.getString(request, "studentName", null);
		String majorId=this.getString(request, "majorId", null);
		String majorName=this.getString(request, "majorName", null);
		
		String clazzName=this.getString(request, "clazzName", null);
		String freeCartId=this.getString(request, "freeCartId", null);
		String mobileMumber=this.getString(request, "mobileMumber", null);
		Date startDate=this.getDate(request, "startDate", null);
		Date endDate=this.getDate(request, "endDate", null);
		
		page =reportServices.getpRojectReport(page, payTypeId, studentCode, studentName, majorId,majorName, clazzName, freeCartId,mobileMumber, startDate, endDate, user.getOrgId());
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		return createSuccessResult(result);
	}
	
	@ResponseBody
	@RequestMapping(value = "excelProject", method = { RequestMethod.POST, RequestMethod.GET })
	public void exceProject(HttpServletRequest request,HttpServletResponse response, @RequestParam(value = "payTypeId") String payTypeId) {
		
		SSCUserDetails user = WebHelper.getUser();
		String studentCode=this.getString(request, "studentCode", null);
		String studentName=this.getString(request, "studentName", null);
		String majorId=this.getString(request, "majorId", null);
		String majorName=this.getString(request, "majorName", null);
		String clazzName=this.getString(request, "clazzName", null);
		String freeCartId=this.getString(request, "freeCartId", null);
		String mobileMumber=this.getString(request, "mobileMumber", null);
		Date startDate=this.getDate(request, "startDate", null);
		Date endDate=this.getDate(request, "endDate", null);
		
		List<Map<String, Object>> rowlist=reportServices.getpRojectReport(payTypeId, studentCode, studentName, majorId,majorName, clazzName, freeCartId,mobileMumber, startDate, endDate,  user.getOrgId());
		String[] titles={"订单号","商户订单号","缴费账套","用户标识","金额","支付状态","支付时间","学生姓名","手机号码","学号","专业","班级","是否对帐","商品名称","备注"};
		String[] columnKey={"wx_order_no","order_no","free_Cart_name","openid_","payMoney","state_str","pay_time","Student_name","Mobile_number","Student_code","major_name","clazz_name","check_state_str","product_name","remark_"};
	    
		String sheetName="缴费明细";
		
		try {
			
			response.setContentType("text/html;charset=utf-8");
			request.setCharacterEncoding("UTF-8");
			String fileName ="缴费明细"+System.currentTimeMillis()+".xls";
		 	fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
		 	HSSFWorkbook  workbook=ExcelUtils.eqEcel(rowlist, titles, sheetName, columnKey);
            response.reset();  
            response.setContentType("contentType=application/vnd.ms-excel");  
            response.setHeader("Content-disposition","attachment;filename="+fileName);  
            response.setHeader("Connection","close");
            workbook.write(response.getOutputStream());  
            response.flushBuffer();  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
