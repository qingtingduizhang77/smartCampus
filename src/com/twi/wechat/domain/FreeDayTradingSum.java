package com.twi.wechat.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

/**
 * 每日交易汇总
 * 
 * @author zhengjc
 *
 */
@Entity
@Table(name = "free_day_trading_sum")
public class FreeDayTradingSum extends IdEntity implements Serializable {

	private static final long serialVersionUID = -2334528322902919368L;

	@Column(name = "pay_number")
	private int payNumber;// 总交易笔数

	@Column(name = "sum_money")
	private double sumMoney;// 总交易额

	// 微信
	@Column(name = "pay_wx_number")
	private int payWxNumber;// 微信交易笔数

	@Column(name = "sum_wx_money")
	private double sumWxMoney;// 微信总交易额

	@Column(name = "refund_wx_money")
	private double refundWxMoney;//  微信退款金额

	@Column(name = "Service_wx_money")
	private double serviceWxMoney;//  微信手续费

	@Column(name = "check_wx_state")
	private int checkWxState;// 微信是否已对帐(0未对帐1已对帐)

	@Column(name = "error_wx_msg")
	private String errorWxMsg;// 微信错误信息

	// 支付宝
	@Column(name = "pay_ali_number")
	private int payAliNumber;// 支付宝交易笔数

	@Column(name = "sum_ali_money")
	private double sumAliMoney;// 支付宝总交易额

	@Column(name = "refund_ali_money")
	private double refundAliMoney;// 支付宝退款金额

	@Column(name = "Service_ali_money")
	private double serviceAliMoney;// 支付宝手续费

	@Column(name = "check_ali_state")
	private int checkAliState;// 支付宝是否已对帐(0未对帐1已对帐)

	@Column(name = "error_ali_msg")
	private String errorAliMsg;// 支付宝错误信息

	// 现金
	@Column(name = "pay_cash_number")
	private int payCashNumber;// 现金交易笔数

	@Column(name = "sum_cash_money")
	private double sumCashMoney;// 现金总交易额

	@Column(name = "refund_cash_money")
	private double refundCashMoney;// 现金退款金额

	@Column(name = "Service_cash_money")
	private double serviceCashMoney;// 现金手续费

	@Column(name = "check_cash_state")
	private int checkCashState;// 现金是否已对帐(0未对帐1已对帐)

	@Column(name = "error_cash_msg")
	private String errorCashMsg;// 现金错误信息

	// 刷卡
	@Column(name = "pay_card_number")
	private int payCardNumber;// 刷卡交易笔数

	@Column(name = "sum_card_money")
	private double sumCardMoney;// 刷卡总交易额

	@Column(name = "refund_card_money")
	private double refundCardMoney;// 刷卡退款金额

	@Column(name = "Service_card_money")
	private double serviceCardMoney;// 刷卡手续费

	@Column(name = "check_card_state")
	private int checkCardState;// 刷卡是否已对帐(0未对帐1已对帐)

	@Column(name = "error_card_msg")
	private String errorCardMsg;// 刷卡错误信息

	@Column(name = "creater_time")
	private Date createTime;// 创建时间

	@Column(name = "org_id")
	private String orgId;// 学校id

	@Column(name = "re_date")
	private Date reDate;// 对账日期

	public int getPayNumber() {
		return payNumber;
	}

	public void setPayNumber(int payNumber) {
		this.payNumber = payNumber;
	}

	public double getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(double sumMoney) {
		this.sumMoney = sumMoney;
	}

	public int getPayWxNumber() {
		return payWxNumber;
	}

	public void setPayWxNumber(int payWxNumber) {
		this.payWxNumber = payWxNumber;
	}

	public double getSumWxMoney() {
		return sumWxMoney;
	}

