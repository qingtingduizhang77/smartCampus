package com.twi.base.services;

import java.util.List;
import java.util.Map;

import com.twi.paymenu.domain.SysPayMenu;
import com.twi.user.domain.SysOrgInfo;

public interface CommonServices {
	
	String getOrgIdByServerName(String serverName);
	String getOrgIdByKey(String Key);
	SysOrgInfo getSysOrgInfoById(String id);
	SysOrgInfo getSysOrgInfoByKey(String key) ;
	List<Map<String,Object>> getRole(String userId);
	SysPayMenu getSysPayMenu(String orgId,String menuName);
	

}
