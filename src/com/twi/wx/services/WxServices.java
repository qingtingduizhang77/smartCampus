package com.twi.wx.services;

import java.util.List;

import com.twi.student.domain.SysWxStudent;

public interface WxServices {

	public String getWxOauthJson(String wxAppid, String wxAppSecret, String wxCode);
	
	SysWxStudent getWxStudentInfo(String studentId, String studentName, String studentCode);
	
	boolean addWxStudent(SysWxStudent wxStudent);
	
	SysWxStudent getWxStudentById(String id);
	
	boolean delWxStudent(SysWxStudent wxStudent);
	
	List<SysWxStudent> getWxStudentList(String openId);
	List<SysWxStudent> getWxStudentList(String orgId,String openId);
}
