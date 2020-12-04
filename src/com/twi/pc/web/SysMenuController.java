package com.twi.pc.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.security.domain.SSCUserDetails;
import com.twi.sysmenu.domain.SysMenu;
import com.twi.sysmenu.services.SysMenuServices;

/**
 * 系统菜单表
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/admin_back/sys/menu")
public class SysMenuController  extends BaseController{
	
	@Autowired
	private SysMenuServices sysMenuServices;
	
	
	/**
	 * 缴费菜单列表
	 * @param request
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/getRoleMenu")
	public Map<String, Object> getRoleMenu(HttpServletRequest request){
		SSCUserDetails user = WebHelper.getUser();
		Map<String, Object> result = new HashMap<String, Object>();
		String  roleId = this.getString(request, "roleId", null); 
		
		if(roleId==null)
		{
			return this.createErrorResult("角色id不能为空");
		}
		
		SysMenu sysMenu=sysMenuServices.getSysMenuBYRole(roleId, user.getOrgId());
		if(sysMenu!=null)
		{
			result.put("menuJson", sysMenu.getContext());
		}
		else
		{
			result.put("menuJson", null);
		}
		
		return createSuccessResult(result);
	}

}
