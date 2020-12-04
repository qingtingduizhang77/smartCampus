package com.twi.wechat.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

/**
 * 微信对账单信息
 * @author zhengjc
 *
 */
@Entity
@Table(name = "free_wx_pay_info")
public class FreeWxPayInfo extends IdEntity implements Serializable{

	private static final long serialVersionUID = -6424167175126576722L;
	
	@Column(name = "pay_time")
	private Date payTime;//交易时间
		
	@Column(name = "appid")
	private String appid;//公众账号ID
	
	@Column(name = "mid_")
	private String mid;//商户号
	
	@Column(name = "sub_mid")
	private String subMid;//子商户号
	
	@Column(name = "pay_equipment")
	private String payEquipment;//交易设备
	
	@Column(name = "wx_order_no")
	private String wxOrderNo;//微信订单号
	
	@Column(name = "free_po_no")
	private String freePoNo;//商户订单号
	
	@Column(name = "user_Iden")
	private String openId;//用户标识
	
	@Column(name = "pay_type")
	private String payType;//交易类型	
	
	@Column(name = "pay_state")
	private String payState;//交易状态
	
	@Column(name = "pay_Bank")
	private String payBank;//交易银行
	
	@Column(name = "money_type")
	private String moneyType;//货币类型
	
	@Column(name = "pay_money")
	private double payMoney;//金额
	
	@Column(name = "p_amount")
	private double pAmount;//代金券或立减优惠金额
	
	@Column(name = "refund_wx_order_no")
	private String refundWxOrderNo;//微信退款单号
	
	@Column(name = "refund_free_po_no")
	private String refundFreePoNo;//商户退款单号
	
	@Column(name = "refund_money")
	private double refundMoney;//退款金额
	
	@Column(name = "refund_p_amount")
	private double refundPAmount;//代金券或立减优惠退款金额
	
	@Column(name = "refund_type")
	private String refundType;//退款类型

	@Column(name = "refund_state")
	private String refundState;//退款状态
	
	@Column(name = "product_name")
	private String productName;//商品名称
	
	@Column(name = "pos_data")
	private String posData;//商户数据包
	
	@Column(name = "Service_money")
	private double serviceMoney;//手续费
	
	@Column(name = "rate_")
	private double rate;//费率
	
	@Column(name = "payer_")
	private String payer; // '付款方'
	
	@Column(name = "free_wx_pay_id")
	private String freeWxPayId;//微信缴费单Id
	
	@Column(name = "org_id")
	private String orgId;//学校id
	
	@Column(name = "creater_time")
	private Date createTime;//创建时间

	public double getpAmount() {
		return pAmount;
	}

	public void setpAmount(double pAmount) {
		this.pAmount = pAmount;
	}

	public String getPosData() {
		return posData;
	}

	public void setPosData(String posData) {
		this.posData = posData;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getSubMid() {
		return subMid;
	}

	public void setSubMid(String subMid) {
		this.subMid = subMid;
	}

	public String getFreeWxPayId() {
		return freeWxPayId;
	}

	public void setFreeWxPayId(String freeWxPayId) {
		this.freeWxPayId = freeWxPayId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

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

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayEquipment() {
		return payEquipment;
	}

	public void setPayEquipment(String payEquipment) {
		this.payEquipment = payEquipment;
	}

	public String getPayState() {
		return payState;
	}

	public void setPayState(String payState) {
		this.payState = payState;
	}

	public String getPayBank() {
		return payBank;
	}

	public void setPayBank(String payBank) {
		this.payBank = payBank;
	}

	public String getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	public double getServiceMoney() {
		return serviceMoney;
	}

	public void setServiceMoney(double serviceMoney) {
		this.serviceMoney = serviceMoney;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getRefundState() {
		return refundState;
	}

	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public String getRefundType() {
		return refundType;
	}

	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}

	public String getRefundWxOrderNo() {
		return refundWxOrderNo;
	}

	public void setRefundWxOrderNo(String refundWxOrderNo) {
		this.refundWxOrderNo = refundWxOrderNo;
	}

	public String getRefundFreePoNo() {
		return refundFreePoNo;
	}

	public void setRefundFreePoNo(String refundFreePoNo) {
		this.refundFreePoNo = refundFreePoNo;
	}

	public double getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(double refundMoney) {
		this.refundMoney = refundMoney;
	}

	public double getRefundPAmount() {
		return refundPAmount;
	}

	public void setRefundPAmount(double refundPAmount) {
		this.refundPAmount = refundPAmount;
	}

}
