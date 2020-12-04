package com.twi.paymenu.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twi.base.domain.IdEntity;

/**
 * 缴费菜单
 * @author zhengjc
 *
 */
@Entity
@Table(name = "sys_pay_menu")
public class SysPayMenu extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "name_")
	private String name;//名称
	
	@Column(name = "img_url")
	private String imgUrl;//图标URL
	
	@Column(name = "pwd_is")
	private int isPwd;//是否密码
	
	@Column(name = "order_number")
	private int orderNumber;//排序号
	
	@Column(name = "remark_")
	private String remark;//备注
	
	@Column(name = "state_")
	private int state;//状态
	
	@Column(name = "creater_date")
	private Date createDate;//创建时间
	
	@Column(name = "org_id")
	private String orgId;//学校id_

	@Column(name = "web_url")
	private String webUrl;//前端访问url

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getIsPwd() {
		return isPwd;
	}

	public void setIsPwd(int isPwd) {
		this.isPwd = isPwd;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
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

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
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

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	
	
}
