package com.twi.wx.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.twi.base.controller.BaseController;
import com.twi.base.services.CommonServices;
import com.twi.base.util.PropertiesUtils;
import com.twi.base.util.StringUtils;
import com.twi.images.services.ImagesServices;
import com.twi.paymenu.domain.SysPayMenu;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.services.SysOrgServices;
import com.twi.wx.services.WxServices;

import net.sf.json.JSONObject;

/**
 * 微信公众帐号入口
 * 
 * @author ouwt
 *
 */
@Controller
public class WebController extends BaseController {
	@Autowired
	private CommonServices commonServices;
	@Autowired
	private WxServices wxServices;
	@Autowired
	private ImagesServices imagesServices;
	@Autowired
	private SysOrgServices sysOrgServicesImp;


	/**
	 * 自助缴费 、新生入学
	 * 
	 * @param id
	 *            缴费类型名称
	 */
	//@RequestMapping("/mp/school/studentqu")
	@RequestMapping("/wx/paymenttype")
	public String wxViewStudentqu(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "id", required = true) String id, RedirectAttributes redirectAttributes) {

		// 登录状态
		boolean isLogin = checkLogin(request);
		// 当前会话ORG
		String  orgId = (String)request.getSession().getAttribute("orgId");
		
		if(StringUtils.isBlank(orgId)) {
			return "/error";
		}
		if(isLogin) {
			String menuName = id;
			String menuUrl = "";
			String menuId = "";
			SysPayMenu menu = commonServices.getSysPayMenu(orgId, menuName);
			if(menu != null) {
				menuUrl = menu.getWebUrl();
				menuId = menu.getId();
				if(StringUtils.isBlank(menuUrl)) {
					//获取默认的模板
					menuUrl = PropertiesUtils.getProperty("wxroute_pay_default");
				}
			}
			return "redirect:/mp/school/stupay?aaa889911=1#" + menuUrl + "?meuid=" + menuId;
		} else {
			request.getSession().setAttribute("menuName", id);
			redirectAttributes.addAttribute("inType", "studentqu");
			return "redirect:/mp/school/wx";
		}
	}

	//	/**
	//	 * 智慧校园->标书费
	//	 * 
	//	 * @param id
	//	 *            缴费类型名称(标书费)
	//	 * 
	//	 */
	//	@RequestMapping("/mp/school/customprice/typename/{id}")
	//	public String wxViewStudentqu2(@PathVariable("id") String id, HttpServletRequest request,
	//			HttpServletResponse response, RedirectAttributes redirectAttributes) {
	//		boolean isLogin=false;
	//		String reorgId=this.getString(request, "orgId", null);
	//		String seorgId = (String) request.getSession().getAttribute("orgId");
	//		
	//		String  orgId=null;
	//		
	//		SysOrgInfo org =null;
	//		if(reorgId!=null && !reorgId.equals(""))
	//		{
	//			 org = commonServices.getSysOrgInfoByKey(reorgId);
	//			if(org==null)
	//			{
	//				return "/error";
	//			}
	//			
	//			orgId=org.getId();
	//			if(seorgId!=null && !seorgId.equals("") && !seorgId.equals(org.getId()))
	//			{
	//				isLogin=true;
	//			}
	//			
	//		}
	//		
	//		
	//		String wxOpenId = (String) request.getSession().getAttribute("wxOpenId");
	//		if (wxOpenId == null || isLogin) {
	//			request.getSession().setAttribute("menuName", id);
	//			redirectAttributes.addAttribute("inType", "studentqu");
	//			if(orgId!=null)
	//			{
	//				request.getSession().setAttribute("orgId", orgId);
	//			}
	//			return "redirect:/mp/school/wx";
	//		} else {
	//			 orgId = (String) request.getSession().getAttribute("orgId");
	//			String orgName = (String) request.getSession().getAttribute("orgName");
	//			String menuName = id;
	//			String menuUrl = "";
	//			String menuId = "";
	//			SysPayMenu menu = commonServices.getSysPayMenu(orgId, menuName);
	//			if (menu != null) {
	//				menuUrl = menu.getWebUrl();
	//				menuId = menu.getId();
	//			}
	//
	//			return "redirect:/mp/school/stupay?aaa889911=1#" + menuUrl + "?meuid=" + menuId;
	//
	//		}
	//
	//	}

	/**
	 * 自助缴费->更多
	 */
	//@RequestMapping("/mp/school/paymenttype")
	@RequestMapping("/wx/paymentlist")
	public String wxViewPaymentType(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		// 登录状态
		boolean isLogin = checkLogin(request);
		// 当前会话ORG
		String  orgId = (String)request.getSession().getAttribute("orgId");
		
		if(StringUtils.isBlank(orgId)) {
			return "/error";
		}
		if(isLogin) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("paymenttype");
		} else {
			redirectAttributes.addAttribute("inType", "paymenttype");
			return "redirect:/mp/school/wx";
		}
	}

	//	/**
	//	 * 综合查询->添加绑定信息
	//	 */
	//	@RequestMapping("/mp/Schoolbinding/MyBindingIndex")
	//	public String MyBindingIndex(HttpServletRequest request, HttpServletResponse response,
	//			RedirectAttributes redirectAttributes) {
	//
	//		boolean isLogin=false;
	//		String reorgId=this.getString(request, "orgId", null);
	//		String seorgId = (String) request.getSession().getAttribute("orgId");
	//		
	//		String  orgId=null;
	//		
	//		SysOrgInfo org =null;
	//		if(reorgId!=null && !reorgId.equals(""))
	//		{
	//			 org = commonServices.getSysOrgInfoByKey(reorgId);
	//			if(org==null)
	//			{
	//				return "/error";
	//			}
	//			
	//			orgId=org.getId();
	//			if(seorgId!=null && !seorgId.equals("") && !seorgId.equals(org.getId()))
	//			{
	//				isLogin=true;
	//			}
	//			
	//		}
	//		String wxOpenId = (String) request.getSession().getAttribute("wxOpenId");
	//
	//		if (wxOpenId == null || isLogin) {
	//			redirectAttributes.addAttribute("inType", "MyBindingIndex");
	//			if(orgId!=null)
	//			{
	//				request.getSession().setAttribute("orgId", orgId);
	//			}
	//			return "redirect:/mp/school/wx";
	//		} else {
	//
	//			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("MyBindingIndex");
	//		}
	//
	//	}

	/**
	 * 综合查询->缴费查询
	 */
	//@RequestMapping("/mp/school/check_bind")
	@RequestMapping("/wx/searchpay")
	public String check_bind(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		// 登录状态
		boolean isLogin = checkLogin(request);
		// 当前会话ORG
		String  orgId = (String)request.getSession().getAttribute("orgId");
		
		if(StringUtils.isBlank(orgId)) {
			return "/error";
		}
		if(isLogin) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("check_bind");
		} else {
			redirectAttributes.addAttribute("inType", "check_bind");
			return "redirect:/mp/school/wx";
		}
	}

	/**
	 * 智慧校园->学生绑定
	 */
	//@RequestMapping("/mp/school/wx_stubinding")
	@RequestMapping("/wx/bindlist")
	public String wx_stubinding(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		// 登录状态
		boolean isLogin = checkLogin(request);
		// 当前会话ORG
		String  orgId = (String)request.getSession().getAttribute("orgId");
		
		if(StringUtils.isBlank(orgId)) {
			return "/error";
		}
		if(isLogin) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("wx_stubinding");
		} else {
			redirectAttributes.addAttribute("inType", "wx_stubinding");
			return "redirect:/mp/school/wx";
		}
	}

	//	/**
	//	 * 智慧校园->解除绑定
	//	 */
	//	//@RequestMapping("/mp/school/del_stubind")
	//	@RequestMapping("/wx/bind_del")
	//	public String del_stubind(HttpServletRequest request, HttpServletResponse response,
	//			RedirectAttributes redirectAttributes) {
	//
	//		boolean isLogin=false;
	//		String reorgId=this.getString(request, "orgId", null);
	//		String seorgId = (String) request.getSession().getAttribute("orgId");
	//		
	//		String  orgId=null;
	//		
	//		SysOrgInfo org =null;
	//		if(reorgId!=null && !reorgId.equals(""))
	//		{
	//			 org = commonServices.getSysOrgInfoByKey(reorgId);
	//			if(org==null)
	//			{
	//				return "/error";
	//			}
	//			
	//			orgId=org.getId();
	//			if(seorgId!=null && !seorgId.equals("") && !seorgId.equals(org.getId()))
	//			{
	//				isLogin=true;
	//			}
	//			
	//		}
	//		String wxOpenId = (String) request.getSession().getAttribute("wxOpenId");
	//
	//		if (wxOpenId == null || isLogin) {
	//			redirectAttributes.addAttribute("inType", "del_stubind");
	//			if(orgId!=null)
	//			{
	//				request.getSession().setAttribute("orgId", orgId);
	//			}
	//			return "redirect:/mp/school/wx";
	//		} else {
	//
	//			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("del_stubind");
	//		}
	//
	//	}

	/**
	 * 智慧校园->校园缴费(主页)
	 */
	//@RequestMapping("/mp/school/wxindex")
	@RequestMapping("/wx/index")
	public String wxindex(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		// 登录状态
		boolean isLogin = checkLogin(request);
		// 当前会话ORG
		String  orgId = (String)request.getSession().getAttribute("orgId");
		
		if(StringUtils.isBlank(orgId)) {
			return "/error";
		}
		if(isLogin) {
			return "redirect:/mp/school/stupay?aaa889911=1";
		} else {
			redirectAttributes.addAttribute("inType", "wxindex");
			return "redirect:/mp/school/wx";
		}
	}

	/**
	 * 取微信Openid接口
	 * 
	 * @return
	 */
	@RequestMapping("/mp/school/wx")
	public String wx(HttpServletRequest request, HttpServletResponse response, Model model) {
		String inType = this.getString(request, "inType", null);
//		String serverName = request.getServerName();
		String orgId = (String)request.getSession().getAttribute("orgId");

		if(StringUtils.isBlank(orgId)) {
			return "/error";
		}
//		if(orgId==null)
//		{
//			orgId = commonServices.getOrgIdByServerName(serverName);
//		}
		try {
//			SysOrgInfo org = commonServices.getSysOrgInfoById(orgId);
			SysOrgInfo org = sysOrgServicesImp.getOrgInfoByIdForEnable(orgId);
			String wxAppid = "";
			if(org != null) {
				wxAppid = org.getAppid();
			} else {
				return "/error";
			}
			String redirect_uri = null;
			int serverPort = request.getServerPort();
			if (serverPort == 443) {
				redirect_uri = "https://" + request.getServerName() + request.getContextPath() + "/mp/school/rewx/"
						+ inType;
			} else {
				redirect_uri = "http://" + request.getServerName() + request.getContextPath() + "/mp/school/rewx/"
						+ inType;
			}

			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + wxAppid + "&redirect_uri="
					+ redirect_uri + "&response_type=code&scope=snsapi_base";

			// 拼装获取openID的方法，获取好openid之前要跳转到进入付费的后台方法为“/mp/school/stupay”，并且需要将菜单入口id，openid，设置好。
			return "redirect:" + url;
		} catch (Exception e) {
			e.printStackTrace();
			return "/error";
		}

	}

	/**
	 * 
	 * 微信Openid回调接口
	 * 
	 * @return
	 */
	@RequestMapping("/mp/school/rewx/{inType}")
	public String stuednPay(@PathVariable("inType") String inType, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String wxOpenId = null;
//		String serverName = request.getServerName();
		String orgId = (String) request.getSession().getAttribute("orgId");
		String wxCode = request.getParameter("code");
		String orgName = "";

		try {

//			if(orgId==null)
//			{
//				orgId = commonServices.getOrgIdByServerName(serverName);
//			}
//
//			if(orgId==null)
//			{
//				orgId=PropertiesUtils.getProperty("testorgId");
//			}
			if(StringUtils.isBlank(orgId)) {
				return "/error";
			}
//			SysOrgInfo org = commonServices.getSysOrgInfoById(orgId);
			SysOrgInfo org = sysOrgServicesImp.getOrgInfoByIdForEnable(orgId);
			String wxAppid = "";
			String wxAppSecret = "";
			if (org != null) {
				wxAppid = org.getAppid();
				wxAppSecret = org.getAppsecret();
				orgName = org.getName();
				request.getSession().setAttribute("orgName", orgName);
				request.getSession().setAttribute("orgId", orgId);
			} else {
				return "/error";
			}

			String contentStr = null;
			contentStr = wxServices.getWxOauthJson(wxAppid, wxAppSecret, wxCode);// 获取微信OPENID

			if (StringUtils.isNotBlank(contentStr)) {
				JSONObject a = JSONObject.fromObject(contentStr);
				if (a.has("openid")) {
					wxOpenId = a.getString("openid");// 获取openId
					request.getSession().setAttribute("wxOpenId", wxOpenId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

			return "/error";
		}

		if (inType != null && inType.equals("studentqu")) {
			String menuName = (String) request.getSession().getAttribute("menuName");
			String menuUrl = "";
			String menuId = "";
			SysPayMenu menu = commonServices.getSysPayMenu(orgId, menuName);
			if (menu != null) {
				menuUrl = menu.getWebUrl();
				menuId = menu.getId();
				if(StringUtils.isBlank(menuUrl)) {
					//获取默认的模板
					menuUrl = PropertiesUtils.getProperty("wxroute_pay_default");
				}
			}
			// return new ModelAndView(reUrl+"#[查出的菜单的前端url地址]?meuid=[菜单的iD]",
			// map);
			return "redirect:/mp/school/stupay?aaa889911=1#" + menuUrl + "?meuid=" + menuId;
		} else if (inType != null && inType.equals("paymenttype")) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("paymenttype");
		} else if (inType != null && inType.equals("MyBindingIndex")) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("MyBindingIndex");
		} else if (inType != null && inType.equals("del_stubind")) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("del_stubind");
		} else if (inType != null && inType.equals("wx_stubinding")) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("wx_stubinding");
		} else if (inType != null && inType.equals("check_bind")) {
			return "redirect:/mp/school/stupay?aaa889911=1#" + PropertiesUtils.getProperty("check_bind");
		}
		else {
			return "redirect:/mp/school/stupay?aaa889911=1";
		}

	}

	/*
	 * 主入口
	 */
	@RequestMapping("/mp/school/stupay")
	public String stuednPay(HttpServletRequest request, HttpServletResponse response, Model model) {

		String wxOpenId = (String) request.getSession().getAttribute("wxOpenId");
		String orgId = (String) request.getSession().getAttribute("orgId");
		String orgName = (String) request.getSession().getAttribute("orgName");
		String dns = null;
		int serverPort = request.getServerPort();
		if (serverPort == 80) {
			dns = "http://" + request.getServerName() + request.getContextPath();

		} else if (serverPort == 443) {
			dns = "https://" + request.getServerName() + request.getContextPath();

		} else {
			dns = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		}
		if(StringUtils.isBlank(orgId)) {
			return "/error";
		}
		if (StringUtils.isBlank(wxOpenId)) {
			return "/error";
		}
		request.getSession().setAttribute("dns",dns);

		model.addAttribute("dns", dns);
		model.addAttribute("orgId", orgId);
		model.addAttribute("orgName", orgName);
		return "../../wx/index";
	}

	@ResponseBody
	@RequestMapping(value = "/wx/getImageList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getImageList(HttpServletRequest request) {
		String orgId = (String) request.getSession().getAttribute("orgId");
		if (orgId == null) {
			return createErrorResult("学校不存在");
		}
		List<Map<String, Object>> imgList= imagesServices.getImageList(orgId);
		return createSuccessResult(imgList);
	}
	
	/**
	 * 检查登录状态
	 * @return
	 */
	private boolean checkLogin(HttpServletRequest request) {
		// 登录状态，未登录
		boolean isLogin = false;
		try {
			// 组织KEY参数
			String orgKey = this.getString(request, "orgId", null);
			// 会话组织
			String seorgId = (String) request.getSession().getAttribute("orgId");
			// 用户微信登录ID
			String wxOpenId = (String) request.getSession().getAttribute("wxOpenId");

			if(StringUtils.isNotBlank(orgKey)) {
				// 有新组织验证
				SysOrgInfo sysOrgInfo = sysOrgServicesImp.getOrgInfoByKeyForEnable(orgKey);
				if(sysOrgInfo==null) {
					// 指定组织不存在，清空session，异常
					cleanSession(request);
					isLogin = false;
				} else {
					// 指定组织存在
					if(StringUtils.isNotBlank(seorgId) && StringUtils.isNotBlank(wxOpenId) && seorgId.equals(sysOrgInfo.getId())) {
						// 存在会话，且存在微信登录ID,且与指定组织相同，视为已登录
						isLogin = true;
					} else {
						// 新组织，没有微信登录ID，清空session，重新设置ORG
						cleanSession(request);
						request.getSession().setAttribute("orgId", sysOrgInfo.getId());
						isLogin = false;
					}
				}
			} else {
				// 无新组织验证
				if(StringUtils.isNotBlank(seorgId) && StringUtils.isNotBlank(wxOpenId)) {
					// 存在会话，且存在微信登录ID,视为已登录
					isLogin = true;
				} else {
					// 否则
					cleanSession(request);
					request.getSession().setAttribute("orgId", seorgId);
					isLogin = false;
				}
			}
		} catch(Exception e) {
			// 清空session，异常
			cleanSession(request);
			isLogin = false;
		}
		return isLogin;
	}
	/**
	 * 清除会话状态
	 * @return
	 */
	private void cleanSession(HttpServletRequest request) {
		request.getSession().setAttribute("orgId", null);
		request.getSession().setAttribute("orgName", null);
		request.getSession().setAttribute("wxOpenId", null);
		request.getSession().setAttribute("dns", null);
	}

}
