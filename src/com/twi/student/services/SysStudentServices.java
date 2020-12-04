package com.twi.student.services;

import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.student.domain.SysStudent;

public interface SysStudentServices {

	/**
	 * 学生档案列表
	 * @param page
	 * @param student
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Page<SysStudent> getStudentPage(Page<SysStudent> page, SysStudent student, String startDate, String endDate);
	
	/**
	 * 导入添加更新学生档案
	 * @param studentList
	 * @param orgId
	 * @return
	 */
	boolean saveOrUpdateStudent(List<SysStudent> studentList, String orgId);
	
	/**
	 * 更新学生档案
	 * @param student
	 * @return
	 */
	boolean updateStudent(SysStudent student);
	
	/**
	 * 获取学生档案信息
	 * @param id
	 * @return
	 */
	SysStudent getStudentById(String id);
	
	/**
	 * 更新学生档案状态
	 * @param ids
	 * @param state
	 * @return
	 */
	boolean updateState(String ids, String state);
	
	/**
	 * 批量删除学生档案
	 * @param orgId
	 * @return
	 */
	boolean batchDelete(String orgId);
	
	/**
	 * 学生列表
	 * @param student
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Map<String, Object>> getStudentList(SysStudent student, String startDate, String endDate);
	
	/**
	 * 获取学生信息
	 * @param orgId
	 * @param studentName
	 * @param studentCode
	 * @return
	 */
	SysStudent getStudentByNameAndCode(String orgId, String studentName, String studentCode, String state);
}
