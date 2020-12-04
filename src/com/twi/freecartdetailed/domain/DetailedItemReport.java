package com.twi.freecartdetailed.domain;

public class DetailedItemReport extends FreeCartDetailed{

	private static final long serialVersionUID = -2322015871321272785L;

	private Integer tpeopleCount;//缴费人数
	private Integer tpayCount;	//缴费笔数
	private Double tmoney;		//缴费金额
	private Double tpayMoney;	//实缴金额
	private Double tlessMoney;	//欠费金额
	private Integer payState;	//状态：0：未缴费   1：已缴费
	
	public Integer getTpeopleCount() {
		return tpeopleCount;
	}
	public void setTpeopleCount(Integer tpeopleCount) {
		this.tpeopleCount = tpeopleCount;
	}
	public Integer getTpayCount() {
		return tpayCount;
	}
	public void setTpayCount(Integer tpayCount) {
		this.tpayCount = tpayCount;
	}
	public Double getTmoney() {
		return tmoney;
	}
	public void setTmoney(Double tmoney) {
		this.tmoney = tmoney;
	}
	public Double getTpayMoney() {
		return tpayMoney;
	}
	public void setTpayMoney(Double tpayMoney) {
		this.tpayMoney = tpayMoney;
	}
	public Double getTlessMoney() {
		return tlessMoney;
	}
	public void setTlessMoney(Double tlessMoney) {
		this.tlessMoney = tlessMoney;
	}
	public Integer getPayState() {
		return payState;
	}
	public void setPayState(Integer payState) {
		this.payState = payState;
	}
			
}
