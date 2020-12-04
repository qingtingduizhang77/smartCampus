package com.twi.pc.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.paytype.domain.SysPayType;
import com.twi.paytype.services.PayTypeServices;
import com.twi.security.domain.SSCUserDetails;

/**
 * 缴费类型
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/admin_back/pay/type")
public class PayTypeController extends BaseController{

	@Autowired
	private PayTypeServices payTypeServicesImp;
	
	/**
	 * 缴费类型列表
	 * @param request
	 * @param name
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="/list")
	public Map<String, Object> payTypeList(HttpServletRequest request, 
			@RequestParam(value="name", defaultValue = "") String name,
			@RequestParam(value="startDate", defaultValue = "") String startDate,
			@RequestParam(value="endDate", defaultValue = "") String endDate){
		logger.info("------start------payTypeList");
		Map<String, Object> result = new HashMap<String, Object>();
		// 分页参数：
		int pageNo = getInt(request, "pageNo", 1); // 当前面
		int pageSize = getInt(request, "pageSize", 10); // 每页显示条数数
		boolean autoCount = getBoolean(request, "autoCount", true); // 是否显示总条数

		Page<SysPayType> page = new Page<SysPayType>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId=user.getOrgId();
		}
		
		page = payTypeServicesImp.getPayTypePage(page, name, startDate, endDate, orgId);
		
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		logger.info("------end------payTypeList");
		return createSuccessResult(result);
	}
	
	/**
	 * 编辑/新增保存缴费类型
	 * @param payType
	 * @return
	 */
	@RequestMapping(value="/save")
	public Map<String, Object> savePayType(SysPayType payType){
		logger.info("------start------ : savePayType");
		Map<String, String> result = new HashMap<String, String>();
		SSCUserDetails user = WebHelper.getUser();
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			payType.setOrgId(user.getOrgId());
		}
		boolean flag = payTypeServicesImp.savePayType(payType);
		if (!flag) {
			return createErrorResult("数据异常");
		}
		logger.info("------end------ : savePayType");
		return createSuccessResult(result, "");
	}
	
	/**
	 * 更新缴费类型状态
	 * @param ids
	 * @param state
	 * @return
	 */
	@RequestMapping(value="/update/state")
	public Map<String, Object> updateState(@RequestParam(value="ids", defaultValue = "") String ids,
			@RequestParam(value="state", defaultValue = "") String state){
		if (StringUtils.isEmpty(ids)) {
			return createErrorResult("参数异常");
		}
		boolean flag = true;
		try {
			if (StringUtils.isEmpty(state)) {
				state = "1";
			}
			flag = payTypeServicesImp.updateState(ids, state);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		if (!flag) {
			return createErrorResult("操作失败");
		} 
		return createSuccessResult(null, "操作成功");
	}
	
	/**
	 * 缴费类型详细
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/info")
	public Map<String, Object> getPayTypeInfo(@RequestParam(value = "id", required = true) String id) {
		SysPayType payType = payTypeServicesImp.getPayTypeById(id);
		return createSuccessResult(payType);
	}
	
	/**
	 * 缴费菜单列表
	 * @return
	 */
	@RequestMapping(value="/allList")
	public Map<String, Object> getPayTypeList() {
		logger.info("------start------getPayTypeList");
		Map<String, Object> result = new HashMap<String, Object>();
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		List<SysPayType> payTypeList = payTypeServicesImp.getPayTypeList(orgId);
		result.put("rows", payTypeList);
		logger.info("------end------getPayTypeList");
		return createSuccessResult(result);
	}
}