	public void setSumWxMoney(double sumWxMoney) {
		this.sumWxMoney = sumWxMoney;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getReDate() {
		return reDate;
	}

	public void setReDate(Date reDate) {
		this.reDate = reDate;
	}

	public double getRefundWxMoney() {
		return refundWxMoney;
	}

	public void setRefundWxMoney(double refundWxMoney) {
		this.refundWxMoney = refundWxMoney;
	}

	public double getServiceWxMoney() {
		return serviceWxMoney;
	}

	public void setServiceWxMoney(double serviceWxMoney) {
		this.serviceWxMoney = serviceWxMoney;
	}

	public int getCheckWxState() {
		return checkWxState;
	}

	public void setCheckWxState(int checkWxState) {
		this.checkWxState = checkWxState;
	}

	public String getErrorWxMsg() {
		return errorWxMsg;
	}

	public void setErrorWxMsg(String errorWxMsg) {
		this.errorWxMsg = errorWxMsg;
	}

	public int getPayAliNumber() {
		return payAliNumber;
	}

	public void setPayAliNumber(int payAliNumber) {
		this.payAliNumber = payAliNumber;
	}

	public double getSumAliMoney() {
		return sumAliMoney;
	}

	public void setSumAliMoney(double sumAliMoney) {
		this.sumAliMoney = sumAliMoney;
	}

	public double getRefundAliMoney() {
		return refundAliMoney;
	}

	public void setRefundAliMoney(double refundAliMoney) {
		this.refundAliMoney = refundAliMoney;
	}

	public double getServiceAliMoney() {
		return serviceAliMoney;
	}

	public void setServiceAliMoney(double serviceAliMoney) {
		this.serviceAliMoney = serviceAliMoney;
	}

	public int getCheckAliState() {
		return checkAliState;
	}

	public void setCheckAliState(int checkAliState) {
		this.checkAliState = checkAliState;
	}

	public String getErrorAliMsg() {
		return errorAliMsg;
	}

	public void setErrorAliMsg(String errorAliMsg) {
		this.errorAliMsg = errorAliMsg;
	}

	public int getPayCashNumber() {
		return payCashNumber;
	}

	public void setPayCashNumber(int payCashNumber) {
		this.payCashNumber = payCashNumber;
	}

	public double getSumCashMoney() {
		return sumCashMoney;
	}

	public void setSumCashMoney(double sumCashMoney) {
		this.sumCashMoney = sumCashMoney;
	}

	public double getRefundCashMoney() {
		return refundCashMoney;
	}

	public void setRefundCashMoney(double refundCashMoney) {
		this.refundCashMoney = refundCashMoney;
	}

	public double getServiceCashMoney() {
		return serviceCashMoney;
	}

	public void setServiceCashMoney(double serviceCashMoney) {
		this.serviceCashMoney = serviceCashMoney;
	}

	public int getCheckCashState() {
		return checkCashState;
	}

	public void setCheckCashState(int checkCashState) {
		this.checkCashState = checkCashState;
	}

	public String getErrorCashMsg() {
		return errorCashMsg;
	}

	public void setErrorCashMsg(String errorCashMsg) {
		this.errorCashMsg = errorCashMsg;
	}

	public int getPayCardNumber() {
		return payCardNumber;
	}

	public void setPayCardNumber(int payCardNumber) {
		this.payCardNumber = payCardNumber;
	}

	public double getSumCardMoney() {
		return sumCardMoney;
	}

	public void setSumCardMoney(double sumCardMoney) {
		this.sumCardMoney = sumCardMoney;
	}

	public double getRefundCardMoney() {
		return refundCardMoney;
	}

	public void setRefundCardMoney(double refundCardMoney) {
		this.refundCardMoney = refundCardMoney;
	}

	public double getServiceCardMoney() {
		return serviceCardMoney;
	}

	public void setServiceCardMoney(double serviceCardMoney) {
		this.serviceCardMoney = serviceCardMoney;
	}

	public int getCheckCardState() {
		return checkCardState;
	}

	public void setCheckCardState(int checkCardState) {
		this.checkCardState = checkCardState;
	}

	public String getErrorCardMsg() {
		return errorCardMsg;
	}

	public void setErrorCardMsg(String errorCardMsg) {
		this.errorCardMsg = errorCardMsg;
	}

}
