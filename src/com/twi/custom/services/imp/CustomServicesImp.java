package com.twi.custom.services.imp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.util.PropertiesMsg;
import com.twi.base.util.ResultFormat;
import com.twi.base.util.ServiceClientUtil;
import com.twi.base.util.StringUtils;
import com.twi.custom.domain.CustomBusDateErr;
import com.twi.custom.services.CustomServices;
import com.twi.freepo.domain.FreePo;
import com.twi.freepo.domain.FreePoItem;
import com.twi.paytype.domain.SysPayType;
import com.twi.wechat.domain.FreeWxPay;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
/**
 * 自定义收费类型
 * @author ouwt
 *
 */
@Service("customServices")
public class CustomServicesImp extends BaseService  implements CustomServices {
	
	/**
	 * 统一定制业务接口
	 * @param jsonParam	执行业务服务的参数集JSON，包括：orgId（学校Id），service（服务类型），business（业务类型），param（前端传递的业务动态参数json），session（外部seesion，json格式）
	 * @return jsonstr
	 */
	private String csExtend(JSONObject jsonParam) {
		Long tip = (new Date()).getTime();
		String result = ResultFormat.jsonStrFail(PropertiesMsg.getProperty("custom-Exception.CustomServicesImp.csExtend"));
		try{
			logger.debug("==调用定制服务开始>>csExtend==##tip:"+tip);
			String orgId = jsonParam.optString("orgId");
			String service = jsonParam.optString("service");
			String business = jsonParam.optString("business");
			String sql="SELECT id_ FROM custom_business_date_config WHERE orgId_=? AND service_=? AND business_=?";
			List<Map<String, Object>> list=this.jdbcDao.queryForMap(sql, new Object[]{orgId,service,business});
			if (list==null || list.size()==0) {
				return result = ResultFormat.jsonStrFail(PropertiesMsg.getProperty("custom-BusSerIsNothing"));
			}
			//组装服务参数集-begin
			JSONObject jsonInput = new JSONObject();
			jsonInput.put("orgId", orgId);
			jsonInput.put("service", service);
			jsonInput.put("business", business);
			jsonInput.put("param", jsonParam.optJSONObject("param"));
			jsonInput.put("ext_session", jsonParam.optJSONObject("ext_session"));
			//组装服务参数集-end
			logger.debug("==调用定制服务执行>>csExtend==##tip:"+tip+"##input:"+jsonInput.toString());
			//调用接口
			result = ServiceClientUtil.csExtend(jsonInput.toString());
		} catch(JSONException e) {
			e.printStackTrace();
			result = ResultFormat.jsonStrFail(PropertiesMsg.getProperty("custom-JSONException.CustomServicesImp"));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			logger.debug("==调用定制服务结束>>csExtend==##tip:"+tip+"##result:"+result);
		}
		return result;
	}

	@Override
	public String csGetFees(HttpServletRequest request) {
		JSONObject jsonParam = new JSONObject();
		try {
			//组装业务服务参数集-begin
			jsonParam.put("orgId", (String)request.getSession().getAttribute("orgId"));
			jsonParam.put("service", "csGetFees"); //定制服务类型，获取外部费用
			jsonParam.put("business", request.getParameter("business"));
			jsonParam.put("param", JSONObject.fromObject(request.getParameterMap()));
//			jsonParam.put("ext_session", request.getSession().getAttribute("ext_session"));
			//组装业务服务参数集-end
		} catch(Exception e) {}
		return csExtend(jsonParam);
	}

	@Override
	public boolean csSetFees(SysPayType payType, FreePo freePo, HttpServletRequest request, JSONObject jsonMsg) {
		try{
			
			String orgId  = (String)request.getSession().getAttribute("orgId");
			String service = "csSetFees"; //定制服务类型，设置外部费用
			String business = request.getParameter("business");
			JSONObject param = JSONObject.fromObject(request.getParameterMap());
			Object ext_session = (request.getSession().getAttribute("ext_session"));

			//新增缴费明细项
			FreePoItem item = new FreePoItem();
			item.setId(java.util.UUID.randomUUID().toString());
			item.setFreePoId(freePo.getId());
			item.setMoney(freePo.getMoney());
			item.setPayMoney(freePo.getPayMoney());
			item.setOrgId(orgId);
			item.setOpenId(freePo.getOpenId());
			item.setCreateTime(new Date());
			item.setPayTypeId(payType.getId());
			item.setPayTypeName(payType.getName());
			item.setFeeTypeCode(payType.getCode());
			item.setState(1);

			//新增微信缴费单
			FreeWxPay freeWxPay = new FreeWxPay();
			freeWxPay.setFreePoId(freePo.getId());
			freeWxPay.setFreePoNo(freePo.getOrderNo());
			freeWxPay.setPayMoney(freePo.getPayMoney());
			freeWxPay.setState(1);
			freeWxPay.setPayMode(1);

			//补充业务参数集-begin
			param.put("feeTypeCode", payType.getCode());
			param.put("feeTypeName", payType.getName());
			param.put("amount", freePo.getPayMoney());
			param.put("productName", freePo.getProductName());
			param.put("orderNo", freePo.getOrderNo());
			param.put("freePoItemId", item.getId());
			//补充业务参数集-end
			//组装异常记录内容-begin
			JSONObject jsonErr = new JSONObject();
			jsonErr.put("orgId", orgId);
			jsonErr.put("service", service);
			jsonErr.put("business", business);
			jsonErr.put("param", param);
			jsonErr.put("ext_session", ext_session);
			//组装异常记录内容-end
//			boolean faly = false; //异常记录
//			try{
				String res = StringUtils.trimToEmpty(csExtend(jsonErr));
				if(!res.equals("")) {
					JSONObject jsonObject = JSONObject.fromObject(res);
					if(jsonObject.optString("return_code").equals("SUCCESS")) {
//						faly = true;
						//保存缴费订单
						if(!this.hibernateBaseDao.addEntity(freePo)) {
							jsonMsg.put("msg", PropertiesMsg.getProperty("custom-CreateFreePoErr"));
							return false;
						}
						//保存缴费订单明细项
						if(!this.hibernateBaseDao.addEntity(item)) {
							jsonMsg.put("msg", PropertiesMsg.getProperty("custom-CreateFreeItemErr"));
							return false;
						}
						//保存微信缴费单
						if(!this.hibernateBaseDao.addEntity(freeWxPay)) {
							jsonMsg.put("msg", PropertiesMsg.getProperty("custom-CreateFreeWxPayErr"));
							return false;
						}
					} else {
						jsonMsg.put("msg", jsonObject.optString("return_msg"));
						//异常日志
						CustomBusDateErr errData=new CustomBusDateErr();
						errData.setData(jsonErr.toString());
						errData.setErrMsg(jsonObject.optString("return_msg"));
						this.hibernateBaseDao.addEntity(errData);
						return false;
					}
				}
//			} catch(Exception e) {
//			} finally {
//				//记录异常
//				try {
//					if(!faly) {
//						CustomBusDateErr errData=new CustomBusDateErr();
//						errData.setData(jsonErr.toString());
//						this.hibernateBaseDao.addEntity(errData);
//						return false;
//					}
//				} catch (Exception e) {}
//			}

		} catch (Exception e) {
			e.printStackTrace();
			jsonMsg.put("msg", PropertiesMsg.getProperty("custom-Exception.CustomServicesImp.csSetFees"));
			return false;
		}
		return true;
	}

}
