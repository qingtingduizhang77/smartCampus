package com.twi.freecartdetailed.domain;

public class DetailedModel extends FreeCartDetailed{

	private static final long serialVersionUID = 6107508459748207741L;

	private int payTypeCode;// 缴费类型的是否一次性缴费属性(1:一次性；2：多次缴费)
	private double ableAmount;// 本次可缴金额
	
	public int getPayTypeCode() {
		return payTypeCode;
	}
	public void setPayTypeCode(int payTypeCode) {
		this.payTypeCode = payTypeCode;
	}
	public double getAbleAmount() {
		return ableAmount;
	}
	public void setAbleAmount(double ableAmount) {
		this.ableAmount = ableAmount;
	}
	
	
}
