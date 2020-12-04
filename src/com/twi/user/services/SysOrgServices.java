package com.twi.user.services;

import java.util.List;

import com.twi.user.domain.SysOrgInfo;

public interface SysOrgServices {

	SysOrgInfo getOrgInfoById(String id);

	/**
	 * 根据ID获取有效（启用）学校对象
	 * @param orgId	学校ID
	 * @return
	 */
	SysOrgInfo getOrgInfoByIdForEnable(String orgId);
	/**
	 * 根据KEY获取有效（启用）学校对象
	 * @param key	学校KEY
	 * @return
	 */
	SysOrgInfo getOrgInfoByKeyForEnable(String key);
	/**
	 * 根据DNS获取有效（启用）学校对象
	 * @param dns	学校DNS
	 * @return
	 */
	SysOrgInfo getOrgInfoByDnsForEnable(String dns);
	/**
	 * 根据KEY获取有效（启用）学校ID
	 * @param key	学校KEY
	 * @return
	 */
	String getOrgIdByKeyForEnable(String key);
	/**
	 * 根据DNS获取有效（启用）学校ID
	 * @param dns	学校DNS
	 * @return
	 */
	String getOrgIdByDnsForEnable(String dns);

	List<SysOrgInfo> getOrgInfoList();
}
