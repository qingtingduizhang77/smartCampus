package com.twi.freecartdetailedlog.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "free_cart_detailed_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DetailedLog extends IdEntity implements Serializable{

	private static final long serialVersionUID = 2089790239069312351L;
	
	@Column(name = "user_id") //用户ID
	private String userId;
	
	@Column(name="free_Cart_id") //账套ID
	private String freeCartId;
	
	@Column(name="opt_type")	//操作  ：清空、上传
	private String optType;
	
	@Column(name="context_")	//内容
	private String context;
	
	@Column(name="org_id")	//机构ID
	private String orgId;
	
	@Column(name="creater_date")	//创建日期
	private Date createDate;
	
	@Column(name="file_url")	//文件地址
	private String fileUrl;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFreeCartId() {
		return freeCartId;
	}

	public void setFreeCartId(String freeCartId) {
		this.freeCartId = freeCartId;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}	
	
}
