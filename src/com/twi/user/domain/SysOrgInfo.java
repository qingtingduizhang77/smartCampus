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
@Table(name = "sys_org_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysOrgInfo  extends IdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1374397256057418276L;
	
	
	@Column(name = "name_")
	private String name;
	
	@Column(name = "dns_")
	private String dns;
	
	@Column(name = "appid_")
	private String appid;
	
	@Column(name = "appsecret_")
	private String appsecret;
	
	@Column(name = "mchid_")
	private String mchid;
	
	@Column(name = "paysignkey_")
	private String paysignkey;
	
	@Column(name = "remark_")
	private String remark;
	
	@Column(name = "creater_date")
	private Date createrDate;
	
	
	@Column(name = "mch_is")
	private int mchIs;//0子帐号用的是服务商的appid,1子帐号用的是子务商的appid,2普通帐号
	
	@Column(name = "state_")
	private int state;
	
	@Column(name = "not_order_day")
	private int notOrderDay;
	
	
	@Column(name = "key_")
	private String key;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public String getMchid() {
		return mchid;
	}

	public void setMchid(String mchid) {
		this.mchid = mchid;
	}

	public String getPaysignkey() {
		return paysignkey;
	}

	public void setPaysignkey(String paysignkey) {
		this.paysignkey = paysignkey;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreaterDate() {
		return createrDate;
	}

	public void setCreaterDate(Date createrDate) {
		this.createrDate = createrDate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getMchIs() {
		return mchIs;
	}

	public void setMchIs(int mchIs) {
		this.mchIs = mchIs;
	}

	public int getNotOrderDay() {
		return notOrderDay;
	}

	public void setNotOrderDay(int notOrderDay) {
		this.notOrderDay = notOrderDay;
	}
	
	
	
	

}
