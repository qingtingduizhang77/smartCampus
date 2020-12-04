package com.twi.pc.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.base.util.CommonConstants;
import com.twi.base.util.DateUtils;
import com.twi.base.util.ExcelUtils;
import com.twi.base.util.ImportExcelUtil;
import com.twi.base.util.PropertiesMsg;
import com.twi.base.util.PropertiesUtils;
import com.twi.base.util.StringUtils;
import com.twi.freecart.domain.FreeCart;
import com.twi.freecart.services.FreeCartServices;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.freecartdetailed.services.FreeCartDetailedServices;
import com.twi.freecartdetailedlog.domain.DetailedLog;
import com.twi.freecartdetailedlog.services.DetailedLogServices;
import com.twi.paytype.domain.SysPayType;
import com.twi.security.domain.SSCUserDetails;
import com.twi.student.domain.SysStudent;

import net.sf.json.JSONObject;

//账套
@Controller
@RequestMapping("/admin_back/freeCart")
public class FreeCartController extends BaseController {

	@Resource(name = "freeCartServices")
	private FreeCartServices freeCartServices;

	@Resource(name = "detailedLogServices")
	private DetailedLogServices detailedLogServices;

	@Resource(name = "freeCartDetailedServices")
	private FreeCartDetailedServices freeCartDetailedServices;

	private String getOrgId() {
		SSCUserDetails user = WebHelper.getUser();
		return user.getOrgId();
	}

	private String getUserId() {
		SSCUserDetails user = WebHelper.getUser();
		return user.getUserId();
	}

