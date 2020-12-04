package com.twi.wx.web;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twi.base.controller.BaseController;
import com.twi.base.util.CalendarUtils;
import com.twi.base.util.HttpClientUtils;
import com.twi.base.util.PropertiesMsg;
import com.twi.base.util.StringUtils;
import com.twi.base.util.WXPayUtil;
import com.twi.common.constants.WXPayConstants;
import com.twi.common.domain.ResponseModel;
import com.twi.custom.services.CustomServices;
import com.twi.freecartdetailed.domain.DetailedModel;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.freecartdetailed.services.FreeCartDetailedServices;
import com.twi.freepo.domain.FreePo;
import com.twi.freepo.services.FreePoServices;
import com.twi.paymenu.domain.SysPayMenu;
import com.twi.paymenu.services.PayMenuServices;
import com.twi.paytype.domain.SysPayType;
import com.twi.paytype.services.PayTypeServices;
import com.twi.student.domain.SysStudent;
import com.twi.student.services.SysStudentServices;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.services.SysOrgServices;
import com.twi.wechat.domain.FreeWxPay;
import com.twi.wechat.services.WxPayServices;

import net.sf.json.JSONObject;


/**
 * 微信
 * @author zhengjc
 *
 */
@RestController
@RequestMapping(value = "/web/wx/pay")
public class WxPayController extends BaseController{

	@Autowired
	private PayMenuServices payMenuServicesImp;
	
	@Autowired
	private FreeCartDetailedServices freeCartDetailedServicesImp;
	
	@Autowired
	private SysOrgServices sysOrgServicesImp;
	
	@Autowired
	private SysStudentServices sysStudentServicesImp;
	
	@Autowired
	private WxPayServices wxPayServicesImp;
	
	@Autowired
	private FreePoServices freePoServicesImp;
	
	@Autowired
	private CustomServices customServices;
	
	@Autowired
	private PayTypeServices payTypeServices;

	/**
	返回成功xml
    */
    private String resSuccessXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

