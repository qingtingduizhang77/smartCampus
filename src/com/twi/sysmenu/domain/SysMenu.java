package com.twi.sysmenu.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.twi.base.domain.IdEntity;

/**
 * 系统菜单表
 * @author ouwt
 *
 */
@Entity
@Table(name = "sys_menu")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysMenu  extends IdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -661814197433808972L;

	
	@Column(name = "role_id")
	private String roleId;//角色id
	
	
	@Column(name = "org_id")
	private String orgId;//学校id_
	
	@Column(name = "context_")
	private String context;//内容

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	
	
	
	
}
