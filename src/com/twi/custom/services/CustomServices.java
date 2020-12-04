package com.twi.custom.services;

import javax.servlet.http.HttpServletRequest;

import com.twi.freepo.domain.FreePo;
import com.twi.paytype.domain.SysPayType;

import net.sf.json.JSONObject;

public interface CustomServices {


	/**
	 * 创建订单
	 * @param payType	收费项目类型对象
	 * @param freePo	缴费订单对象
	 * @param request	前端参数对象，包括：orgId（学校Id），business（业务类型），param（前端传递的业务动态参数json），session（外部ext_seesion，json格式）
	 * @param jsonMsg	错误描述JSON（为了能够获取到错误描述，采用引用类型数据对象）
	 * @return strJson
	 */
	boolean csSetFees(SysPayType payType, FreePo freePo, HttpServletRequest request, JSONObject jsonMsg);

	/**
	 * 定制业务统一接口：获取（外部）缴费项目明细
	 * @param request	前端参数对象，包括：orgId（学校Id），business（业务类型），param（前端传递的业务动态参数json），session（外部ext_seesion，json格式）
	 * @return strJson
	 */
	String csGetFees(HttpServletRequest request);
}
