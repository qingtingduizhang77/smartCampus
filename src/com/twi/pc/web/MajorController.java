package com.twi.pc.web;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.base.util.CommonConstants;
import com.twi.base.util.DateUtils;
import com.twi.base.util.ImportExcelUtil;
import com.twi.base.util.PropertiesMsg;
import com.twi.base.util.PropertiesUtils;
import com.twi.base.util.StringUtils;
import com.twi.major.domain.Major;
import com.twi.major.services.MajorServices;
import com.twi.security.domain.SSCUserDetails;

@Controller
@RequestMapping("/admin_back/major")
public class MajorController extends BaseController {

	@Resource(name = "majorServices")
	private MajorServices majorServices;

	private String getOrgId() {
		SSCUserDetails user = WebHelper.getUser();
		return user.getOrgId();
	}

	@ResponseBody
	@RequestMapping(value = "query", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> query(HttpServletRequest request, @RequestParam(value = "id") String id) {

		Major major = majorServices.getMajorById(id);
		if (major == null) {
			return createErrorResult("专业不存在！");
		}
		major.setOrgId(getOrgId());
		return createSuccessResult(major);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> create(HttpServletRequest request, Major major) {
		major.setOrgId(getOrgId());
		if (!majorServices.addMajor(major)) {
			return createErrorResult("创建失败！");
		}
		return createSuccessResult(null);
	}

	@ResponseBody
	@RequestMapping(value = "update", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> update(HttpServletRequest request, Major major) {
		major.setOrgId(getOrgId());
		if (!majorServices.updMajor(major)) {
			return createErrorResult("保存编辑失败！");
		}
		return createSuccessResult(null);
	}

	@ResponseBody
	@RequestMapping(value = "delete", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> delete(HttpServletRequest request, @RequestParam(value = "id") String id) {
		if (!majorServices.delMajorById(id)) {
			return createErrorResult("删除失败！");
		}
		return createSuccessResult(null);
	}

	@ResponseBody
	@RequestMapping(value = "batchDelete", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> batchDelete(HttpServletRequest request, @RequestParam String idStr) {
		String[] ids = idStr.split(",");
		if (!majorServices.batchDelMajor(ids)) {
			return createErrorResult("删除失败！");
		}
		return createSuccessResult(null);
	}

	@ResponseBody
	@RequestMapping(value = "getPageList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getPageList(HttpServletRequest request,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "beginDate", required = false) String beginDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<Major> page = new Page<Major>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(name)) {
			pMap.put("name", name);
		}
		if (StringUtils.strIsNotNull(beginDate)) {
			pMap.put("beginDate", beginDate);
		}
		if (StringUtils.strIsNotNull(endDate)) {
			pMap.put("endDate", endDate);
		}
		pMap.put("orgId", getOrgId());
		page = majorServices.getMajorPage(page, pMap);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("count", page.getTotalCount());
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());

		return createSuccessResult(contentMap);
	}

	@ResponseBody
	@RequestMapping(value = "getMajorList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getMajorList(HttpServletRequest request,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "1000") Integer pageSize) {

		Page<Major> page = new Page<Major>();
		page.setPageNo(pageNo);
		page.setPageSize(pageSize);
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("orgId", getOrgId());
		page = majorServices.getMajorPage(page, pMap);
		Map<String, Object> contentMap = new HashMap<String, Object>();

		contentMap.put("majorJson", page.getResult());
		return contentMap;
	}

	@ResponseBody
	@RequestMapping(value = "importExcel", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> importExcel(HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile multipartFile) throws Exception {
		String orgId = getOrgId();
		if (StringUtils.isBlank(orgId)) { //学校不存在！
			return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
		}
		if (null == multipartFile || multipartFile.isEmpty()) {
			return createErrorResult("上传文件失败！");
		}
		// 获取文件名
		String fileName = multipartFile.getOriginalFilename();
		// 获取文件后缀
		String prefix = fileName.substring(fileName.lastIndexOf("."));
		if (!prefix.equals(".xlsx") && !prefix.equals(".xls")) {
			return createErrorResult("上传文件格式错误！");
		}
		logger.info("------已接收到上传文件-----执行校验文件模板-----");
		try {
			// 保存文件
			String orgPath = StringUtils.trimToEmpty(orgId).replaceAll("-", "");
			String filePath = "/"+ CommonConstants.ATTACHMENT_PATH +"/"+ orgPath + "/excel/major";
			String path = PropertiesUtils.getProperty("files") + filePath;
			String rename = DateUtils.getAsString(new Date(), DateUtils.FORMAT_DATE_TIME2)+"_"+fileName;
			File file = new File(path, rename);
			FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
			// 读取内容
			InputStream in = multipartFile.getInputStream();
			Map<String, Object> objMap = new HashMap<String, Object>();
			try {
				objMap = new ImportExcelUtil().getMapByExcel(in, fileName,true);
			} catch (Exception e) {
				return createErrorResult("导入失败！"+ e.getMessage());
			}
			
			@SuppressWarnings("unchecked")
			List<List<Object>> objList = (List<List<Object>>) objMap.get("list");

			logger.info("------验证成功！------开始导入数据-----");			
			// 将数据封装到对象里
			for (int i = 0; i < objList.size(); i++) {
				List<Object> list = objList.get(i);				
				String majorName = list.get(0).toString();
				Major major = majorServices.getMajorByName(majorName, orgId);
				if (major != null) {			
					if (list.size()==1 ||  "".equals(list.get(1).toString().trim())) { //父级跳过
						continue;
					}else {
						String pName = list.get(1).toString();
						Major pMajor = majorServices.getMajorByName(pName, orgId);
						if (pMajor != null) {
							major.setPid(pMajor.getId());
							major.setPname(pName);
						}
					}				
				} else {
					major = new Major();
					if (list.size()==1 || "".equals(list.get(1).toString().trim())) { // 父级直接保存
						major.setName(majorName);
						major.setCreateDate(new Date());
						major.setState(1);
						major.setOrgId(orgId);
						majorServices.addMajor(major);
						continue;
					}else{
						String pName = list.get(1).toString();
						Major pMajor = majorServices.getMajorByName(pName, orgId);
						if (pMajor != null) {
							major.setPid(pMajor.getId());
							major.setPname(pName);
						}
					}
				}
				major.setName(majorName);
				//major.setRemark(remark);
				major.setCreateDate(new Date());
				major.setState(1);
				major.setOrgId(orgId);
				majorServices.addMajor(major);
			}
			in.close();
			logger.info("------成功导入！---------");
		} catch (Exception e) {
			return createErrorResult("导入失败！"+ e.getMessage());
		}
		return createSuccessResult(null);
	}
}
