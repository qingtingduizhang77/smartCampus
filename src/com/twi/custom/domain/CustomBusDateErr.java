package com.twi.custom.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "custom_business_date_err")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CustomBusDateErr extends IdEntity implements Serializable{

	private static final long serialVersionUID = -2363660159368358778L;
	
	@Column(name = "data_") 
	private String data;//数据
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	@Column(name = "err_msg") 
	private String err_msg; //异常描述
	public String getErrMsg() {
		return err_msg;
	}
	public void setErrMsg(String err_msg) {
		this.err_msg = err_msg;
	}
	
}
