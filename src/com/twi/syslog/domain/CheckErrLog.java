package com.twi.syslog.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "check_err_log")
public class CheckErrLog extends IdEntity implements Serializable {

	private static final long serialVersionUID = 7487902529319098187L;
	
	@Column(name = "wx_order_no")
	private String wxOrderNo;//微信订单号
	
	@Column(name = "free_po_no")
	private String freePoNo;//商户订单号
	
	@Column(name = "pay_money")
	private double payMoney;//金额
	
	@Column(name = "pay_time")
	private Date payTime;//交易时间
	
	@Column(name = "check_mode")
	private int checkMode;//1微信对账、2支付宝对账、3POS机对账
	
	@Column(name = "state_")
	private int state;//处理状态 0 未处理  1已处理
	
	@Column(name = "org_id")
	private String orgId;//学校id_
	
	@Column(name = "err_Code") //错误编码
	private int errCode;
	
	@Column(name = "process_man") //处理人
	private String processMan;
	
	@Column(name = "advise_") //处理记录
	private String advise;
	
	@Column(name = "process_time")
	private Date processTime;//处理时间
	
	@Column(name = "creater_time")
	private Date createrTime;//创建时间

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

	public String getAdvise() {
		return advise;
	}

	public void setAdvise(String advise) {
		this.advise = advise;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public Date getCreaterTime() {
		return createrTime;
	}

	public void setCreaterTime(Date createrTime) {
		this.createrTime = createrTime;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public int getCheckMode() {
		return checkMode;
	}

	public void setCheckMode(int checkMode) {
		this.checkMode = checkMode;
	}

	public String getProcessMan() {
		return processMan;
	}

	public void setProcessMan(String processMan) {
		this.processMan = processMan;
	}

	
}
