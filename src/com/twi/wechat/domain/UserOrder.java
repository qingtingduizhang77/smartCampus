package com.twi.wechat.domain;

import java.util.List;
import java.util.Map;

import com.twi.freepo.domain.FreePo;

public class UserOrder extends FreePo {

	private static final long serialVersionUID = -1334977197183305555L;
	
	private String poId;
	private int    payState;
	private String createrTime;
	private String payTime;
	
	List<Map<String, Object>> poItemList;
	
	public String getPoId() {
		return poId;
	}
	public void setPoId(String poId) {
		this.poId = poId;
	}
	public int getPayState() {
		return payState;
	}
	public void setPayState(int payState) {
		this.payState = payState;
	}
	
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public List<Map<String, Object>> getPoItemList() {
		return poItemList;
	}
	public void setPoItemList(List<Map<String, Object>> poItemList) {
		this.poItemList = poItemList;
	}
	
	public String getCreaterTime() {
		return createrTime;
	}
	public void setCreaterTime(String createrTime) {
		this.createrTime = createrTime;
	}	
	
}
