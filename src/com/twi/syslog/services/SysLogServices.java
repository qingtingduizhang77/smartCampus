package com.twi.syslog.services;

import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.syslog.domain.SysLog;

public interface SysLogServices {

	boolean add(SysLog entity);

	boolean update(SysLog entity);
	
	SysLog getEntityById(String id);

	// 获取分页列表
	Page<SysLog> getPageList(Page<SysLog> page, Map<String, Object> pMap);
}