	@ResponseBody
	@RequestMapping(value = "query", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> query(HttpServletRequest request, @RequestParam(value = "id") String id) {

		FreeCart freeCart = freeCartServices.getFreeCartById(id);
		if (freeCart == null) {
			return createErrorResult("账套不存在！");
		}
		freeCart.setOrgId(getOrgId());
		return createSuccessResult(freeCart);
	}

	@ResponseBody
	@RequestMapping(value = "getPageList", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> getPageList(HttpServletRequest request,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		boolean autoCount = getBoolean(request, "autoCount", true);
		Page<FreeCart> page = new Page<FreeCart>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(name)) {
			pMap.put("name", name);
		}
		if (StringUtils.strIsNotNull(year)) {
			pMap.put("year", year);
		}
		if (StringUtils.strIsNotNull(state)) {
			pMap.put("state", state);
		}
		pMap.put("orgId", getOrgId());
		page = freeCartServices.getFreeCartPage(page, pMap);

		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("pageNo", page.getPageNo());
		contentMap.put("pageSize", page.getPageSize());
		contentMap.put("rows", page.getResult());
		contentMap.put("count", page.getTotalCount());
		return createSuccessResult(contentMap);
	}

	@ResponseBody
	@RequestMapping(value = "save", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> save(HttpServletRequest request, FreeCart freeCart) {
		freeCart.setOrgId(getOrgId());
		if (StringUtils.strIsNotNull(freeCart.getId())) {
			FreeCart fc = freeCartServices.getFreeCartById(freeCart.getId());
			fc.setName(freeCart.getName());
			fc.setCode(freeCart.getCode());
			fc.setState(freeCart.getState());
			fc.setRemark(freeCart.getRemark());
			fc.setDescribe(freeCart.getDescribe());
			if (!freeCartServices.updFreeCart(fc)) {
				return createErrorResult("更新失败！");
			}
		} else {
			if (!freeCartServices.addFreeCart(freeCart)) {
				return createErrorResult("保存失败！");
			}
		}
		return createSuccessResult(null);
	}

	@ResponseBody
	@RequestMapping(value = "batchDelete", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> batchDelete(HttpServletRequest request, @RequestParam String[] list) {
		// String[] ids = idStr.split(",");
		if (!freeCartServices.batchDelete(list)) {
			return createErrorResult("删除失败！");
		}
		return createSuccessResult(null);
	}

	// 导入明细（列读取），无效方法
	@ResponseBody
	@RequestMapping(value = "importExcel2", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> importExcel2(HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile multipartFile, @RequestParam(value = "id") String id)
			throws Exception {

		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding("UTF-8");
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
			String path = PropertiesUtils.getProperty("files") + "/" + getOrgId() + "/freeDetailed";
			String rename = DateUtils.getAsString(new Date(), DateUtils.FORMAT_DATE_TIME2) + "_" + fileName;
			File file = new File(path, rename);
			FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
			// 读取内容
			InputStream in = multipartFile.getInputStream();
			Map<String, Object> objMap = new HashMap<String, Object>();
			try {
				objMap = new ImportExcelUtil().getMapByExcel(in, fileName, true);
			} catch (Exception e) {
				return createErrorResult("导入失败！" + e.getMessage());
			}

			@SuppressWarnings("unchecked")
			List<List<Object>> objList = (List<List<Object>>) objMap.get("list");
			// 获取表头
			List<Object> titleList = objList.get(0);
			// 验证类型
			for (int j = 2; j < titleList.size(); j++) {
				String typeName = titleList.get(j).toString().trim();
				if (!StringUtils.strIsNotNull(typeName)) {
					continue;
				}
				// 缴费类型
				SysPayType payType = freeCartServices.getPayTypeByName(typeName, getOrgId());
				if (payType == null) {
					return createErrorResult(typeName + "类型不存在，不可导入明细！");
				} else {
					if (payType.getState() == 2) {
						return createErrorResult(typeName + "类型已禁用，不可导入明细！");
					}
				}

				// 账套信息
				FreeCart freeCart = freeCartServices.getFreeCartById(id);
				if (freeCart == null) {
					return createErrorResult("出错了，账套不存在！");
				} else {
					if (freeCart.getState() == 1) {
						return createErrorResult("缴费账套已禁用，不可导入明细！");
					}
					String payTypeName = freeCart.getPayTypeName();
					String payTypeId = freeCart.getPayTypeId();
					updatePayType(freeCart, payTypeId, payTypeName, payType);
				}

				logger.info("------验证成功！------开始导入数据-----");
				List<FreeCartDetailed> detailedList = new ArrayList<FreeCartDetailed>();
				// 将数据封装到对象里
				for (int i = 1; i < objList.size(); i++) {
					List<Object> list = objList.get(i);
					if (list.size() == 0 || !StringUtils.strIsNotNull(list.get(0).toString())) {
						break;
					}
					String code = list.get(0).toString().trim();
					if (list.get(j) == "") {
						continue;
					}
					String moneyStr = list.get(j).toString().trim();
					if (!StringUtils.strIsNotNull(moneyStr) || StringUtils.equals(moneyStr, "0")) {
						continue;
					}
					// 验证金额
					try {
						Double.parseDouble(moneyStr);
						if (moneyStr.contains("-") || getDigits(moneyStr) > 2) {
							return createErrorResult(list.get(1).toString() + typeName + "金额，必须正数且最多保留2位小数！");
						}
					} catch (NumberFormatException e) {
						return createErrorResult(list.get(1).toString() + typeName + "金额，无法转换为浮点型！");
					}

					String stuName = list.get(1).toString().trim();
					if ("".equals(stuName)) {
						return createErrorResult("第" + (i + 2) + "行," + "学生姓名不能为空！");
					}
					// 先查询明细是否存在
					FreeCartDetailed freeCartDetailed = freeCartServices.queryBycondition(id, code, stuName, typeName,
							getOrgId());
					if (freeCartDetailed == null) {
						freeCartDetailed = new FreeCartDetailed();
						freeCartDetailed.setFreeCartId(freeCart.getId()); // 账套ID
						freeCartDetailed.setFreeCartName(freeCart.getName()); // 账套名称
						freeCartDetailed.setLessMoney(Double.valueOf(moneyStr));// 欠费
					}

					SysStudent student = freeCartServices.getStudentByCode(code, getOrgId());
					if (student == null) {
						student = new SysStudent();
						student.setCode(code);
						student.setName(stuName);
						student.setOrgId(getOrgId());
						freeCartServices.addSysStudent(student);
					} else {
						// 校验学生姓名
						if (!StringUtils.equals(student.getName(), stuName)) {
							return createErrorResult("导入学号：" + code + " 与档案对应的名称不一致！");
						}
						// 校验学生状态，如已禁用 改为启用
						if (!StringUtils.equals(student.getState(), "1")) {
							student.setState("1");
							freeCartServices.updSysStudent(student);
						}
					}

					freeCartDetailed.setStudentCode(code); // 学号
					freeCartDetailed.setStudentId(student.getId());// 学生id
					freeCartDetailed.setStudentName(stuName); // 姓名
					freeCartDetailed.setGradeName(student.getGradeName()); // 年级
					freeCartDetailed.setClazzName(student.getClassName()); // 班级
					Double money = Double.valueOf(moneyStr);
					freeCartDetailed.setMoney(money); // 应缴费用
					double lessMoney = money - freeCartDetailed.getPayMoney();
					lessMoney = (double) Math.round(lessMoney * 100) / 100;
					if (lessMoney == 0 || lessMoney < 0) {
						freeCartDetailed.setState(4); // 已缴费
					} else if (money != lessMoney) {
						freeCartDetailed.setState(3); // 缴费中
					}
					freeCartDetailed.setLessMoney(lessMoney); // 欠费
					freeCartDetailed.setFreeCartItemId(null);
					freeCartDetailed.setMajorId(student.getMajorId());
					freeCartDetailed.setMajorName(student.getMajorName()); // 专业
					freeCartDetailed.setPayTypeId(payType.getId()); // 类型编号
					freeCartDetailed.setPayTypeName(typeName); // 类型名称
					freeCartDetailed.setOrgId(getOrgId());
					// 添加到内存列表中
					detailedList.add(freeCartDetailed);

				}

				in.close();
				if (detailedList.size() > 0) {
					for (FreeCartDetailed fDetailed : detailedList) {
						if (StringUtils.strIsNotNull(fDetailed.getId())) {
							if (!freeCartDetailedServices.update(fDetailed)) {
								return createErrorResult("导入更新异常！");
							}
						} else {
							if (!freeCartDetailedServices.add(fDetailed)) {
								return createErrorResult("导入新增异常！");
							}
						}
					}

					DetailedLog detailedLog = new DetailedLog();
					detailedLog.setUserId(getUserId());
					detailedLog.setFreeCartId(id);
					detailedLog.setOptType("导入");
					// detailedLog.setContext(JSONObject.fromObject(objList).toString());
					detailedLog.setOrgId(getOrgId());
					detailedLog.setCreateDate(new Date());
					detailedLog.setFileUrl(path + "/" + rename);
					detailedLogServices.add(detailedLog);
				}
				logger.info("------成功导入：" + detailedList.size() + "-----条----------");
			}
		} catch (Exception e) {
			return createErrorResult("导入失败！" + e.getMessage());
		}
		return createSuccessResult(null);
	}

	// 导入明细（行读取）
	@ResponseBody
	@RequestMapping(value = "importExcel", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> importExcel(HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile multipartFile, @RequestParam(value = "id") String id)
			throws Exception {
		String orgId = getOrgId();
		if (StringUtils.isBlank(orgId)) { // 学校不存在！
			return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
		}
		// 验证账套信息
		FreeCart freeCart = freeCartServices.getFreeCartById(id);
		if (freeCart == null) {
			return createErrorResult("出错了，账套不存在！");
		} else {
			if (freeCart.getState() == 1) {
				return createErrorResult("缴费账套已禁用，不可导入明细！");
			}
		}

		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding("UTF-8");
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
		logger.info("------已接收到上传文件-----开始读取excel-----");
		try {
			// 保存文件
			String orgPath = StringUtils.trimToEmpty(orgId).replaceAll("-", "");
			String filePath = "/" + CommonConstants.ATTACHMENT_PATH + "/" + orgPath + "/excel/freeDetailed";
			String path = PropertiesUtils.getProperty("files") + filePath;
			String rename = DateUtils.getAsString(new Date(), DateUtils.FORMAT_DATE_TIME2) + "_" + fileName;
			File file = new File(path, rename);
			FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
			// 读取内容
			InputStream in = multipartFile.getInputStream();
			Map<String, Object> objMap = new HashMap<String, Object>();
			try {
				objMap = new ImportExcelUtil().getMapByExcel(in, fileName, true);
			} catch (Exception e) {
				return createErrorResult("导入失败！" + e.getMessage());
			}

			@SuppressWarnings("unchecked")
			List<List<Object>> objList = (List<List<Object>>) objMap.get("list");
			// 获取表头
			List<Object> titleList = objList.get(0);
			if (!titleList.get(0).toString().contains("学号") || !titleList.get(1).toString().contains("姓名")
					|| !titleList.get(2).toString().contains("专业") || !titleList.get(3).toString().contains("班级")) {
				return createErrorResult("模板表头格式错误，请下载使用新模板！");
			}

			Map<String, Object> payTypeMap = new HashMap<>();
			logger.info("------读取excel成功------开始验证缴费类型-----");
			// 验证类型
			int titleSize = 0;
			for (Object object : titleList) {
				titleSize++;
				String typeName = object.toString();
				if (typeName.contains("学号") || typeName.contains("姓名") 
						|| typeName.contains("专业") || typeName.contains("班级")) {
					continue;
				} else if (typeName == "") {
					// return createErrorResult("标题不能为空字符串，请使用格式刷清除！");
					titleSize--;
					break;
				}
				// 缴费类型
				SysPayType payType = freeCartServices.getPayTypeByName(typeName, getOrgId());
				if (payType == null) {
					return createErrorResult(typeName + "类型不存在，不可导入明细！");
				} else {
					if (payType.getState() == 2) {
						return createErrorResult(typeName + "类型已禁用，不可导入明细！");
					} else {
						payTypeMap.put(typeName, payType);
						// 更新账套信息
						String payTypeName = freeCart.getPayTypeName();
						String payTypeId = freeCart.getPayTypeId();
						updatePayType(freeCart, payTypeId, payTypeName, payType);
					}
				}
			}
			logger.info("------验证成功！------开始导入数据-----");
			List<FreeCartDetailed> detailedList = new ArrayList<FreeCartDetailed>();
			// 将数据封装到对象里
			for (int i = 1; i < objList.size(); i++) {
				List<Object> list = objList.get(i);

				// 学号、姓名 不能为空
				String code = list.get(0).toString();
				String stuName = list.get(1).toString();

				if (list.size() == 0 || !StringUtils.strIsNotNull(code) || !StringUtils.strIsNotNull(stuName)) {
					return createErrorResult("第" + (i + 2) + "行," + "学号、姓名不能为空！");
				}
				
				String major = list.get(2).toString();
				String clazz = list.get(3).toString();

				for (int j = 4; j < titleSize; j++) {
					// 获取缴费类型
					String typeName = titleList.get(j).toString();
					SysPayType payType = (SysPayType) payTypeMap.get(typeName);
					if (payType == null) {
						return createErrorResult("导入出错了，请联系管理员！");
					}
					// 获取金额，为空 或者 0 跳过
					String moneyStr = list.get(j).toString();
					if (!StringUtils.strIsNotNull(moneyStr) || StringUtils.equals(moneyStr, "0")) {
						continue;
					}
					// 验证金额
					try {
						Double.parseDouble(moneyStr);
						if (moneyStr.contains("-") || getDigits(moneyStr) > 2) {
							return createErrorResult(list.get(1).toString() + typeName + "金额，必须正数且最多保留2位小数！");
						}
					} catch (NumberFormatException e) {
						return createErrorResult(list.get(1).toString() + typeName + "金额，无法转换为浮点型！");
					}

					// 先查询明细是否存在
					FreeCartDetailed freeCartDetailed = freeCartServices.queryBycondition(id, code, stuName, typeName,
							getOrgId());
					if (freeCartDetailed == null) {
						freeCartDetailed = new FreeCartDetailed();
						freeCartDetailed.setFreeCartId(freeCart.getId()); // 账套ID
						freeCartDetailed.setFreeCartName(freeCart.getName()); // 账套名称
						freeCartDetailed.setLessMoney(Double.valueOf(moneyStr));// 欠费
					}

					// 验证学生档案
					SysStudent student = freeCartServices.getStudentByCode(code, getOrgId());
					if (student == null) {
						student = new SysStudent();
						student.setCode(code);
						student.setName(stuName);
						student.setMajorName(major);
						student.setClassName(clazz);
						student.setOrgId(getOrgId());
						freeCartServices.addSysStudent(student);
					} else {
						// 校验学生姓名
						if (!StringUtils.equals(student.getName(), stuName)) {
							return createErrorResult("导入学号：" + code + " 与档案对应的名称不一致！");
						}
						boolean isUpdate=false;
						// 校验学生状态，如已禁用 改为启用
						if (!StringUtils.equals(student.getState(), "1")) {
							student.setState("1");
							isUpdate = true;
						}
						if (!StringUtils.isNotBlank(student.getMajorName())) {
							student.setMajorName(major);
							isUpdate = true;
						} 
						if (!StringUtils.isNotBlank(student.getClassName())) {
							student.setClassName(clazz);
							isUpdate = true;
						}
						if (isUpdate) {
							freeCartServices.updSysStudent(student);
						}
					}

					freeCartDetailed.setStudentCode(code); // 学号
					freeCartDetailed.setStudentId(student.getId());// 学生id
					freeCartDetailed.setStudentName(stuName); // 姓名
					freeCartDetailed.setGradeName(student.getGradeName()); // 年级
					freeCartDetailed.setClazzName(StringUtils.isNotBlank(clazz)?clazz:student.getClassName()); // 班级
					Double money = Double.valueOf(moneyStr);
					freeCartDetailed.setMoney(money); // 应缴费用
					double lessMoney = money - freeCartDetailed.getPayMoney();
					lessMoney = (double) Math.round(lessMoney * 100) / 100;
					if (lessMoney == 0 || lessMoney < 0) {
						freeCartDetailed.setState(4); // 已缴费
					} else if (money != lessMoney) {
						freeCartDetailed.setState(3); // 缴费中
					}
					freeCartDetailed.setLessMoney(lessMoney); // 欠费
					freeCartDetailed.setFreeCartItemId(null);
					freeCartDetailed.setMajorId(student.getMajorId());
					freeCartDetailed.setMajorName(StringUtils.isNotBlank(major)?major:student.getMajorName()); // 专业
					freeCartDetailed.setPayTypeId(payType.getId()); // 类型编号
					freeCartDetailed.setPayTypeName(typeName); // 类型名称
					freeCartDetailed.setOrgId(getOrgId());
					// 添加到内存列表中
					detailedList.add(freeCartDetailed);
				}
			}
			in.close();
			if (detailedList.size() > 0) {
				for (FreeCartDetailed fDetailed : detailedList) {
					if (StringUtils.strIsNotNull(fDetailed.getId())) {
						if (!freeCartDetailedServices.update(fDetailed)) {
							return createErrorResult("导入更新异常！");
						}
					} else {
						if (!freeCartDetailedServices.add(fDetailed)) {
							return createErrorResult("导入新增异常！");
						}
					}
				}
				DetailedLog detailedLog = new DetailedLog();
				detailedLog.setUserId(getUserId());
				detailedLog.setFreeCartId(id);
				detailedLog.setOptType("导入");
				detailedLog.setOrgId(getOrgId());
				detailedLog.setCreateDate(new Date());
				detailedLog.setFileUrl(filePath + "/" + rename);
				detailedLogServices.add(detailedLog);
			}
			logger.info("------成功导入：" + (objList.size() - 1) + "人，共 " + detailedList.size() + "条数据----------");

		} catch (Exception e) {
			return createErrorResult("导入失败！" + e.getMessage());
		}
		return createSuccessResult(null);
	}

	public static int getDigits(String number) {
		if (number.contains(".")) {
			String[] numStr = number.split("\\.");
			return numStr[1].length();
		}
		return 0;
	}

	// 更新账套类型ID、名称
	private void updatePayType(FreeCart freeCart, String payTypeId, String payTypeName, SysPayType payType) {
		String[] ptName = null;
		String[] ptId = null;
		String typeName = payType.getName();
		String typeId = payType.getId();

		if (StringUtils.strIsNotNull(payTypeName)) {
			if (payTypeName.contains(",")) {
				ptName = freeCart.getPayTypeName().split(",");
				if (!Arrays.asList(ptName).contains(typeName)) {
					payTypeName = typeName + "," + payTypeName;
					freeCart.setPayTypeName(payTypeName);
					freeCartServices.updFreeCart(freeCart);
				}
			} else {
				if (!typeName.equals(freeCart.getPayTypeName())) {
					payTypeName = typeName + "," + payTypeName;
				}
			}
		} else {
			payTypeName = typeName;
		}

		if (StringUtils.strIsNotNull(payTypeId)) {
			if (payTypeId.contains(",")) {
				ptId = freeCart.getPayTypeId().split(",");
				if (!Arrays.asList(ptId).contains(typeId)) {
					payTypeId = typeId + "," + payTypeId;
					freeCart.setPayTypeId(payTypeId);
					freeCartServices.updFreeCart(freeCart);
				}
			} else {
				if (!typeId.equals(freeCart.getPayTypeId())) {
					payTypeId = typeId + "," + payTypeId;
				}
			}
		} else {
			payTypeId = typeId;
		}
		freeCart.setPayTypeId(payTypeId);
		freeCart.setPayTypeName(payTypeName);
		freeCartServices.updFreeCart(freeCart);
	}

	@ResponseBody
	@RequestMapping(value = "clear", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> clear(HttpServletRequest request, @RequestParam(value = "id") String id) {
		Map<String, Object> pMap = new HashMap<String, Object>();

		// 获取账套未缴费的详细列表
		if (StringUtils.strIsNotNull(id)) {
			pMap.put("freeCartId", id);
		}
		pMap.put("deleteState", 2);
		pMap.put("orgId", getOrgId());

		List<Map<String, Object>> rowlist = freeCartDetailedServices.getConditionList(pMap);
		// 记录到日志
		for (Map<String, Object> detailed : rowlist) {
			DetailedLog detailedLog = new DetailedLog();
			detailedLog.setUserId(getUserId());
			detailedLog.setFreeCartId(id);
			detailedLog.setOptType("清空");
			detailedLog.setContext(JSONObject.fromObject(detailed).toString());
			detailedLog.setOrgId(getOrgId());
			detailedLog.setCreateDate(new Date());
			detailedLog.setFileUrl("");
			detailedLogServices.add(detailedLog);
			// 移除详细列表
			freeCartDetailedServices.deleteById(detailed.get("ID_").toString());
		}
		// 更新账套类型
		List<Map<String, Object>> payTypeList = freeCartServices.getPayTypeStr(id, getOrgId());
		for (Map<String, Object> map : payTypeList) {
			String ptName = null;
			if (map.get("payTypeName") != null) {
				ptName = map.get("payTypeName").toString();
			}
			String ptId = null;
			if (map.get("payTypeId") != null) {
				ptId = map.get("payTypeId").toString();
			}
			FreeCart freeCart = freeCartServices.getFreeCartById(id);
			freeCart.setPayTypeId(ptId);
			freeCart.setPayTypeName(ptName);
			freeCartServices.updFreeCart(freeCart);
		}

		return createSuccessResult(null);
	}

	// 导出账套缴费明细
	@RequestMapping(value = "/exportExcel")
	public void exportExcel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "id", required = false) String freeCartId) {

		logger.info("-------start--------exportExcel");
		FreeCart freeCart = freeCartServices.getFreeCartById(freeCartId);
		if (freeCart == null) {
			return;
		}
		String[] typeNameArr = freeCart.getPayTypeName().trim().split(",");
		String[] columnKey = new String[typeNameArr.length * 3 + 5];
		columnKey[0] = "学号";
		columnKey[1] = "姓名";
		columnKey[2] = "年级";
		columnKey[3] = "专业";
		columnKey[4] = "班级";
		int j = 5;
		for (int i = 0; i < typeNameArr.length; i++) {
			columnKey[j++] = typeNameArr[i].trim() + "_应缴";
			columnKey[j++] = typeNameArr[i].trim() + "_实缴";
			columnKey[j++] = typeNameArr[i].trim() + "_欠缴";
		}

		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.strIsNotNull(freeCartId)) {
			pMap.put("freeCartId", freeCartId);
		}
		pMap.put("orgId", getOrgId());

		List<Map<String, Object>> rowlist = freeCartServices.getFreeCartDetailedList(pMap, typeNameArr);
		String sheetName = "账套_缴费明细";

		try {
			HSSFWorkbook workbook = ExcelUtils.eqEcel(rowlist, columnKey, sheetName, columnKey);
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			// 设置输出的格式
			response.setContentType("application/x-excel");
			String fileName = "FreeCart_" + CalendarUtils.format(new Date(), "yyyy-MM-dd") + ".xls";
			fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------end--------exportExcel");
	}
}
