package com.twi.freecartdetailed.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "free_cart_detailed")
public class FreeCartDetailed extends IdEntity implements Serializable{

	private static final long serialVersionUID = 3292575099749756437L;
	
	@Column(name="free_Cart_id")	//账套ID
	private String freeCartId;
	
	@Column(name="free_Cart_name")	//账套名称
	private String freeCartName;

	@Column(name="free_Cart_item_id")	//缴费账套项ID
	private String freeCartItemId;
	
	@Column(name="Student_code")	//学生学号
	private String studentCode;
	
	@Column(name="Student_id")	//学生ID
	private String studentId;
	
	@Column(name="Student_name")	//学生姓名
	private String studentName;
	
	@Column(name="pwd_")	//密码
	private String pwd;
	
	@Column(name="major_id")	//专业ID
	private String majorId;
	
	@Column(name="major_name")	//专业名称
	private String majorName;
	
	@Column(name="grade_name")	//年级
	private String gradeName;
	
	@Column(name="clazz_name")	//班级
	private String clazzName;
	
	@Column(name="pay_type_id")	//缴费类型id
	private String payTypeId;
	
	@Column(name="pay_type_name")	//缴费类型名称
	private String payTypeName;
	
	@Column(name="money_")	//应缴费用
	private double money;
	
	@Column(name="pay_money")	//已交费用
	private double payMoney;
	
	@Column(name="less_money")	//欠缴费用
	private double lessMoney;
	
	
	@Column(name="order_money")	//订单锁定金额
	private double orderMoney;
	
	@Column(name="remark_")	//备注
	private String remark;
	
	@Column(name="state_")	//状态1:编辑2:待缴费3:缴费中4:已缴费
	private int state;
	
	@Column(name="org_id")	//学校ID
	private String orgId;

	public String getFreeCartId() {
		return freeCartId;
	}

	public void setFreeCartId(String freeCartId) {
		this.freeCartId = freeCartId;
	}

	public String getFreeCartName() {
		return freeCartName;
	}

	public void setFreeCartName(String freeCartName) {
		this.freeCartName = freeCartName;
	}

	public String getFreeCartItemId() {
		return freeCartItemId;
	}

	public void setFreeCartItemId(String freeCartItemId) {
		this.freeCartItemId = freeCartItemId;
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

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
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

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public String getPayTypeId() {
		return payTypeId;
	}

	public void setPayTypeId(String payTypeId) {
		this.payTypeId = payTypeId;
	}

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Double getLessMoney() {
		return lessMoney;
	}

	public void setLessMoney(Double lessMoney) {
		this.lessMoney = lessMoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Double getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(Double orderMoney) {
		this.orderMoney = orderMoney;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}
	
	
	
}
