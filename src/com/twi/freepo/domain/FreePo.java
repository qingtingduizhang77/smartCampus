package com.twi.freepo.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 缴费订单
 * @author zhengjc
 *
 */
@Entity
@Table(name = "free_po")
public class FreePo implements Serializable{

	private static final long serialVersionUID = -6761113717604776854L;

	@Id
	@Column(name = "ID_")
	private String id;
	
	@Column(name = "order_no")
	private String orderNo;//订单号
	
	@Column(name = "Student_code")
	private String studentCode;//学生学号
	
	@Column(name = "Student_id")
	private String studentId;//学生id
	
	@Column(name = "Student_name")
	private String studentName;//学生姓名(付款方)
	
	@Column(name = "product_name")
	private String productName;//商品名称
	
	@Column(name = "pay_type_id")
	private String payTypeId;//包含缴费类型id
	
	@Column(name = "pay_type_name")
	private String payTypeName;//包含缴费类型名称
	
	@Column(name = "money_")
	private double money;//应缴费用
	
	@Column(name = "pay_money")
	private double payMoney;//实缴费用
	
	@Column(name = "openid_")
	private String openId;//openId
	
	@Column(name = "creater_time")
	private Date createTime;//创建时间
	
	@Column(name = "state_")
	private int state;//状态1:待付款2:付款中3:已付款4:删除
	
	@Column(name = "org_id")
	private String orgId;//学校id

	@Column(name = "wxprepay_create_time_")
	private Date wxprepayCreateTime;// 微信预付单生成时间

	@Column(name = "wxprepay_id_")
	private String wxprepayId;// 微信支付预付订单ID
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	public Date getWxprepayCreateTime() {
		return wxprepayCreateTime;
	}

	public void setWxprepayCreateTime(Date wxprepayCreateTime) {
		this.wxprepayCreateTime = wxprepayCreateTime;
	}

	public String getWxprepayId() {
		return wxprepayId;
	}

	public void setWxprepayId(String wxprepayId) {
		this.wxprepayId = wxprepayId;
	}
	
	
}
