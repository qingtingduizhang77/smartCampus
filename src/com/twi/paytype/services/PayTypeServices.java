package com.twi.paytype.services;

import java.util.List;

import com.twi.base.domain.Page;
import com.twi.paytype.domain.SysPayType;

public interface PayTypeServices {

	/**
	 * 缴费类型列表
	 * @param page
	 * @param name
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Page<SysPayType> getPayTypePage(Page<SysPayType> page, String name, String startDate, String endDate, String orgId);
	
	/**
	 * 缴费类型编辑新增保存
	 * @param payType
	 * @return
	 */
	boolean savePayType(SysPayType payType);
	
	/**
	 * 更新缴费类型状态
	 * @param ids
	 * @param state
	 * @return
	 */
	boolean updateState(String ids, String state);
	
	/**
	 * 缴费类型详细
	 * @param id
	 * @return
	 */
	SysPayType getPayTypeById(String id);
	
	/**
	 * 缴费类型列表
	 * @param orgId
	 * @return
	 */
	List<SysPayType> getPayTypeList(String orgId);
	
	/**
	 * 根据orgId，payTypeCode获取缴费类型详细，如果不存在则增加
	 * @param orgId			学校ID
	 * @param feeTypeCode	收费项目编码
	 * @param feeTypeName	收费项目名称
	 * @return
	 */
	public SysPayType findPayTypeByOrgCode(String orgId,String feeTypeCode,String feeTypeName);
}
