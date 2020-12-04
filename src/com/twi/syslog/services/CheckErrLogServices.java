package com.twi.syslog.services;

import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.syslog.domain.CheckErrLog;

public interface CheckErrLogServices {

	boolean add(CheckErrLog entity);

	boolean update(CheckErrLog entity);
	
	CheckErrLog getEntityById(String id);
	
	CheckErrLog getEntity(String freePoNo, String orgId);

	// 获取分页列表
	Page<CheckErrLog> getPageList(Page<CheckErrLog> page, Map<String, Object> pMap);
}
