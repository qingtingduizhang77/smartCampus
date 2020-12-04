package com.twi.pc.web;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import com.twi.security.domain.SSCUserDetails;
import com.twi.student.domain.SysStudent;
import com.twi.student.services.SysStudentServices;

/**
 * 学生信息
 * 
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/admin_back/student")
public class SysStudentController extends BaseController {

	@Autowired
	private SysStudentServices sysStudentServicesImp;

	/**
	 * 学生列表
	 * 
	 * @param request
	 * @param name
	 *            姓名
	 * @param majorId
	 *            专业名称
	 * @param className
	 *            班级名称
	 * @param code
	 *            学号
	 * @param nation
	 *            民族
	 * @return
	 */
	@RequestMapping(value = "/list")
	public Map<String, Object> sysStudentList(HttpServletRequest request,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "majorName", defaultValue = "") String majorName,
			@RequestParam(value = "gradeName", defaultValue = "") String gradeName,
			@RequestParam(value = "className", defaultValue = "") String className,
			@RequestParam(value = "code", defaultValue = "") String code,
			@RequestParam(value = "nation", defaultValue = "") String nation,
			@RequestParam(value = "startDate", defaultValue = "") String startDate,
			@RequestParam(value = "endDate", defaultValue = "") String endDate,
			@RequestParam(value = "state", defaultValue = "1") String state) {
		logger.info("------start------sysStudentList");
		Map<String, Object> result = new HashMap<String, Object>();
		// 分页参数：
		int pageNo = getInt(request, "pageNo", 1); // 当前面
		int pageSize = getInt(request, "pageSize", 10); // 每页显示条数数
		boolean autoCount = getBoolean(request, "autoCount", true); // 是否显示总条数

		Page<SysStudent> page = new Page<SysStudent>(pageSize);
		page.pageNo(pageNo).autoCount(autoCount);

		SysStudent student = new SysStudent();
		student.setName(name);
		student.setMajorName(majorName);
		student.setGradeName(gradeName);
		student.setClassName(className);
		student.setCode(code);
		student.setNation(nation);
		student.setState(state);
		SSCUserDetails user = WebHelper.getUser();
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			student.setOrgId(user.getOrgId());
		}

		page = sysStudentServicesImp.getStudentPage(page, student, startDate, endDate);

		result.put("pageSize", page.getPageSize());
		result.put("pageNo", page.getPageNo());
		result.put("rows", page.getResult());
		result.put("count", page.getTotalCount());
		logger.info("------end------sysStudentList");
		return createSuccessResult(result);
	}

	/**
	 * 学生档案读取
	 * 
	 * @param file
	 * @param isCover
	 * @return
	 */
	@RequestMapping(value = "/readExcel")
	public Map<String, Object> readExcel(@RequestParam(value = "file") MultipartFile file) {
		logger.info("------start------readExcel");
		SSCUserDetails user = WebHelper.getUser();
		if (user != null && StringUtils.isBlank(user.getOrgId())) { //学校不存在！
			return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
		}
		String orgId = user.getOrgId();
		if (file == null) {
			return createErrorResult("请重新上传文件！");
		}
		boolean flag = true;
		// 读取Excel数据到List中
		String orgPath = StringUtils.trimToEmpty(orgId).replaceAll("-", "");
		String filePath = "/"+ CommonConstants.ATTACHMENT_PATH +"/"+ orgPath + "/excel/student";
		String targetDirectory = PropertiesUtils.getProperty("files") + filePath;
		String rename = DateUtils.getAsString(new Date(), DateUtils.FORMAT_DATE_TIME2) + "_"
				+ file.getOriginalFilename();
		// 生成上传的文件对象
		File target = new File(targetDirectory, rename);
		try {
			// 复制file对象，实现上传
			FileUtils.copyInputStreamToFile(file.getInputStream(), target);
			// 读取内容
			InputStream in = file.getInputStream();
			Map<String, Object> objMap = new HashMap<String, Object>();
			try {
				objMap = new ImportExcelUtil().getMapByExcel(in, file.getOriginalFilename(), false);
			} catch (Exception e) {
				return createErrorResult("导入失败！" + e.getMessage());
			}

			@SuppressWarnings("unchecked")
			List<ArrayList<Object>> objList = (List<ArrayList<Object>>) objMap.get("list");
			// List<ArrayList<String>> alist = new ExcelRead().readExcel(file);
			// List<Object> titleList = objList.get(0);
			List<SysStudent> studentList = new ArrayList<SysStudent>();
			SysStudent student = null;
			if (objList != null && objList.size() > 1) {
				for (int i = 1; i < objList.size(); i++) {
					List<Object> list = objList.get(i);					
					if (list.size() == 0 || !StringUtils.strIsNotNull(list.get(0).toString())) {
						break;
					}
					student = new SysStudent();
					student.setCode(list.get(0) == null ? "" : list.get(0).toString());// 学号// //每一行的第一个单元格
					student.setName(list.get(1) == null ? "" : list.get(1).toString());// 姓名
					student.setIdCard(list.get(2) == null ? "" : list.get(2).toString());// 身份证
					student.setSex(list.get(3) == null ? "" : list.get(3).toString());// 性别
					student.setEntrance(CalendarUtils.simpleparse(list.get(4) == null ? "" : list.get(4).toString()));
					student.setMajorName(list.get(5) == null ? "" : list.get(5).toString());// 专业
					student.setGradeName(list.get(6) == null ? "" : list.get(6).toString());// 年级
					student.setClassName(list.get(7) == null ? "" : list.get(7).toString());// 班级
					student.setNation(list.get(8) == null ? "" : list.get(8).toString());// 民族
					student.setPolitical(list.get(9) == null ? "" : list.get(9).toString());// 政治面貌
					student.setNativePlace(list.get(10) == null ? "" : list.get(10).toString());// 籍贯
					student.setAddress(list.get(11) == null ? "" : list.get(11).toString());// 家庭地址
					studentList.add(student);

				}
				flag = sysStudentServicesImp.saveOrUpdateStudent(studentList, orgId);
			} else {
				flag = false;
			}

		} catch (Exception e) {
			flag = false;
			return createErrorResult("导入失败！" + e.getMessage());
		} finally {
			// 如果文件已经存在，则删除原有文件
			// if(target.exists()){
			// target.delete();
			// }
		}
		if (!flag) {
			return createErrorResult("导入失败！");
		}
		logger.info("------end------readExcel");
		return createSuccessResult(null, "导入成功！");

	}

	/**
	 * 更新学生档案信息
	 * 
	 * @param student
	 * @return
	 */
	@RequestMapping(value = "/update")
	public Map<String, Object> saveStudentInfo(SysStudent student,
			@RequestParam(value = "entrance_", defaultValue = "", required = false) String entrance) {
		logger.info("------start------ : saveStudentInfo");
		Map<String, String> result = new HashMap<String, String>();
		SSCUserDetails user = WebHelper.getUser();
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			student.setOrgId(user.getOrgId());
		}
		if (StringUtils.isNotEmpty(entrance)) {
			student.setEntrance(CalendarUtils.simpleparse(entrance));
		}
		if (StringUtils.isEmpty(student.getId())) {
			SysStudent student1 = sysStudentServicesImp.getStudentByNameAndCode(student.getOrgId(), null,
					student.getCode(), null);
			if (student1 != null && "2".equals(student1.getState())) {
				return createErrorResult("该学生学号已被删除，请您先恢复");
			}
			if (student1 != null) {
				return createErrorResult("该学生已存在，请重新修改学号");
			}
		}
		boolean flag = sysStudentServicesImp.updateStudent(student);
		if (!flag) {
			return createErrorResult("数据异常");
		}
		logger.info("------end------ : saveStudentInfo");
		return createSuccessResult(result, "");
	}

	/**
	 * 学生档案详细
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/info")
	public Map<String, Object> getStudentInfo(@RequestParam(value = "id", required = true) String id) {
		SysStudent student = sysStudentServicesImp.getStudentById(id);
		return createSuccessResult(student);
	}

	/**
	 * 更新学生档案状态
	 * 
	 * @param ids
	 * @param state
	 * @return
	 */
	@RequestMapping(value = "/update/state")
	public Map<String, Object> updateState(@RequestParam(value = "ids", defaultValue = "") String ids,
			@RequestParam(value = "state", defaultValue = "") String state) {
		logger.info("------start------:updateState");
		if (StringUtils.isEmpty(ids)) {
			return createErrorResult("参数异常");
		}
		boolean flag = true;
		try {
			if (StringUtils.isEmpty(state)) {
				state = "2";// 删除即禁用
			}
			flag = sysStudentServicesImp.updateState(ids, state);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		if (!flag) {
			return createErrorResult("操作失败");
		}
		logger.info("------end------:updateState");
		return createSuccessResult(null, "操作成功");
	}

	/**
	 * 批量删除学生档案
	 * 
	 * @return
	 */
	@RequestMapping(value = "/batch/delete")
	public Map<String, Object> batchDeleteStudent() {
		logger.info("------start------:batchDeleteStudent");
		SSCUserDetails user = WebHelper.getUser();
		boolean flag = true;
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			flag = sysStudentServicesImp.batchDelete(user.getOrgId());
		} else {
			return createErrorResult("请重新登录");
		}
		if (!flag) {
			return createErrorResult("操作失败");
		}
		logger.info("------end------:batchDeleteStudent");
		return createSuccessResult(null, "操作成功");
	}

	/**
	 * 学生档案数据导出
	 * 
	 * @param request
	 * @param response
	 * @param name
	 *            学生姓名
	 * @param majorName
	 *            专业名称
	 * @param className
	 *            班级名称
	 * @param code
	 *            学号
	 * @param nation
	 *            民族
	 * @param startDate
	 * @param endDate
	 */
	@RequestMapping(value = "/exportExcel")
	public void exportExcel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "majorName", defaultValue = "") String majorName,
			@RequestParam(value = "gradeName", defaultValue = "") String gradeName,
			@RequestParam(value = "className", defaultValue = "") String className,
			@RequestParam(value = "code", defaultValue = "") String code,
			@RequestParam(value = "nation", defaultValue = "") String nation,
			@RequestParam(value = "startDate", defaultValue = "") String startDate,
			@RequestParam(value = "endDate", defaultValue = "") String endDate,
			@RequestParam(value = "state", defaultValue = "1") String state) {
		logger.info("-------start--------exportExcel");
		SysStudent student = new SysStudent();
		student.setName(name);
		student.setMajorName(majorName);
		student.setGradeName(gradeName);
		student.setClassName(className);
		student.setCode(code);
		student.setNation(nation);
		student.setState(state);
		SSCUserDetails user = WebHelper.getUser();
		if (user != null && StringUtils.isNotEmpty(user.getOrgId())) {
			student.setOrgId(user.getOrgId());
		}

		List<Map<String, Object>> rowlist = sysStudentServicesImp.getStudentList(student, startDate, endDate);
		String[] titles = { "学号", "姓名", "身份证号", "性别", "入学日期", "专业", "年级", "班级", "民族", "政治面貌", "籍贯", "家庭地址" };
		String[] columnKey = { "code_", "name_", "id_card", "sex_", "entrance_", "major_name", "grade_name",
				"clazz_name", "Nation_", "Political_", "Native_place", "address_" };
		try {

			request.setCharacterEncoding("utf-8");
			String fileName = "student_info_" + CalendarUtils.format(new Date(), "yyyyMMddHHmmss") + ".xls";
			fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
			HSSFWorkbook workbook = ExcelUtils.eqEcel(rowlist, titles, "学生档案", columnKey);
			response.reset();
			response.setContentType("contentType=application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName);
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Connection", "close");
			workbook.write(response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------end--------exportExcel");
	}
}
