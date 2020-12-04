package com.twi.pc.web;

import java.io.File;
import java.util.HashMap;
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
import com.twi.base.util.PropertiesMsg;
import com.twi.base.util.PropertiesUtils;
import com.twi.base.util.StringUtils;
import com.twi.images.domain.Images;
import com.twi.images.services.ImagesServices;
import com.twi.security.domain.SSCUserDetails;

@Controller
@RequestMapping("/admin_back/images")
public class ImagesController extends BaseController {

	@Resource(name = "imagesServices")
	private ImagesServices imagesServices;

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
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<Images> page = new Page<Images>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();

		pMap.put("orgId", getOrgId());
		page = imagesServices.getPageList(page, pMap);

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
		String[] idArr = ids.split(",");
		if (!imagesServices.batchDelete(idArr)) {
			return createErrorResult("出错了，请检查参数是否正确！");
		}
		return createSuccessResult(null);
	}

	@ResponseBody
	@RequestMapping(value = "save", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> save(HttpServletRequest request,
			@RequestParam(value = "file", required = false) MultipartFile multipartFile, Images image) {
		try {
			String orgId = getOrgId();
			if(StringUtils.isBlank(orgId)) { //学校不存在！
				return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
			}
			if (null != multipartFile) {
				// 获取文件名
				String fileName = multipartFile.getOriginalFilename();
				// 获取文件后缀
				String prefix = fileName.substring(fileName.lastIndexOf("."));
				if (!prefix.equals(".png") && !prefix.equals(".jpg") && !prefix.equals(".bmp")) {
					return createErrorResult("上传文件格式错误！png/jpg/bmp");
				}
				logger.info("------已接收到上传图片-----");

				// 保存文件
				String orgPath = StringUtils.trimToEmpty(orgId).replaceAll("-", "");
				String filePath = "/"+ CommonConstants.ATTACHMENT_PATH +"/"+ orgPath + "/images/banner";
				String path = PropertiesUtils.getProperty("files") + filePath;
				File file = new File(path, fileName);
				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);

				String url = filePath + "/" + fileName;
				image.setImgUrl(url);
			}
			image.setOrgId(orgId);
			if (StringUtils.strIsNotNull(image.getId())) {
				Images images = imagesServices.getEntityById(image.getId());
				if (images !=null) {
					if (StringUtils.strIsNotNull(image.getImgUrl())) {
						images.setImgUrl(image.getImgUrl());
					}
					if (!images.getTitle().equals(image.getTitle())) {
						images.setTitle(image.getTitle());
					}
					images.setOrderNumber(image.getOrderNumber());
					if (!imagesServices.update(images)) {
						return createErrorResult("更新失败！");
					}
				}else {
					return createErrorResult("修改图片不存在！");
				}				
			} else {
				if (null != multipartFile) {
					if (!imagesServices.add(image)) {
						return createErrorResult("保存失败！");
					}
				} else {
					return createErrorResult("新增图片不能为空！");
				}
			}
		} catch (Exception e) {
			return createErrorResult("服务器异常！");
		}
		return createSuccessResult(null);
	}

	@ResponseBody
	@RequestMapping(value = "query", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> query(HttpServletRequest request, @RequestParam(value = "id") String id) {

		Images image = imagesServices.getEntityById(id);
		if (image == null) {
			return createErrorResult("图片不存在！");
		}
		image.setOrgId(getOrgId());
		return createSuccessResult(image);
	}
}
