package com.twi.wx.web;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twi.base.controller.BaseController;
import com.twi.base.util.CalendarUtils;
import com.twi.student.domain.SysStudent;
import com.twi.student.domain.SysWxStudent;
import com.twi.student.services.SysStudentServices;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.services.SysOrgServices;
import com.twi.wechat.services.WxPayServices;
import com.twi.wx.services.WxServices;

/**
 * 微信
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/wx")
public class WxController extends BaseController{

	@Autowired
	private WxServices wxServicesImp;
	
	@Autowired
	private SysStudentServices sysStudentServicesImp;
	
	@Autowired
	private WxPayServices wxPayServicesImp;
	
	@Autowired
	private SysOrgServices sysOrgServicesImp;
	
	/**
	 * 绑定学生信息
	 * @param studentName 姓名
	 * @param studentCode 学号
	 * @return
	 */
	@RequestMapping(value="/bind/student/info")
	public Map<String, Object> bindStudentInfo(HttpServletRequest request,
			@RequestParam(value="studentName", defaultValue = "") String studentName,
			@RequestParam(value="studentCode", defaultValue = "") String studentCode){
		logger.info("------start------bindStudentInfo");
		Map<String, Object> result = new HashMap<String, Object>();
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		String orgId = request.getSession().getAttribute("orgId").toString();
		String openId = request.getSession().getAttribute("wxOpenId").toString();
		SysStudent student = sysStudentServicesImp.getStudentByNameAndCode(orgId, studentName, studentCode, "1");
		if (student == null) {
			return createErrorResult("该学生不存在！");
		}
		SysWxStudent wxStudent = wxServicesImp.getWxStudentInfo(student.getId(), null, null);
		if (wxStudent!=null && !openId.equals(wxStudent.getOpenId())) {
			return createErrorResult("该学生已经被其他用户绑定");
		}else if(wxStudent!=null && openId.equals(wxStudent.getOpenId())) {
			return createErrorResult("该学生已做过绑定");
		}
		SysWxStudent sysWxStudent = new SysWxStudent();
		sysWxStudent.setOpenId(openId);
		sysWxStudent.setStudentId(student.getId());
		sysWxStudent.setStudentName(student.getName());
		sysWxStudent.setStudentCode(student.getCode());
		sysWxStudent.setCreateDate(new Date());
		wxServicesImp.addWxStudent(sysWxStudent);
		logger.info("------end------bindStudentInfo");
		return createSuccessResult(result, "绑定成功！");
	}
	
	
	/**
	 * 解绑学生信息
	 * @param id 
	 * @return
	 */
	@RequestMapping(value="/unbind/student/info")
	public Map<String, Object> unbindStudentInfo(HttpServletRequest request,
			@RequestParam(value="id", defaultValue = "") String id){
		logger.info("------start------unbindStudentInfo");
		Map<String, Object> result = new HashMap<String, Object>();
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		if (StringUtils.isEmpty(id)) {
			return createErrorResult("参数异常");
		}
		String openId = request.getSession().getAttribute("wxOpenId").toString();
		SysWxStudent wxStudent = wxServicesImp.getWxStudentById(id);
		if (wxStudent == null) {
			return createErrorResult("绑定的学生信息不存在！");
		}
		if (!openId.equals(wxStudent.getOpenId())) {
			return createErrorResult("绑定的学生信息不存在！");
		}
		if (!wxServicesImp.delWxStudent(wxStudent)) {
			return createErrorResult("解绑失败！");
		}
		logger.info("------end------unbindStudentInfo");
		return createSuccessResult(result, "解绑成功！");
	}
	
	/**
	 * 绑定的学生列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/bind/student/list")
	public Map<String, Object> bindStudentList(HttpServletRequest request){
		logger.info("------start------bindStudentList");
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		if (request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		String orgId = request.getSession().getAttribute("orgId").toString();
		String openId = request.getSession().getAttribute("wxOpenId").toString();
		List<SysWxStudent> list = wxServicesImp.getWxStudentList(orgId,openId);
		result.put("rows", list);
		logger.info("------end------bindStudentList");
		return createSuccessResult(result);
	}
	
	/**
	 * 数据统计
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/data/stat")
	public Map<String, Object> statisticsData(
			@RequestParam(value="date", defaultValue = "", required=true) String date){
		try {
			List<SysOrgInfo> orgList = sysOrgServicesImp.getOrgInfoList();
			if (orgList != null && orgList.size() > 0) {
				for (SysOrgInfo org : orgList) {
					Date reDate = CalendarUtils.dateAddOrSub(CalendarUtils.simpleparse(date), Calendar.DAY_OF_MONTH, -1);
					List<Map<String, Object>> listMap = wxPayServicesImp.getSummaryList(reDate, org.getId());
					if (listMap != null && listMap.size() > 0) {
						for (Map<String, Object> map : listMap) {
							String orgId = String.valueOf(map.get("org_id") == null ? "" : map.get("org_id"));
							if (StringUtils.isEmpty(orgId)) {
								continue;
							}
							wxPayServicesImp.addOrUpdTradingSum(reDate, orgId, map);
						}
					}else{
						wxPayServicesImp.addOrUpdTradingSum(reDate, org.getId(), new HashMap<String, Object>());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResult("fail");
		}
		return createSuccessResult(null, "success");
	}
	
	
	/**
	 * 下载对账单
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/download/bill")
	public Map<String, Object> downloadBill(
			@RequestParam(value="orgId", defaultValue = "", required=true) String orgId,
			@RequestParam(value="startDate", defaultValue = "", required=true) String startDate,
			@RequestParam(value="endDate", defaultValue = "", required=true) String endDate){
		try {
			if (StringUtils.isEmpty(orgId)) {
				return createErrorResult("机构ID不能空");
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
