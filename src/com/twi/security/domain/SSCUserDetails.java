package com.twi.security.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SSCUserDetails implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8638302225598072247L;
	// 用户id、密码、角色(从can_cust、can_user_role、can_role、can_role_func获得）
	private String userId;
	private String username;
	private String loginPwd;
	private String account;
	private List<Map<String,Object>> roles;
	private String  orgId;
	private String  orgName;
	private boolean enabled;
	
	

	protected SSCUserDetails() {
		userId = null;
		loginPwd = null;
		
	}

	public SSCUserDetails(String userId, String username,String loginPwd, String account,String  orgId,String  orgName,List<Map<String,Object>> roles) {
		this.userId = userId;
		this.username = username;
		this.loginPwd = loginPwd;
		this.account = account;
		this.orgId = orgId;
		this.orgName = orgName;
		this.roles = roles;
		enabled=true;
	}



	@Override
	public String getPassword() {
		return this.loginPwd;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return enabled;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		
		return authorities;
	}
	
	
	@Override
	public boolean equals(Object object) {
		System.out.println(String.format("==equals:%s|%s", this.getUsername(),
				((SSCUserDetails) object).getUsername()));
		if (object instanceof SSCUserDetails) {
			if (this.getUsername() == null
					|| ((SSCUserDetails) object).getUsername() == null) {
				return false;
			}
			if (this.getUsername().equals(
					((SSCUserDetails) object).getUsername()))
				
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.getUsername() == null) {
			return 0;
		}
		
		return this.getUsername().hashCode();
	}
	
	
	public String getUserId() {
		return this.userId;
	}

	
	
	
	

	



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public List<Map<String,Object>> getRoles() {
		return roles;
	}

	public String getOrgId() {
		return orgId;
	}

	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	public String getOrgName() {
		return orgName;
	}

	
	
	

}