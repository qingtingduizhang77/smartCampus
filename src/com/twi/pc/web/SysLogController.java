package com.twi.pc.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.base.util.StringUtils;
import com.twi.security.domain.SSCUserDetails;
import com.twi.syslog.domain.CheckErrLog;
import com.twi.syslog.domain.SysLog;
import com.twi.syslog.services.CheckErrLogServices;
import com.twi.syslog.services.SysLogServices;

@Controller
@RequestMapping("/admin_back/log")
public class SysLogController extends BaseController{
	
	@Autowired
	private CheckErrLogServices checkErrLogServices;
	
	@Autowired
	private SysLogServices sysLogServices;
	
	private String getOrgId() {
		SSCUserDetails user = WebHelper.getUser();
		return user.getOrgId();
	}
	
	@ResponseBody
	@RequestMapping(value = "saveCheckErrLog", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveCheckErrLog(HttpServletRequest request, CheckErrLog checkErrLog) {
		checkErrLog.setOrgId(getOrgId());
		SSCUserDetails user = WebHelper.getUser();
		if (StringUtils.strIsNotNull(checkErrLog.getId())) {
			CheckErrLog errLog = checkErrLogServices.getEntityById(checkErrLog.getId());
			errLog.setAdvise(checkErrLog.getAdvise());
			errLog.setProcessMan(user.getOrgName());
			errLog.setProcessTime(new Date());
			errLog.setState(checkErrLog.getState());
			if (!checkErrLogServices.update(errLog)) {
				return createErrorResult("更新失败");
			}
		} else {
			if (!checkErrLogServices.add(checkErrLog)) {
				return createErrorResult("保存失败");
			}
		}
		return createSuccessResult(null);
	}
	

	@ResponseBody
	@RequestMapping(value = "getCheckErrPageList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getCheckErrPageList(HttpServletRequest request,
			@RequestParam(value = "wxOrderNo", required = false) String wxOrderNo,
			@RequestParam(value = "freePoNo", required = false) String freePoNo,
			@RequestParam(value = "startDate", required = false) String beginPayTime,
			@RequestParam(value = "endDate", required = false) String endPayTime,
			@RequestParam(value = "checkMode", required = false) Integer checkMode,
			@RequestParam(value = "errCode", required = false) Integer errCode,
			@RequestParam(value = "state", required = false) Integer state,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<CheckErrLog> page = new Page<CheckErrLog>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(wxOrderNo)) {
			pMap.put("wxOrderNo", wxOrderNo);
		}
		if (StringUtils.strIsNotNull(freePoNo)) {
			pMap.put("freePoNo", freePoNo);
		}
		if (StringUtils.strIsNotNull(beginPayTime)) {
			pMap.put("beginPayTime", beginPayTime);
		}
		if (StringUtils.strIsNotNull(endPayTime)) {
			pMap.put("endPayTime", endPayTime);
		}
		if (checkMode != null) {
			pMap.put("checkMode", checkMode);
		}
		if (errCode != null) {
			pMap.put("errCode", errCode);
		}
		if (state != null) {
			pMap.put("state", state);
		}
		pMap.put("orgId", getOrgId());
		page = checkErrLogServices.getPageList(page, pMap);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());
		contentMap.put("count", page.getTotalCount());
		return createSuccessResult(contentMap);
	}
	
	@ResponseBody
	@RequestMapping(value = "getSysLogPageList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getSysLogPageList(HttpServletRequest request,
			@RequestParam(value = "startDate", required = false) String beginTime,
			@RequestParam(value = "endDate", required = false) String endTime,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<SysLog> page = new Page<SysLog>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(beginTime)) {
			pMap.put("beginTime", beginTime);
		}
		if (StringUtils.strIsNotNull(endTime)) {
			pMap.put("endTime", endTime);
		}
		
		pMap.put("orgId", getOrgId());
		page = sysLogServices.getPageList(page, pMap);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());
		contentMap.put("count", page.getTotalCount());
		return createSuccessResult(contentMap);
	}
	
}