    /**
    * 返回失败xml
    */
    private String resFailXml = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[报文为空]]></return_msg></xml>";
	
	/**
	 * 缴费菜单列表
	 * @return
	 */
	@RequestMapping(value="/menu/list")
	public Map<String, Object> getPayMenuList(HttpServletRequest request) {
		logger.info("------start------getPayMenuList");
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		String orgId = (String)request.getSession().getAttribute("orgId");
		List<SysPayMenu> payMenu = payMenuServicesImp.getPayMenuList(orgId);
		result.put("rows", payMenu);
		logger.info("------end------getPayMenuList");
		return createSuccessResult(result);
	}
	
	/**
	 * 获取缴费项目明细
	 * @param request
	 * @param studentName 学生姓名
	 * @param studentCode 学号
	 * @param pwd 密码
	 * @param payMenuId 缴费菜单id
	 * @return
	 */
	@RequestMapping(value="/free/cart/detail")
	public Map<String, Object> getFreeCartDetailInfo(HttpServletRequest request,
			@RequestParam(value="studentName", defaultValue = "") String studentName,
			@RequestParam(value="studentCode", defaultValue = "") String studentCode,
			@RequestParam(value="pwd", defaultValue = "") String pwd,
			@RequestParam(value="payMenuId", defaultValue = "") String payMenuId){
		logger.info("--start--getFreeCartDetailInfo:pwd:{},studentName:{},studentCode:{},payMenuId:{}",pwd,studentName,studentCode,payMenuId);
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(payMenuId) || StringUtils.isBlank(studentName) || StringUtils.isBlank(studentCode)){
			return createErrorResult("参数异常！");
		}
		if (request.getSession().getAttribute("orgId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		String orgId = request.getSession().getAttribute("orgId").toString();
		SysPayMenu menu = payMenuServicesImp.getPayMenuById(payMenuId);
		boolean isPwd = false;
		if (menu != null && menu.getState() == 1) {
			isPwd = (menu.getIsPwd() == 1 ? true : false);
		}else{
			return createErrorResult("缴费菜单不存在！");
		}
		SysStudent student = sysStudentServicesImp.getStudentByNameAndCode(orgId, studentName, studentCode, "1");
		if (student == null) {
			return createErrorResult("该学生不存在！");
		}
		List<DetailedModel> list = freeCartDetailedServicesImp.getFreeCartDetailList(orgId, payMenuId, null, studentCode, pwd, isPwd);
		result.put("rows", list);
		result.put("studentInfo", student);
		logger.info("------end------getFreeCartDetailInfo");
		return createSuccessResult(result);
	}
	
	
	
	/**
	 * 统一定制业务接口：获取（外部）缴费项目明细
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/csGetFees")
	public Map<String, Object> csGetFees(HttpServletRequest request) {
		Long tip = (new Date()).getTime();
		try {
			logger.debug("==定制服务开始>>csGetFees==##tip:"+tip);
			if(request.getSession()==null) {
				return createErrorResult("2", PropertiesMsg.getProperty("comm-SysSessionExpire"));
			}
			String openId = (String)request.getSession().getAttribute("wxOpenId");
			String orgId = (String)request.getSession().getAttribute("orgId");
			if(StringUtils.isBlank(openId)) {
				return createErrorResult("2", PropertiesMsg.getProperty("comm-WxSessionExpire"));
			}
			if(StringUtils.isBlank(orgId)) {
				return createErrorResult(PropertiesMsg.getProperty("comm-OrgIdIsNULL"));
			}
			SysOrgInfo orgInfo = sysOrgServicesImp.getOrgInfoByIdForEnable(orgId);
			if(orgInfo==null) { //学校不存在或已禁用！
				return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
			}
			String business = request.getParameter("business"); //定制业务类型
			if (StringUtils.isBlank(business)) {
				return createErrorResult(PropertiesMsg.getProperty("custom-BusSerIsNothing"));
			}
			String res = customServices.csGetFees(request);
			 if(StringUtils.isNotBlank(res)) {
				 JSONObject jsonObject = JSONObject.fromObject(res);
				 String return_code = jsonObject.optString("return_code");
				 if(return_code.equals("FAIL")) {
					 return createErrorResult(jsonObject.optString("return_msg"));
				 } else if(return_code.equals("SUCCESS")) {
					 if(jsonObject.has("return_session")) {
						 request.getSession().setAttribute("ext_session",jsonObject.optJSONObject("return_session"));
					 }else {
						 request.getSession().removeAttribute("ext_session");
					 }
//					 System.out.println("=ext_session="+request.getSession().getAttribute("ext_session"));
					 Map<String, Object> mp =  createSuccessResult(jsonObject.opt("return_content"),jsonObject.optString("return_msg"));
					  return mp;
				 } else {
					 return createSuccessResult(jsonObject);
				 }
			 } else {
				 return createErrorResult(PropertiesMsg.getProperty("custom-ReturnIsNULL"));
			 }
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.debug("==定制服务结束>>csGetFees==##tip:"+tip);
		return createErrorResult(PropertiesMsg.getProperty("comm-Exception"));
	}
	/**
	 * 统一定制业务接口：自定义收费类型订单支付
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/csSetFees")
	public Map<String, Object> csSetFees(HttpServletRequest request) {
		Long tip = (new Date()).getTime();
		Map<String, String> result = new HashMap<String, String>();
		String msg = "";
		boolean flag = true;
		FreePo freePo = null;
		try{
			logger.debug("==定制服务开始>>csSetFees==##tip:"+tip);
			if(request.getSession()==null) {
				return createErrorResult("2", PropertiesMsg.getProperty("comm-SysSessionExpire"));
			}
			String openId = (String)request.getSession().getAttribute("wxOpenId");
			String orgId = (String)request.getSession().getAttribute("orgId");
			if(StringUtils.isBlank(openId)) {
				return createErrorResult("2", PropertiesMsg.getProperty("comm-WxSessionExpire"));
			}
			if(StringUtils.isBlank(orgId)) {
				return createErrorResult(PropertiesMsg.getProperty("comm-OrgIdIsNULL"));
			}
			String dns = (String)request.getSession().getAttribute("dns");
			String ip = request.getRemoteAddr(); //终端IP
			SysOrgInfo orgInfo = sysOrgServicesImp.getOrgInfoByIdForEnable(orgId);
			if(orgInfo==null) { //学校不存在或已禁用！
				return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
			}
			String business = request.getParameter("business"); //定制业务类型
			if (StringUtils.isBlank(business)) {
				return createErrorResult(PropertiesMsg.getProperty("custom-BusSerIsNothing"));
			}
			String feeTypeCode = this.getString(request, "feeTypeCode", "");
			String feeTypeName = this.getString(request, "feeTypeName", "");
			String amount = this.getString(request, "amount", "");
			String productName = this.getString(request, "productName", "");
			if (StringUtils.isBlank(feeTypeCode) || StringUtils.isBlank(feeTypeName) || !NumberUtils.isNumber(amount) || StringUtils.isBlank(productName)) {
				return createErrorResult("参数异常!");
			}
			SysPayType payType = payTypeServices.findPayTypeByOrgCode(orgId, feeTypeCode, feeTypeName);
			if(payType==null) {
				return createErrorResult("定制费用类型异常!");
			}
			String payTypeId = payType.getId();
			
			//生成FreePo
			freePo = new FreePo();
			double payAmount = Math.round(Double.valueOf(amount)*100)/100.0;
			String id = StringUtils.randomUUID();
			freePo.setId(id);
			freePo.setOrderNo(id);
			freePo.setPayTypeId(payTypeId);
			freePo.setPayTypeName(feeTypeName);
			freePo.setMoney(payAmount);
			freePo.setPayMoney(payAmount);
			freePo.setState(2);
			freePo.setCreateTime(new Date());
			if (productName.length() > 128) {
				freePo.setProductName(productName.substring(0, 120) + "...");
			}else{
				freePo.setProductName(productName);
			}
			freePo.setOrgId(orgId);
			freePo.setOpenId(openId);
			Object ext_session = request.getSession().getAttribute("ext_session");
			if(ext_session!=null) {
				JSONObject jsonExtSession = JSONObject.fromObject(ext_session);
				freePo.setStudentId(jsonExtSession.optString("custid")); //ext_session标准项，用户ID（学生ID）
				freePo.setStudentCode(jsonExtSession.optString("custno")); //ext_session标准项，用户编号（学生学号）
				freePo.setStudentName(jsonExtSession.optString("custname")); //ext_session标准项，用户名称（学生姓名）
			}
			
			Map<String, String> params = setParams(orgInfo, openId, dns, freePo.getId(), amount, ip, freePo.getProductName());
			String response = HttpClientUtils.post(params, WXPayConstants.WX_PREPAY_URL);
			if (StringUtils.isBlank(response)) {
				return createErrorResult("请求异常！");
			}
			response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
			ResponseModel model = (ResponseModel) HttpClientUtils.getObjectFromXML(response, ResponseModel.class);
//			//***DEBUG***
//			model.setReturn_code(WXPayConstants.SUCCESS);
//			model.setResult_code(WXPayConstants.SUCCESS);
//System.out.println("===="+model.getReturn_code()+"++++"+model.getResult_code());
//			//***DEBUG***
			logger.debug("==定制服务执行>>csSetFees==##tip:"+tip+"##Return_code:"+model.getReturn_code()+"##Result_code:"+model.getResult_code()+"##Err_code_des:"+model.getErr_code_des());
			if (model != null) {
				if (WXPayConstants.SUCCESS.equalsIgnoreCase(model.getReturn_code())) {
					result.put("appId", model.getAppid());
					Date now = CalendarUtils.parseDate(CalendarUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")); 
					Date mydate= CalendarUtils.parseDate(CalendarUtils.format("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
					long  longTime = (now.getTime() - mydate.getTime());
					result.put("timeStamp", (longTime / 1000) +"");
					result.put("nonceStr", params.get("nonce_str"));
					if (WXPayConstants.SUCCESS.equalsIgnoreCase(model.getResult_code())) {
						result.put("package", "prepay_id=" + model.getPrepay_id());
						freePo.setWxprepayId(model.getPrepay_id());
						freePo.setWxprepayCreateTime(new Date());
						//创建订单
						JSONObject jsonMsg = new JSONObject();
						jsonMsg.put("msg", null);
						if(customServices.csSetFees(payType,freePo,request,jsonMsg)) {
							result.put("signType", "MD5");
							result.put("paySign", HttpClientUtils.sign(HttpClientUtils.CHARSET, WXPayConstants.WX_MCHKEY, result, false));
						} else {
							msg = jsonMsg.optString("msg");
							flag = false;
						}
					}else{
						flag = false;
						msg = model.getErr_code_des();
					}
				}else{
					flag = false;
					msg = model.getReturn_msg();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			msg = e.getMessage();
			flag = false;
		}
		if(!flag) {
			return createErrorResult(msg);
		}
		result.put("orderId", freePo.getId());
		logger.debug("==定制服务结束>>csSetFees==##tip:"+tip);
		return createSuccessResult(result, msg);
	}
//	@RequestMapping(value="/csSetBO")
//	public Map<String, Object> csSetBO(HttpServletRequest request) {
//		return csSetFees(request);
//	}

	
	
	/**
	 * 订单支付
	 * @param request
	 * @param detailedId
	 * @param amount
	 * @return
	 */
	@RequestMapping(value="/save")
	public Map<String, Object> saveOrder(HttpServletRequest request,
			@RequestParam(value="detailedId", defaultValue = "") String detailedId,
			@RequestParam(value="amount", defaultValue = "") String amount){
		Long tip = (new Date()).getTime();
		logger.debug("==订单支付开始>>saveOrder==##tip:"+tip);
		boolean flag = true;
		String msg = "";
		FreePo freePo = null;
		Map<String, String> result = new HashMap<String, String>();
		try{
			if(request.getSession()==null) {
				return createErrorResult("2", PropertiesMsg.getProperty("comm-SysSessionExpire"));
			}
			String openId = (String)request.getSession().getAttribute("wxOpenId");
			String orgId = (String)request.getSession().getAttribute("orgId");
			if(StringUtils.isBlank(openId)) {
				return createErrorResult("2", PropertiesMsg.getProperty("comm-WxSessionExpire"));
			}
			if(StringUtils.isBlank(orgId)) {
				return createErrorResult(PropertiesMsg.getProperty("comm-OrgIdIsNULL"));
			}
			if (StringUtils.isBlank(detailedId) || StringUtils.isBlank(amount)) {
				return createErrorResult("参数异常!");
			}
			freePo = new FreePo();
			String dns = (String)request.getSession().getAttribute("dns");
			String ip = request.getRemoteAddr();//终端IP
			SysOrgInfo orgInfo = sysOrgServicesImp.getOrgInfoByIdForEnable(orgId);

			if (orgInfo == null) { //学校不存在或已禁用！
				return createErrorResult(PropertiesMsg.getProperty("comm-OrgIsNothing"));
			}
			freePo.setOrgId(orgId);
			freePo.setOpenId(openId);
			Map<String, Object> map = new HashMap<String, Object>();
			FreeCartDetailed freeCartDetailed = null;
			String[] detailedIds = detailedId.split(",");
			if (detailedIds != null && detailedIds.length > 1) {// 合并支付
				wxPayServicesImp.saveFreePoInfo(freePo, detailedIds, amount, map);
			}else{
				wxPayServicesImp.saveOrderInfo(freePo, detailedId, amount, freeCartDetailed);
				if (freeCartDetailed == null) {
					freeCartDetailed = new FreeCartDetailed();
				}
			}

			Map<String, String> params = setParams(orgInfo, openId, dns, freePo.getId(), amount, ip, freePo.getProductName());

			String response = HttpClientUtils.post(params, WXPayConstants.WX_PREPAY_URL);
			if (StringUtils.isBlank(response)) {
				return createErrorResult("请求异常！");
			}

			response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
			ResponseModel model = (ResponseModel) HttpClientUtils.getObjectFromXML(response, ResponseModel.class);

			logger.debug("==订单支付执行>>saveOrder==##tip:"+tip+"##Return_code:"+model.getReturn_code()+"##Result_code:"+model.getResult_code()+"##Err_code_des:"+model.getErr_code_des());
			if (model != null) {
				if (WXPayConstants.SUCCESS.equalsIgnoreCase(model.getReturn_code())) {
					result.put("appId", model.getAppid());
					Date now = CalendarUtils.parseDate(CalendarUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")); 
					Date mydate= CalendarUtils.parseDate(CalendarUtils.format("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
					long  longTime = (now.getTime() - mydate.getTime());
					result.put("timeStamp", (longTime / 1000) +"");
					result.put("nonceStr", params.get("nonce_str"));
					if (WXPayConstants.SUCCESS.equalsIgnoreCase(model.getResult_code())) {
						result.put("package", "prepay_id=" + model.getPrepay_id());
						freePo.setWxprepayId(model.getPrepay_id());
						freePo.setWxprepayCreateTime(new Date());
						wxPayServicesImp.addEntity(freePo);//创建订单
						if (freeCartDetailed != null && StringUtils.isNotBlank(freeCartDetailed.getId())) {
							freeCartDetailedServicesImp.update(freeCartDetailed);// 更新锁定金额
						}
						if (detailedIds != null && detailedIds.length > 0) {// 单项支付与合并支付
							for (int i=0;i< detailedIds.length;i++ ) {
								//新增缴费明细项
								wxPayServicesImp.addEntity(wxPayServicesImp.newFreePoItem(freePo, detailedIds[i], map));
							}
						}
						FreeWxPay freeWxPay = new FreeWxPay();
						freeWxPay.setFreePoId(freePo.getId());
						freeWxPay.setFreePoNo(freePo.getOrderNo());
						freeWxPay.setPayMoney(freePo.getPayMoney());
						freeWxPay.setState(1);
						freeWxPay.setPayMode(1);
						wxPayServicesImp.addEntity(freeWxPay);//创建微信订单
						result.put("signType", "MD5");
						result.put("paySign", HttpClientUtils.sign(HttpClientUtils.CHARSET, WXPayConstants.WX_MCHKEY, result, false));
					}else{
						flag = false;
						msg = model.getErr_code_des();
					}
				}else{
					flag = false;
					msg = model.getReturn_msg();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.debug(e.getMessage());
			msg = e.getMessage();
			flag = false;
		}
		if (!flag) {
			return createErrorResult(msg);
		}
		result.put("orderId", freePo.getId());
		logger.debug("==订单支付结束>>saveOrder==##tip:"+tip);
		return createSuccessResult(result, msg);
	}
	
	public Map<String, String> setParams(SysOrgInfo orgInfo, String openId, String dns, String orderId, String amount, String ip, String body) throws Exception{
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", WXPayConstants.WX_APPID);
		params.put("mch_id", WXPayConstants.WX_MCHID);
		if (orgInfo.getMchIs() != 2) {
			if (orgInfo.getMchIs() == 1) {
				params.put("sub_appid", orgInfo.getAppid());
			}
			params.put("sub_mch_id", orgInfo.getMchid());
		}
		params.put("device_info", "WEB");
		params.put("nonce_str", WXPayUtil.generateNonceStr());
		params.put("body", body);
		params.put("trade_type", WXPayConstants.TRADE_TYPE_JSAPI);
		params.put("notify_url", dns + WXPayConstants.NOTIFY_URL);
		params.put("out_trade_no", orderId);
		params.put("total_fee", Math.round((Double.valueOf(amount)*10000)/100.0)+"");
		params.put("spbill_create_ip", ip);
		if (orgInfo.getMchIs() != 2) {
			if (orgInfo.getMchIs() == 0) {//是否子商户号
				params.put("openid", openId);
			}else{
				params.put("sub_openid", openId);
			}
		}else{
			params.put("openid", openId);
		}
		params.put("sign_type", WXPayConstants.MD5);
		params.put("sign", WXPayUtil.generateSignature(params, WXPayConstants.WX_MCHKEY));
		return params;
	}
	
	
	/**
	 * 微信回调接口
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/notify")
	public void wxnotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String resXml = "";
        InputStream inStream;
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }

            logger.info("wxnotify:----start----");

            // 获取微信调用我们notify_url的返回信息
            String result = new String(outSteam.toByteArray(), "utf-8");
            logger.info("wxnotify:----result----=" + result);

            // 关闭流
            outSteam.close();
            inStream.close();

            // xml转换为map
            Map<String, String> resultMap = HttpClientUtils.xmlToMap(result, HttpClientUtils.CHARSET);
            boolean isSuccess = false;
            if (WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
               logger.info("wxnotify:----success");
                if (HttpClientUtils.validateSign(resultMap, WXPayConstants.WX_MCHKEY)) {
                	logger.info("wxnotify:----sign success");
                    // 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
                    resXml = resSuccessXml;
                    isSuccess = true;
                } else {
                	logger.error("wxnotify:----sign error----");
                }
            } else {
            	logger.error("wxnotify:fail,error info：" + resultMap.get("err_code_des"));
                resXml = resFailXml;
            }

            // 回调方法，处理业务 - 修改订单状态
            logger.info("wxnotify:update order ===>" + resultMap.get("out_trade_no"));
            FreePo freePo = freePoServicesImp.getEntityById(resultMap.get("out_trade_no"));
            if (isSuccess && freePo != null) {
            	FreeWxPay freeWxPay = wxPayServicesImp.getFreeWxPayByPoId(freePo.getId());
            	if (freeWxPay == null) {
            		freeWxPay = new FreeWxPay();
            	}
            	freeWxPay.setWxOrderNo(resultMap.get("transaction_id"));
            	freeWxPay.setPayMoney(freePo.getPayMoney());//交易金额
            	freeWxPay.setPayTime(new Date());//支付完成时间
//            	freeWxPay.setPayType(resultMap.get("trade_type"));//交易类型
//            	freeWxPay.setPayEquipment(resultMap.get("device_info"));//交易终端设备号
//            	freeWxPay.setPayBank(resultMap.get("bank_type"));//付款银行
//            	freeWxPay.setMoneyType(resultMap.get("fee_type"));//货币类型
//            	freeWxPay.setPayState("SUCCESS");
            	freeWxPay.setState(2);
            	freeWxPay.setOrgId(freePo.getOrgId());
            	freeWxPay.setOpenId(freePo.getOpenId());
            	wxPayServicesImp.updateEntity(freeWxPay);
            	wxPayServicesImp.updateMoneyAndState(freePo);
            	logger.info("wxnotify:update order successful");
            } else {
            	logger.error("wxnotify:update order failed");
            	resXml = resFailXml;
            }
        } catch (Exception e) {
        	logger.error("wxnotify: exception：", e);
        	resXml = resFailXml;
        } finally {
            try {
                // 处理业务完毕
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(resXml.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                logger.error("wxnotify:exception:out：", e);
            }
        }
	}
	
	
	/**
	 * 重新支付
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value="/payAgain")
	public Map<String, Object> payAgain(HttpServletRequest request, String orderId){
		logger.info("------ start ------ payAgain");
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		if (StringUtils.isBlank(orderId)) {
			return createErrorResult("订单不存在");
		}
		FreePo freePo = freePoServicesImp.getEntityById(orderId);
		if (freePo == null) {
			return createErrorResult("订单不存在");
		}
		Map<String, String> result = new HashMap<String, String>();
		String tradeState = wxPayServicesImp.getWxOrderInfo(freePo.getId(), freePo.getOrgId());
		if (WXPayConstants.CODE_NOTPAY.equalsIgnoreCase(tradeState) 
				&& (freePo.getState() == 1 || freePo.getState() ==2)) {
			result.put("appId", WXPayConstants.WX_APPID);
			Date now = CalendarUtils.parseDate(CalendarUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")); 
			Date mydate= CalendarUtils.parseDate(CalendarUtils.format("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
			long  longTime = (now.getTime() - mydate.getTime());
			result.put("timeStamp", (longTime / 1000) +"");
			result.put("nonceStr",  com.twi.base.util.StringUtils.randomUUID());
			result.put("package", "prepay_id=" + freePo.getWxprepayId());
			result.put("signType", "MD5");
			logger.info("------paySign-------:" + HttpClientUtils.sign(HttpClientUtils.CHARSET, WXPayConstants.WX_MCHKEY, result, false));
			result.put("paySign", HttpClientUtils.sign(HttpClientUtils.CHARSET, WXPayConstants.WX_MCHKEY, result, false));
		}else if (freePo.getState() == 3 
				&& WXPayConstants.SUCCESS.equalsIgnoreCase(tradeState)) {
			return createErrorResult("订单已做过支付");
		}else{
			return createErrorResult("订单已失效");
		}
		logger.info("------ end ------ payAgain");
		return createSuccessResult(result, "");
	}
	
	/**
	 * 取消支付
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value="/cancelPay")
	public Map<String, Object> cancelPay(HttpServletRequest request, String orderId){
		logger.info("------ start ------ cancelPay");
		Map<String, String> result = new HashMap<String, String>();
		if (request.getSession()== null || request.getSession().getAttribute("wxOpenId") == null) {
			return createErrorResult("2", "会话已过期");
		}
		if (StringUtils.isBlank(orderId)) {
			return createErrorResult("订单不存在");
		}
		// 付款中的订单
		FreePo freePo = freePoServicesImp.getFreePoById(orderId, null);
		if (freePo == null) {
			return createErrorResult("订单不存在");
		}
		if (freePo.getState() == 3) {
			return createErrorResult("订单已付款");
		}
		if (freePo.getState() != 2) {
			return createErrorResult("订单已失效");
		}
		if (!freePo.getOpenId().equalsIgnoreCase((String)request.getSession().getAttribute("wxOpenId"))) {
			return createErrorResult("无权限取消订单");
		}
		List<FreePo> freePos = new ArrayList<FreePo>();
		freePos.add(freePo);
		try {
			wxPayServicesImp.updateOrderState(freePos);
		} catch (Exception e) {
			return createErrorResult("取消失败");
		}
		logger.info("------ end ------ cancelPay");
		return createSuccessResult(result, "取消成功");
	}
}
