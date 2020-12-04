package com.twi.wechat.domain;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

/**
 * 微信缴费单
 * @author zhengjc
 *
 */
@Entity
@Table(name = "free_wx_pay")
public class FreeWxPay extends IdEntity implements Serializable{

	private static final long serialVersionUID = -6945795295172453971L;
	
	@Column(name = "wx_order_no")
	private String wxOrderNo;//微信订单号
	
	@Column(name = "free_po_no")
	private String freePoNo;//商户订单号
	
	@Column(name = "free_po_id")
	private String freePoId;//商户订单id
	
	@Column(name = "pay_money")
	private double payMoney;//金额
	
	@Column(name = "pay_time")
	private Date payTime;//交易时间

	@Column(name = "state_")
	private int state;//状态1:待付款2:已付款3:删除
	
	@Column(name = "openid_")
	private String openId;//openId
	
	@Column(name = "org_id")
	private String orgId;//学校id_

	@Column(name = "pay_mode")
	private int payMode;//支付方式：1、微信、2支付宝  3现金   4刷卡'
	
	//临时
	@Transient
	private String studentCode;//学生学号
	@Transient
	private String studentName;//学生姓名(付款方)
	@Transient
	private String productName;//商品名称
	@Transient
	private String payTypeName;//包含缴费类型名称
	@Transient
	private String payTypeId;//缴费类型id
	@Transient
	private String majorName;//专业
	@Transient
	private String className;//班级
	@Transient
	private String payModeStr;//支付方式：1、微信、2支付宝  3现金   4刷卡'
	
	

	public String getWxOrderNo() {
		return wxOrderNo;
	}

	public void setWxOrderNo(String wxOrderNo) {
		this.wxOrderNo = wxOrderNo;
	}

	public String getFreePoNo() {
		return freePoNo;
	}

	public void setFreePoNo(String freePoNo) {
		this.freePoNo = freePoNo;
	}

	public String getFreePoId() {
		return freePoId;
	}

	public void setFreePoId(String freePoId) {
		this.freePoId = freePoId;
	}

	public double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getPayTypeId() {
		return payTypeId;
	}

	public void setPayTypeId(String payTypeId) {
		this.payTypeId = payTypeId;
	}

	public String getMajorName() {
		return majorName;
	}

	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getPayMode() {
		return payMode;
	}

	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}

	public String getPayModeStr() {
		return payModeStr;
	}

	public void setPayModeStr(String payModeStr) {
		this.payModeStr = payModeStr;
	}

	
}
