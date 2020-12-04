package com.twi.freecartdetailedlog.services;

import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.freecartdetailedlog.domain.DetailedLog;

public interface DetailedLogServices {

	boolean add(DetailedLog entity);

	boolean update(DetailedLog entity);

	boolean deleteById(String id);

	boolean batchDelete(String[] ids);

	//获取明细分页列表
	Page<DetailedLog> getPageList(Page<DetailedLog> page, Map<String, Object> pMap);
}
