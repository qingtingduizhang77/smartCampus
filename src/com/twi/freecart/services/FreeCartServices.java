package com.twi.freecart.services;

import java.util.List;
import java.util.Map;
import com.twi.base.domain.Page;
import com.twi.freecart.domain.FreeCart;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.paytype.domain.SysPayType;
import com.twi.student.domain.SysStudent;

public interface FreeCartServices {
	
	boolean addFreeCart(FreeCart freeCart);
	
	boolean updFreeCart(FreeCart freeCart);
	
	boolean delFreeCartById(String id);
	
	boolean batchDelete(String[] ids);
	
	FreeCart getFreeCartByName(String name, String orgId);
	
	FreeCart getFreeCartById(String id);
	
	Page<FreeCart> getFreeCartPage(Page<FreeCart> page, Map<String, Object> pMap);
	
	SysStudent getStudentByCode(String code, String orgId);
	
	SysPayType getPayTypeByName(String name, String orgId);
	
	boolean addSysStudent(SysStudent student);
	
	boolean updSysStudent(SysStudent student);
		
	boolean batchAddFreeCartDetailed(List<FreeCartDetailed> list);
	
	FreeCartDetailed queryBycondition(String freeCartId,String code,String name,String payTypeName,String orgId);
	
	List<Map<String, Object>> getPayTypeStr(String freeCartId,String orgId );
	
	List<Map<String, Object>> getFreeCartDetailedList(Map<String, Object> pMap,String[] columnKey);
}
