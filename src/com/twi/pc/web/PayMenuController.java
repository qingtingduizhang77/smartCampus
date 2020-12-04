package com.twi.pc.web;

import java.io.File;
import java.io.IOException;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.base.util.CommonConstants;
//import com.twi.base.util.CalendarUtils;
import com.twi.base.util.PropertiesMsg;
import com.twi.base.util.PropertiesUtils;
import com.twi.paymenu.domain.SysPayMenu;
import com.twi.paymenu.services.PayMenuServices;
import com.twi.security.domain.SSCUserDetails;

/**
 * 缴费菜单
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/admin_back/pay/menu")
public class PayMenuController extends BaseController{

	@Autowired
	private PayMenuServices payMenuServicesImp;
	
	
	/**
	 * 新增/编辑缴费菜单保存
	 * @param imgFile 图片文件
	 * @param request
	 * @param payMenu
	 * @return
	 */
	@RequestMapping(value="/save")
	public Map<String, Object> savePayMenu(@RequestParam(value="file",required = false) MultipartFile imgFile,
			HttpServletRequest request,
			SysPayMenu payMenu){
		logger.info("------start------ : savePayMenu");
		String msg = "";
		Map<String, String> result = new HashMap<String, String>();
		SSCUserDetails user = WebHelper.getUser();
		if (user != null && StringUtils.isBlank(user.getOrgId())) { //学校不存在！
			return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
		}
		String orgId = user.getOrgId();
		payMenu.setOrgId(orgId);
		if (imgFile!= null && !imgFile.isEmpty()) {
			String suffName = imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf(".")+1);
			if(!"jpg".toLowerCase().equalsIgnoreCase(suffName)
					&&!"png".toLowerCase().equalsIgnoreCase(suffName)
					&&!"gif".toLowerCase().equalsIgnoreCase(suffName)
					&&!"jpeg".toLowerCase().equalsIgnoreCase(suffName)){
				return createErrorResult("请上传图片文件!");
			}
			String orgPath = StringUtils.trimToEmpty(orgId).replaceAll("-", "");
			String filePath = "/"+ CommonConstants.ATTACHMENT_PATH +"/"+ orgPath + "/images/paymenu";
			String realPath = PropertiesUtils.getProperty("files") + filePath;
			
			File dir = new File(realPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!realPath.endsWith(File.separator)) {
				realPath = realPath + File.separator;
	        }
			String fileName = imgFile.getOriginalFilename();
			File file= new File(realPath, fileName);
			
			double size = file.length()/1024d;
			if(size > 200){
				return createErrorResult("图片文件大小不超过200Kb");
			}
			
			try {
				FileUtils.copyInputStreamToFile(imgFile.getInputStream(), file);
			} catch (IOException e) {
				msg = "文件保存失败！";
				logger.debug("------error-------" + e.getMessage());
				e.printStackTrace();
			}
			String url = filePath + "/" + fileName;
			payMenu.setImgUrl(url);
		}
		boolean flag = payMenuServicesImp.savePayMenu(payMenu);
		if (!flag) {
			return createErrorResult("数据异常");
		}
		logger.info("------end------ : savePayMenu");
		return createSuccessResult(result, msg);
	}
	
	/**
	 * 缴费菜单列表
	 * @param request
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/list")
	public Map<String, Object> payMenuList(HttpServletRequest request, 
			@RequestParam(value="name", defaultValue = "") String name){
		logger.info("------start------payMenuList");
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

		Page<SysPayMenu> page = new Page<SysPayMenu>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);
		
		page = payMenuServicesImp.getPayMenuPage(page, orgId, name);
		
		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		logger.info("------end------payMenuList");
		return createSuccessResult(result);
	}
	
	
	/**
	 * 缴费菜单详细
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/info")
	public Map<String, Object> getPayMenuInfo(@RequestParam(value = "id", required = true) String id) {
		SysPayMenu payMenu = payMenuServicesImp.getPayMenuById(id);
		return createSuccessResult(payMenu);
	}
	
	/**
	 * 缴费菜单列表
	 * @return
	 */
	@RequestMapping(value="/allList")
	public Map<String, Object> getPayMenuList() {
		logger.info("------start------getPayMenuList");
		Map<String, Object> result = new HashMap<String, Object>();
		SSCUserDetails user = WebHelper.getUser();
		String orgId = "";
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			orgId = user.getOrgId();
		}
		List<SysPayMenu> payMenu = payMenuServicesImp.getPayMenuList(orgId);
		result.put("rows", payMenu);
		logger.info("------end------getPayMenuList");
		return createSuccessResult(result);
	}
}
