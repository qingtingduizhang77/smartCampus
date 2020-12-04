package com.twi.major.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "sys_major_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Major extends IdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8119548866931010699L;

	@Column(name = "name_") //专业名称
	private String name;
	
	@Column(name = "remark_") //备注
	private String remark;
	
	@Column(name = "pid_")	//父级ID
	private String pid;
	
	@Column(name = "state_") //状态
	private int state;
	
	@Column(name = "create_date")  //日期
	private Date createDate;
	
	@Column(name = "org_id")  //机构
	private String orgId;
	
	@Column(name = "pname_")  //父级名称
	private String pname;

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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	
}
