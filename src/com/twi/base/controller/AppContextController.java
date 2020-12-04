package com.twi.base.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.twi.base.services.CommonServices;
import com.twi.base.util.PropertiesUtils;
import com.twi.base.util.StringUtils;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.services.SysOrgServices;

/**
 * 
 * @author ouwt
 *系统全局控制器
 */
@Controller
public class AppContextController extends BaseController {

//	@Autowired
//	private CommonServices commonServices;
	@Autowired
	private SysOrgServices sysOrgServicesImp;

	/**
	 * 跳转到PC主页
	 */
	@RequestMapping("pc")
	public String pc(HttpServletRequest request,Model model) {
		String serverName = request.getServerName();
		String sysDns = PropertiesUtils.getProperty("dns");
		SysOrgInfo sysOrgInfo = null;
		if (StringUtils.isNotBlank(sysDns) && sysDns.trim().equals(serverName)) {
			//当前域名与系统主域名匹配
			String orgKey = this.getString(request, "orgId", null);
			if (StringUtils.isBlank(orgKey)) {
				//没有orgKey取默认org
				sysOrgInfo = sysOrgServicesImp.getOrgInfoByIdForEnable(PropertiesUtils.getProperty("testorgId"));
			} else {
				//有orgKey则取key
				sysOrgInfo = sysOrgServicesImp.getOrgInfoByKeyForEnable(orgKey);
			}
		} else {
			//机构子域名
			sysOrgInfo = sysOrgServicesImp.getOrgInfoByDnsForEnable(serverName);
//			if (orgId==null) {
//				orgId=PropertiesUtils.getProperty("testorgId");
//			}
		}
		if (sysOrgInfo==null) { //机构不存在或禁用
			return "/error";
		}
		String orgId = sysOrgInfo.getId();
		String orgName = sysOrgInfo.getName();
		String dns = null;
		switch (request.getServerPort()) {
		case 80:
			dns = "http://"+serverName+request.getContextPath();
			break;
		case 443:
			dns = "https://"+serverName+request.getContextPath();
			break;
		default:
			dns = "http://"+serverName+":"+request.getServerPort()+request.getContextPath();
		}

		model.addAttribute("dns", dns);
		model.addAttribute("orgId", orgId);
		model.addAttribute("orgName", orgName);

		request.getSession().setAttribute("orgId", orgId);
		return "../../pc/index";
	}



	/**
	 * 跳转到PC主页
	 */
	@RequestMapping("index")
	public String index(HttpServletRequest request,Model model) {
		return pc(request,model);
	}


	/**
	 * 跳转到WX主页
	 */
	@RequestMapping("wxiffi/index")
	public String wx(HttpServletRequest request,Model model) {

		String orgKey=this.getString(request, "orgId", null);
		String orgId=null;
		String orgName=null;
		String serverName=request.getServerName();
//		String sysDns=PropertiesUtils.getProperty("dns");

		if(StringUtils.isNotBlank(orgKey))
		{
//			orgId=commonServices.getOrgIdByKey(orgKey);
			orgId = sysOrgServicesImp.getOrgIdByKeyForEnable(orgKey);
		}

		if(orgId==null)
		{
//			orgId=commonServices.getOrgIdByServerName(serverName);
			orgId = sysOrgServicesImp.getOrgIdByDnsForEnable(serverName);
		}
		if(orgId==null)
		{
			orgId=PropertiesUtils.getProperty("testorgId");
		}

//		SysOrgInfo SysOrgInfo=commonServices.getSysOrgInfoById(orgId);
		SysOrgInfo SysOrgInfo = sysOrgServicesImp.getOrgInfoByIdForEnable(orgId);

		if(SysOrgInfo==null)
		{
			return "/error";
		}
		else
		{
			orgName=SysOrgInfo.getName();
			request.getSession().setAttribute("orgId", orgId);
			request.getSession().setAttribute("orgName", orgName);
		}

		String dns=null;
		int serverPort=request.getServerPort();
		if(serverPort==80)
		{
			dns="http://"+request.getServerName()+request.getContextPath();

		}
		else if(serverPort==443)
		{
			dns="https://"+request.getServerName()+request.getContextPath();

		}
		else
		{
			dns="http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		}


		model.addAttribute("dns", dns);
		model.addAttribute("orgId", orgId);
		model.addAttribute("orgName", orgName);

		return "../../wx/index";
	}


	@RequestMapping(value="/logout")
	public  @ResponseBody Map<String, Object> logout(HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", 1);
		result.put("msg", "登出成功！");
		return  result;
	}

	@RequestMapping(value="tt/test")
	public   String test(HttpServletRequest request){

		request.getSession().setAttribute("orgId","0af3ce9c-184e-4984-841d-3a4ea8ee33e0");

		return  "test";
	}


}
