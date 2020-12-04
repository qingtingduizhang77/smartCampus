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
@Table(name = "sys_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends IdEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 463747439112208442L;
	
	@Column(name = "name_")
	private String name;
	
	@Column(name = "nickname_")
	private String nickname;
	
	
	@Column(name = "accounts_")
	private String account;
	
	
	@Column(name = "pwd_")
	private String pwd;
	
	@Column(name = "headimg_url")
	private String headimgUrl;
	
	@Column(name = "email_")
	private String email;
	
	@Column(name = "Mobile_number")
	private String mobileNumber;
	
	@Column(name = "remark_")
	private String remark;
	
	@Column(name = "state_")
	private int state;
	
	@Column(name = "creater_date")
	private Date createrDate;
	
	@Column(name = "org_id")
	private String orgId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getHeadimgUrl() {
		return headimgUrl;
	}

	public void setHeadimgUrl(String headimgUrl) {
		this.headimgUrl = headimgUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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

	public Date getCreaterDate() {
		return createrDate;
	}

	public void setCreaterDate(Date createrDate) {
		this.createrDate = createrDate;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	

	

	
	
		

}
