package com.twi.syslog.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

/**
 * 日志
 * @author zhengjc
 *
 */
@Entity
@Table(name = "sys_log")
public class SysLog extends IdEntity implements Serializable{

	
	private static final long serialVersionUID = -7301743792544824339L;
	
	@Column(name = "type_")
	private String type;//1、微信对账失败：SYS_ERR_001；	2、每日交易量异常：SYS_ERR_002；

	
	@Column(name = "description_")
	private String description;// 描述
	
	@Column(name = "org_id")
	private String orgId;// 
	
	@Column(name = "create_time")
	private Date createTime;// 

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
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

		
}
