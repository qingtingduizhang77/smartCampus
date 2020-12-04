package com.twi.student.domain;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.twi.base.domain.IdEntity;

/**
 * 学生绑定
 * @author zhengjc
 *
 */
@Entity
@Table(name = "sys_wx_student")
public class SysWxStudent extends IdEntity implements Serializable{

	private static final long serialVersionUID = -7798492696547494779L;

	@Column(name = "openid_")
	private String openId;
	
	@Column(name = "Student_code")
	private String studentCode;//学号
	
	@Column(name = "Student_id")
	private String studentId;//学生档案id
	
	@Column(name = "Student_name")
	private String studentName;//学生姓名
	
	@Column(name = "creater_date")
	private Date createDate;
	
	@Column(name = "last_login_time")
	private Date lastLoginTime;

	private String className;//班级
	private String majorName;//专业
	
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@Transient
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Transient
	public String getMajorName() {
		return majorName;
	}

	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}
	
	
}
