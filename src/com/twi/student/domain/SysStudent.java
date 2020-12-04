package com.twi.student.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

/**
 * 学生
 * @author zhengjc
 *
 */
@Entity
@Table(name = "sys_student_info")
public class SysStudent extends IdEntity implements Serializable{

	private static final long serialVersionUID = 8924732203725951882L;

	@Column(name = "name_")
	private String name;//姓名
	
	@Column(name = "code_")
	private String code;//学号
	
	@Column(name = "id_card")
	private String idCard;//身份证
	
	@Column(name = "sex_")
	private String sex;//性别
	
	@Column(name = "entrance_")
	private Date entrance;//入学日期
	
	@Column(name = "major_id")
	private String majorId;//专业Id
	
	@Column(name = "major_name")
	private String majorName;//专业名称
	
	@Column(name = "clazz_name")
	private String className;//班级
	
	@Column(name = "grade_name")
	private String gradeName;//年级
	
	@Column(name = "Nation_")
	private String nation;//民族
	
	@Column(name = "Native_place")
	private String nativePlace;//籍贯
	
	@Column(name = "Political_")
	private String political;//政治面貌
	
	@Column(name = "address_")
	private String address;//地址
	
	@Column(name = "email_")
	private String email;//邮箱
	
	@Column(name = "Mobile_number")
	private String mobileNumber;//手机号
	
	@Column(name = "state_")
	private String state;//状态
	
	@Column(name = "remark_")
	private String remark;//备注
	
	@Column(name = "creater_date")
	private Date createDate;//创建时间
	
	@Column(name = "org_id")
	private String orgId;//学校id

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	public Date getEntrance() {
		return entrance;
	}

	public void setEntrance(Date entrance) {
		this.entrance = entrance;
	}

	public String getMajorId() {
		return majorId;
	}

	public void setMajorId(String majorId) {
		this.majorId = majorId;
	}

	public String getMajorName() {
		return majorName;
	}

	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getNativePlace() {
		return nativePlace;
	}

	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}

	public String getPolitical() {
		return political;
	}

	public void setPolitical(String political) {
		this.political = political;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	
}
