package com.twi.freecartdetailed.services;

import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.freecartdetailed.domain.DetailedItemReport;
import com.twi.freecartdetailed.domain.DetailedModel;
import com.twi.freecartdetailed.domain.DetailedSummary;
import com.twi.freecartdetailed.domain.FreeCartDetailed;

public interface FreeCartDetailedServices {
	
	boolean add(FreeCartDetailed entity);

	boolean update(FreeCartDetailed entity);

	boolean deleteById(String id);

	boolean batchDelete(String[] ids);

	FreeCartDetailed getEntityByStuCode(String studentCode, String orgId);

	FreeCartDetailed getEntityById(String id);
	//获取明细分页列表
	Page<FreeCartDetailed> getPageList(Page<FreeCartDetailed> page, Map<String, Object> pMap);
	//根据条件，获取所有明细列表
	List<Map<String, Object>> getConditionList( Map<String, Object> pMap);
	//缴费汇总
	Page<DetailedSummary> getSummaryList(Page<DetailedSummary> page, Map<String, Object> pMap);
	//缴费报表
	Page<DetailedItemReport> getItemReportList(Page<DetailedItemReport> page, Map<String, Object> pMap);
	//根据条件，获取所有缴费项目列表
	List<Map<String, Object>> getPayReportList( Map<String, Object> pMap);
	/**
	 * 缴费项目列表
	 * @param payMenuId 缴费菜单id
	 * @return
	 */
	List<DetailedModel> getFreeCartDetailList(String orgId, String payMenuId, String studentName, String studentCode, String pwd, boolean isPwd);
	/**
	 * 多少个ID可删除
	 * @param ids
	 * @return
	 */
	int canDeleteCount(String[] ids);
}
