package com.twi.freecart.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "free_cart")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FreeCart extends IdEntity implements Serializable{

	private static final long serialVersionUID = -6649105919900351781L;
	
	@Column(name = "name_")  //名称
	private String name;
	
	@Column(name="code_")  //编号
	private Integer code;
	
	@Column(name = "create_date")  //创建日期
	private Date createDate;
	
	@Column(name="describe_")	//描述
	private String describe;
	
	@Column(name = "remark_")	//说明
	private String remark;
	
	@Column(name="year_")	//年份
	private int year;
	
	@Column(name="del_state")	//是否可删除1:是2否:
	private int delState;
	
	@Column(name="pay_type_id")	//包含缴费类型id_，多个用','分隔
	private String payTypeId;
	
	@Column(name="pay_type_name")	//包含缴费类型名称，多个用','分隔
	private String payTypeName;
	
	@Column(name="state_")	//状态1:禁用2:启用3:归档
	private int state;
	
	@Column(name="org_id")	//学校id_
	private String orgId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getDelState() {
		return delState;
	}

	public void setDelState(int delState) {
		this.delState = delState;
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
	
	
	

}
