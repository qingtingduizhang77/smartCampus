package com.twi.freepo.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 缴费订单项
 * @author zhengjc
 *
 */
@Entity
@Table(name = "free_po_item")
public class FreePoItem  implements Serializable{

	private static final long serialVersionUID = -9116943854058997752L;

	@Id
	@Column(name = "ID_")
	private String id;
	
	@Column(name = "free_po_id")
	private String freePoId;//缴费订单Id
	
	@Column(name = "free_Cart_Detailed_id")
	private String freeCartDetailedId;//缴费帐套明细(缴费明细)Id
	
	@Column(name = "money_")
	private double money;//应缴费用
	
	@Column(name = "pay_money")
	private double payMoney;//实缴费用
	
	@Column(name = "openid_")
	private String openId;//
	
	@Column(name = "creater_time")
	private Date createTime;//创建时间
	
	@Column(name = "state_")
	private int state;//状态
	
	@Column(name = "org_id")
	private String orgId;//学校id

	@Column(name = "pay_type_id")
	private String payTypeId;//缴费类型id
	
	@Column(name = "pay_type_name")
	private String payTypeName;//缴费类型
	
	@Column(name = "Mobile_number")
	private String mobileNumber;//手机号
	
	@Column(name = "remark_")
	private String remark;//备注
	
	
	@Column(name = "fee_type_code")
	private String feeTypeCode;//收费项目代码
	
	@Column(name = "syn_state")
	private int synState;//状态0否 1是
	
	public String getFreePoId() {
		return freePoId;
	}

	public void setFreePoId(String freePoId) {
		this.freePoId = freePoId;
	}

	public String getFreeCartDetailedId() {
		return freeCartDetailedId;
	}

	public void setFreeCartDetailedId(String freeCartDetailedId) {
		this.freeCartDetailedId = freeCartDetailedId;
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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFeeTypeCode() {
		return feeTypeCode;
	}

	public void setFeeTypeCode(String feeTypeCode) {
		this.feeTypeCode = feeTypeCode;
	}

	public int getSynState() {
		return synState;
	}

	public void setSynState(int synState) {
		this.synState = synState;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
