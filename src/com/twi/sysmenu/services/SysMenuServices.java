package com.twi.sysmenu.services;

import org.springframework.stereotype.Service;

import com.twi.sysmenu.domain.SysMenu;


public interface SysMenuServices {
	SysMenu getSysMenuBYRole(String roleId,String orgId);
}
