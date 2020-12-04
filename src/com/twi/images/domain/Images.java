package com.twi.images.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.twi.base.domain.IdEntity;

@Entity
@Table(name = "sys_ad_img")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Images extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1262940311165747534L;
	
	@Column(name = "title_")
	private String title;	//标题
	
	@Column(name="order_number")
	private int orderNumber;	//排序
	
	@Column(name="img_url")
	private String imgUrl;	//图片URL
	
	@Column(name="link_url")
	private String linkUrl;	//链接
	
	@Column(name="remark_")
	private String remark;	//备注
	
	@Column(name="state_")
	private int state;	//状态1:正常2删除
	
	@Column(name = "creater_date")
	private Date createDate;	//创建时间
	
	@Column(name = "org_id")
	private String orgId;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

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
	
	
}
