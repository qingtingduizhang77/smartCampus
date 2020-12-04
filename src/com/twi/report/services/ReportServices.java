package com.twi.report.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.freecart.domain.FreeCart;

public interface ReportServices {
	
	
	
	/**
	 * 根据付费类型id_取帐套
	 * @param payTypeId 付费类型id_
	 * @param orgId  '学校ID'
	 * @return
	 */
	List<Map<String, Object>>  getFreeCartByPayTypeId(String payTypeId,String orgId);
	
	/**
	 * 项目分頁报表
	 * @param page
	 * @param payTypeId 缴费类型ID
	 * @param studentCode 学号
	 * @param studentName 学生姓名
	 * @param majorId  专业Id
	 * @param clazzName 所属班级
	 * @param freeCartId 缴费账套Id
	 * @param mobileMumber 手机号码
	 * @param startDate 开始时间
	 * @param endDate  结束时间
	 * @param orgId  '学校ID'
	 * @return
	 */
	Page<Map<String,Object>> getpRojectReport(Page<Map<String,Object>> page,String payTypeId,String studentCode,String studentName,String majorId,String majorName,String clazzName,String freeCartId,String mobileMumber,Date startDate,Date endDate,String orgId);
    
   
	/**
	 * 项目报表列表
	 * @param page
	 * @param payTypeId 缴费类型ID
	 * @param studentCode 学号
	 * @param studentName 学生姓名
	 * @param majorId  专业Id
	 * @param clazzName 所属班级
	 * @param freeCartId 缴费账套Id
	 * @param mobileMumber 手机号码
	 * @param startDate 开始时间
	 * @param endDate  结束时间
	 *  @param orgId  '学校ID'
	 * @return
	 */
	List<Map<String,Object>> getpRojectReport(String payTypeId,String studentCode,String studentName,String majorId,String majorName,String clazzName,String freeCartId,String mobileMumber,Date startDate,Date endDate,String orgId);
    
   

}
