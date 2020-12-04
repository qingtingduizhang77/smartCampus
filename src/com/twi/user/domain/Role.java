package com.twi.user.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "sys_role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role  extends IdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "name_")
	private String name;
	
	@Column(name = "remark_")
	private String remark;
	
	@Column(name = "code_")
	private int code;
	
	@Column(name = "creater_date")
	private Date createrDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Date getCreaterDate() {
		return createrDate;
	}

	public void setCreaterDate(Date createrDate) {
		this.createrDate = createrDate;
	}
	
	

}
