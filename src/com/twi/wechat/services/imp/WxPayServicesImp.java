package com.twi.wechat.services.imp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.exceptions.BusinessException;
import com.twi.base.util.CalendarUtils;
import com.twi.base.util.DataUtils;
import com.twi.base.util.HttpClientUtils;
import com.twi.base.util.StringUtils;
import com.twi.base.util.WXPayUtil;
import com.twi.common.constants.WXPayConstants;
import com.twi.common.domain.ResponseModel;
import com.twi.custom.services.CustomServices;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.freecartdetailed.services.FreeCartDetailedServices;
import com.twi.freepo.domain.FreePo;
import com.twi.freepo.domain.FreePoItem;
import com.twi.freepo.services.FreePoServices;
import com.twi.student.domain.SysStudent;
import com.twi.student.services.SysStudentServices;
import com.twi.syslog.domain.CheckErrLog;
import com.twi.syslog.domain.SysLog;
import com.twi.syslog.services.CheckErrLogServices;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.services.SysOrgServices;
import com.twi.wechat.domain.FreeDayTradingSum;
import com.twi.wechat.domain.FreeWxPay;
import com.twi.wechat.domain.FreeWxPayInfo;
import com.twi.wechat.services.WxPayServices;

@Service("wxPayServicesImp")
public class WxPayServicesImp extends BaseService implements WxPayServices {

	@Autowired
	private SysOrgServices sysOrgServicesImp;

	@Autowired
	private FreeCartDetailedServices freeCartDetailedServicesImp;

	@Autowired
	private SysStudentServices sysStudentServicesImp;

	@Autowired
	private FreePoServices freePoServicesImp;

	@Autowired
	private CheckErrLogServices checkErrLogServices;
	
	
	@Autowired
	private CustomServices customServices;

	@Override
	public void quartzDownloadBill() {
		List<SysOrgInfo> orgList = sysOrgServicesImp.getOrgInfoList();
		if (orgList != null && orgList.size() > 0) {
			for (SysOrgInfo org : orgList) {
				downloadBill(org, new Date());
			}
		}
	}

	public void downloadBill(SysOrgInfo org, Date date) {
		StopWatch watch = new StopWatch();
		watch.start();

		Date reDate = CalendarUtils.dateAddOrSub(date, Calendar.DAY_OF_MONTH, -1);
		String billDate = CalendarUtils.format(reDate, "yyyyMMdd");
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", WXPayConstants.WX_APPID);
		params.put("mch_id", WXPayConstants.WX_MCHID);
		if (org.getMchIs() != 2) {
			params.put("sub_mch_id", org.getMchid());
		}
		params.put("nonce_str", WXPayUtil.generateNonceStr());
		params.put("sign_type", WXPayConstants.MD5);
		params.put("bill_date", billDate);
		params.put("bill_type", "ALL");// 当日所有订单
		String description = "";
		try {
			params.put("sign", WXPayUtil.generateSignature(params, WXPayConstants.WX_MCHKEY));
			String result = HttpClientUtils.post(params, WXPayConstants.WX_DOWNLOADBILL);
			if (StringUtils.isNotEmpty(result) && !result.contains("error_code")) {
				logger.info("###########对账数据#############" + result);
				String[] str = result.split("\n");// 按行读取数据
				Map<String, Object> poIdMap = new HashMap<String, Object>();
				int len = str.length;
				for (int i = 0; i < len; i++) {
					String[] tradeDetailArray = str[i].replace("`", "").split(",");
					if (i > 0 && i < (len - 2)) {
						// 当日成功支付的订单
						// 明细行数据[交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券金额,商品名称,商户数据包,手续费,费率]
						// 当日所有订单
						// [交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,
						// 微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额，退款类型，退款状态,商品名称,商户数据包,手续费,费率]
						String poId = getArrayValue(tradeDetailArray, 6);
						poIdMap.put(poId, org.getId());
						FreeWxPay entity = this.getFreeWxPay(poId, org.getId());
						// 无支付单,有对账单
						if (entity == null) {
							CheckErrLog errLog = checkErrLogServices.getEntity(poId, org.getId());
							if (errLog == null) {
								errLog = new CheckErrLog();
								errLog.setWxOrderNo(getArrayValue(tradeDetailArray, 5));// 微信订单号
								errLog.setFreePoNo(poId);// 商户订单号
								errLog.setPayMoney(Double.valueOf(getArrayValue(tradeDetailArray, 12))); // 总金额
								errLog.setPayTime(CalendarUtils.parse(getArrayValue(tradeDetailArray, 0)));// 交易时间
								errLog.setErrCode(3);
								errLog.setCheckMode(1);
								errLog.setOrgId(org.getId());

								checkErrLogServices.add(errLog);
							}

							// 记录异常日志
							logger.error("无支付单，有对账单：" + poId);
						} else {
							// 有支付单
							FreePo freePo = freePoServicesImp.getEntityById(poId);
							if (freePo != null) {
								entity.setStudentName(freePo.getStudentName());
							}

							// 若订单状态1or3时，需更新实际缴费金额和锁定金额
							if (entity.getState() == 1 || entity.getState() == 3) {
								if (freePo != null) {
									logger.info("#######info#######微信订单状态为1或3,订单id为：" + freePo.getId());
									updateMoneyAndState(freePo);
								} else {
									logger.warn("######warn######没有此订单" + poId);
								}
							}
							// 验证支付单状态
							if (StringUtils.isNotEmpty(entity.getId()) && entity.getState() != 2) {
								entity.setState(2);// 已付款
								hibernateBaseDao.udpEntity(entity);
							}
						}

						// 微信对账单信息
						FreeWxPayInfo info = getFreeWxPayInfoByFreePoNo(poId, org.getId());

						// 有支付单，没对账信息，则添加
						if (info == null) {
							info = new FreeWxPayInfo();
							info.setPayTime(CalendarUtils.parse(getArrayValue(tradeDetailArray, 0)));// 交易时间
							info.setAppid(getArrayValue(tradeDetailArray, 1));// 公众账号ID
							info.setMid(getArrayValue(tradeDetailArray, 2));// 商户号
							info.setSubMid(getArrayValue(tradeDetailArray, 3));// 子商户号
							info.setPayEquipment(getArrayValue(tradeDetailArray, 4));// 设备号
							info.setWxOrderNo(getArrayValue(tradeDetailArray, 5));// 微信订单号
							info.setFreePoNo(poId);// 商户订单号
							info.setOpenId(getArrayValue(tradeDetailArray, 7));// 用户标识
							info.setPayType(getArrayValue(tradeDetailArray, 8));// 交易类型
							info.setPayState(getArrayValue(tradeDetailArray, 9));// 交易状态
							info.setPayBank(getArrayValue(tradeDetailArray, 10));// 付款银行
							info.setMoneyType(getArrayValue(tradeDetailArray, 11)); // 货币种类
							info.setPayMoney(Double.valueOf(getArrayValue(tradeDetailArray, 12))); // 总金额
							info.setpAmount(Double.valueOf(getArrayValue(tradeDetailArray, 13)));// 代金券金额
							info.setRefundWxOrderNo(getArrayValue(tradeDetailArray, 14));// 微信退款单号
							info.setRefundFreePoNo(getArrayValue(tradeDetailArray, 15));// 商户退款单号
							info.setRefundMoney(Double.valueOf(getArrayValue(tradeDetailArray, 16))); // 退款金额
							info.setRefundPAmount(Double.valueOf(getArrayValue(tradeDetailArray, 17)));// 代金券或立减优惠退款金额
							info.setRefundType(getArrayValue(tradeDetailArray, 18));// 退款类型
							info.setRefundState(getArrayValue(tradeDetailArray, 19));// 退款状态
							info.setProductName(getArrayValue(tradeDetailArray, 20));// 商品名称
							info.setPosData(getArrayValue(tradeDetailArray, 21));// 商户数据包
							info.setServiceMoney(Double.valueOf(getArrayValue(tradeDetailArray, 22)));// 手续费
							info.setRate(Double.valueOf(getArrayValue(tradeDetailArray, 23).split("%")[0]));// 费率

							if (entity != null) {
								info.setPayer(entity.getStudentName());// 付款方（学生名称）
								if (entity.getPayMoney() != Double.valueOf(getArrayValue(tradeDetailArray, 12))) {
									// 插入异常表
									CheckErrLog errLog = checkErrLogServices.getEntity(poId, org.getId());
									if (errLog == null) {
										errLog = new CheckErrLog();
										errLog.setWxOrderNo(getArrayValue(tradeDetailArray, 5));// 微信订单号
										errLog.setFreePoNo(poId);// 商户订单号
										errLog.setPayMoney(Double.valueOf(getArrayValue(tradeDetailArray, 12))); // 总金额
										errLog.setPayTime(CalendarUtils.parse(getArrayValue(tradeDetailArray, 0)));// 交易时间
										errLog.setErrCode(1);
										errLog.setCheckMode(1);
										errLog.setOrgId(org.getId());
										checkErrLogServices.add(errLog);
									}
									logger.error("支付单：" + poId + "与对账单-----金额不对！");
								}
							}
							info.setOrgId(org.getId());// 机构
							info.setCreateTime(new Date());
							//info.setFreeWxPayId(entity.getId());
							hibernateBaseDao.addEntity(info);
						}
					}

					// 汇总行数据 [总交易单数,总金额,退款总金额,充值券退款总金额,手续费总金额]
					// getArrayValue(tradeDetailArray, 3)// 充值券退款总金额
					// getArrayValue(tradeDetailArray, 5)// 订单总金额
					// getArrayValue(tradeDetailArray, 6)// 申请退款总金额
					if (i > (len - 2)) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("payWxNumber", getArrayValue(tradeDetailArray, 0));// 总交易单数
						map.put("sumWxMoney", getArrayValue(tradeDetailArray, 1));// 总金额
						map.put("refundWxMoney", getArrayValue(tradeDetailArray, 2));// 退款总金额
						map.put("serviceWxMoney", getArrayValue(tradeDetailArray, 4));// 手续费总金额
						addOrUpdWxTradingSum(reDate, org.getId(), map);
						// List<Map<String, Object>> listMap =
						// getSummaryList(reDate, org.getId());
						// if (listMap != null && listMap.size() > 0) {
						// for (Map<String, Object> map : listMap) {
						// String orgId = String.valueOf(map.get("org_id") ==
						// null ? "" : map.get("org_id"));
						// if (StringUtils.isEmpty(orgId)) {
						// continue;
						// }
						// map.put("payWxNumber",
						// getArrayValue(tradeDetailArray, 0));// 总交易单数
						// map.put("sumWxMoney", getArrayValue(tradeDetailArray,
						// 1));// 总金额
						// map.put("refundWxMoney",
						// getArrayValue(tradeDetailArray, 2));// 退款总金额
						// map.put("serviceWxMoney",
						// getArrayValue(tradeDetailArray, 4));// 手续费总金额
						// addOrUpdWxTradingSum(reDate, orgId, map);
						// }
						// }
					}
				}

				// 获取昨天支付单列表
				List<Map<String, Object>> wxPayMap = getYesterdayWxPay(reDate, org.getId());
				// 有支付单，校验是否存在对账单
				for (Map<String, Object> map : wxPayMap) {
					String poId = map.get("free_po_no").toString();
					// 不存在，插入对账信息，状态为异常，并记录日志
					if (!poIdMap.containsKey(poId) || !StringUtils.equals(poIdMap.get(poId).toString(), org.getId())) {
						CheckErrLog errLog = checkErrLogServices.getEntity(poId, org.getId());
						if (errLog == null) {
							errLog = new CheckErrLog();
							errLog.setWxOrderNo(map.get("wx_order_no").toString());// 微信订单号
							errLog.setFreePoNo(poId);// 商户订单号
							errLog.setPayMoney(Double.valueOf(map.get("pay_money").toString())); // 总金额
							errLog.setPayTime(CalendarUtils.parse(map.get("pay_time").toString()));// 交易时间
							errLog.setErrCode(2);
							errLog.setCheckMode(1);
							errLog.setOrgId(org.getId());
							checkErrLogServices.add(errLog);
						}
						logger.error("支付单号：" + poId + ",无对账单！");
					}
				}

			} else {
				logger.error("######error###########获取对账数据有误#################");
				logger.error("error-------返回对账数据为:" + result);

				try {
					result = new String(result.getBytes("ISO-8859-1"), "UTF-8");
					ResponseModel model = (ResponseModel) HttpClientUtils.getObjectFromXML(result, ResponseModel.class);
					if (model != null && "20002".equals(model.getError_code())
							&& "No Bill Exist".equalsIgnoreCase(model.getReturn_msg())) {
						logger.error("#######info########账单不存在");
						addOrUpdWxTradingSum(reDate, org.getId(), new HashMap<String, Object>());
					} else if (model != null && !"20002".equals(model.getError_code())) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("errCode", model.getError_code() + ":" + model.getReturn_msg());
						map.put("wxState", -1); // 异常状态
						addOrUpdWxTradingSum(reDate, org.getId(), map);

						description = String.format("对账日期：%s，微信返回数据：%s", billDate, result);
						hibernateBaseDao.addEntity(newSysLog("SYS_ERR_001", description, org.getId()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("########debug################" + e.getMessage());
		}
		watch.stop();
		logger.info("################下载时长#########################" + watch.getTime());
	}

	public List<Map<String, Object>> getYesterdayWxPay(Date reDate, String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select * from free_wx_pay WHERE state_ = 2 ");

		if (reDate != null) {// 支付时间
			sql.append(" and pay_time >=? ");
			list.add(CalendarUtils.dayBegin(reDate));
		}
		if (reDate != null) {// 支付时间
			sql.append(" and pay_time <=? ");
			list.add(CalendarUtils.dayEnd(reDate));
		}
		if (StringUtils.isNotEmpty(orgId)) {
			sql.append(" and org_id = ? ");
			list.add(orgId);
		}
		return jdbcDao.queryForMap(sql.toString(), list.toArray());
	}

	public String getArrayValue(String[] tradeDetailArray, int index) {
		try {
			return tradeDetailArray[index];
		} catch (Exception e) {
			return "";
		}
	}

	public boolean batchAddFreeWxPay(List<FreeWxPay> freeWxPayList) {
		boolean flag = true;
		try {
			if (freeWxPayList != null && freeWxPayList.size() > 0) {
				for (FreeWxPay freeWxPay : freeWxPayList) {
					addFreeWxPay(freeWxPay);
				}
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public SysLog newSysLog(String type, String description, String orgId) {
		SysLog log = new SysLog();
		log.setType(type);
		log.setDescription(description);
		log.setOrgId(orgId);
		log.setCreateTime(new Date());
		return log;
	}

	public boolean addFreeWxPay(FreeWxPay freeWxPay) {
		return hibernateBaseDao.addEntity(freeWxPay);
	}

	@Override
	public boolean addWxTradeSummary(FreeDayTradingSum freeDayTradingSum) {
		return hibernateBaseDao.addEntity(freeDayTradingSum);
	}

	@Override
	public Page<FreeWxPayInfo> getEveryDayBillPage(Page<FreeWxPayInfo> page, String orgId, String freePoNo,
			String studentName, String startDate, String endDate, String studentCode, String payTypeId,
			boolean payTypeFlag) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select f.wx_order_no as wxOrderNo,f.free_po_no as freePoNo,"
				+ "f.pay_money as payMoney,f.pay_time as payTime,f.pay_type as payType,"
				+ "f.pay_Bank as payBank,f.money_type as moneyType,f.Service_money as serviceMoney,"
				+ "f.rate_ as rate,f.user_Iden as openId," + "f.payer_ as payer,f.product_name as productName");
		sql.append(" from free_wx_pay_info f where 1 = 1 ");
		if (StringUtils.isNotBlank(freePoNo)) {// 商户订单号
			sql.append(" and f.free_po_no like ? ");
			list.add("%" + freePoNo + "%");
		}
		// if (StringUtils.isNotBlank(studentCode)) {// 学号
		// sql.append(" and fo.Student_code like ? ");
		// list.add("%" + studentCode + "%");
		// }
		if (StringUtils.isNotBlank(studentName)) {// 学生姓名（付款方）
			sql.append(" and f.payer_ like ? ");
			list.add("%" + studentName + "%");
		}
		// if (StringUtils.isNotBlank(payTypeId)) {// 缴费类型
		// sql.append(" and fo.pay_type_id like ? ");
		// list.add("%"+payTypeId+"%");
		// }
		if (StringUtils.isNotBlank(startDate)) {// 交易时间
			sql.append(" and f.pay_time >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {// 交易时间
			sql.append(" and f.pay_time <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			sql.append(" and f.org_id = ? ");
			list.add(orgId);
		}

		// if (payTypeFlag) {
		// sql.append(" group by fo.pay_type_id ");
		// } else {
		sql.append(" group by f.wx_order_no ");
		// }

		sql.append(" order by f.pay_time desc ");

		RowMapper<FreeWxPayInfo> rm = ParameterizedBeanPropertyRowMapper.newInstance(FreeWxPayInfo.class);

		return jdbcDao.queryForPageList(page, rm, sql.toString(), list.toArray());
	}

	@Override
	public Page<FreeDayTradingSum> getEveryDaySummaryPage(Page<FreeDayTradingSum> page, String orgId, String numFlag,
			String payWxNumber, String amountFlag, String sumWxMoney, String startDate, String endDate) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreeDayTradingSum f where 1=1 ");
		if (StringUtils.isNotBlank(payWxNumber)) {// 交易笔数
			hql.append(" and f.payWxNumber " + numFlag + " ? ");
			list.add(Integer.valueOf(payWxNumber).intValue());
		}
		if (StringUtils.isNotBlank(sumWxMoney)) {// 交易金额
			hql.append(" and f.sumWxMoney " + amountFlag + " ? ");
			list.add(Double.valueOf(sumWxMoney));
		}
		if (StringUtils.isNotBlank(startDate)) {// 对账日期
			hql.append(" and f.reDate >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {// 对账日期
			hql.append(" and f.reDate <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			hql.append(" and f.orgId = ? ");
			list.add(orgId);
		}

		// hql.append(" group by f.createTime ");

		hql.append(" order by f.reDate desc ");

		return hibernateBaseDao.findPage(page, hql.toString(), list.toArray());
	}

	@Override
	public List<Map<String, Object>> getEveryDaySummaryList(String orgId, String numFlag, String payWxNumber,
			String amountFlag, String sumWxMoney, String startDate, String endDate) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(
				" select f.pay_wx_number,f.sum_wx_money,f.pay_number,f.sum_money,f.refund_wx_money,f.Service_wx_money,"
						+ "(case when f.check_wx_state = 0 then '未对账' else '已对账' end) as check_wx_state,"
						+ "DATE_FORMAT(f.re_date,'%Y-%m-%d') as re_date, "
						+ "DATE_FORMAT(f.creater_time,'%Y-%m-%d') as creater_time "
						+ " from free_day_trading_sum f where 1=1 ");
		if (StringUtils.isNotBlank(payWxNumber)) {// 交易笔数
			sql.append(" and f.pay_wx_number " + numFlag + " ? ");
			list.add(payWxNumber);
		}
		if (StringUtils.isNotBlank(sumWxMoney)) {// 交易金额
			sql.append(" and f.sum_wx_money " + amountFlag + " ? ");
			list.add(sumWxMoney);
		}
		if (StringUtils.isNotBlank(startDate)) {// 对账日期
			sql.append(" and f.re_date >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {// 对账日期
			sql.append(" and f.re_date <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			sql.append(" and f.org_id = ? ");
			list.add(orgId);
		} else {// 无数据导出
			sql.append(" and f.org_id = '11111111' ");
		}

		sql.append(" order by f.re_date desc ");

		return jdbcDao.queryForMap(sql.toString(), list.toArray());
	}

	@Override
	public synchronized void saveOrderInfo(FreePo freePo, String detailedId, String amount, FreeCartDetailed freeCartDetailed) {
		double payAmount = Math.round(Double.valueOf(amount) * 100) / 100.0;
		freeCartDetailed = freeCartDetailedServicesImp.getEntityById(detailedId);
		if (freeCartDetailed == null) {
			throw new BusinessException("缴费单不存在!");
		}
		// 未加payAmount的锁定金额
		if (DataUtils.subtract(freeCartDetailed.getMoney(), freeCartDetailed.getOrderMoney(), 2) < payAmount) {
			throw new BusinessException("实际缴费超过应缴金额，请查看是否有遗漏支付的订单!");
		}
		// 加上payAmount的锁定金额
		freeCartDetailed.setOrderMoney(DataUtils.add(freeCartDetailed.getOrderMoney(), payAmount, 2));

		if (DataUtils.subtract(freeCartDetailed.getOrderMoney(), freeCartDetailed.getMoney(), 2) > 0) {
			throw new BusinessException("实际缴费超过应缴金额，请查看是否有遗漏支付的订单!");
		}
		//freeCartDetailedServicesImp.update(freeCartDetailed);// 更新锁定金额

		SysStudent student = sysStudentServicesImp.getStudentById(freeCartDetailed.getStudentId());
		if (student == null) {
			throw new BusinessException("学生档案不存在!");
		}
		String id = com.twi.base.util.StringUtils.randomUUID();
		freePo.setId(id);
		freePo.setOrderNo(id);
		freePo.setStudentId(student.getId());
		freePo.setStudentCode(student.getCode());
		freePo.setStudentName(student.getName());
		String productName = student.getCode() + "-" + student.getName() + "-" + freeCartDetailed.getPayTypeName();
		if (productName.length() > 128) {
			freePo.setProductName(productName.substring(0, 120) + "...");
		}else{
			freePo.setProductName(productName);
		}
		freePo.setPayTypeId(freeCartDetailed.getPayTypeId());
		freePo.setPayTypeName(freeCartDetailed.getPayTypeName());
		freePo.setMoney(freeCartDetailed.getMoney());
		freePo.setPayMoney(payAmount);
		freePo.setState(2);
		freePo.setCreateTime(new Date());
	}

	@Override
	public synchronized void saveFreePoInfo(FreePo freePo, String[] detailedIds, String amount, Map<String, Object> map) {
		double payAmount = Math.round(Double.valueOf(amount) * 100) / 100.0;
		double needAmount = 0;// 应缴金额
		double ableAmount = 0;// 可缴金额
		
		String payTypeId = "";// 缴费类型ID
		String payTypeName = ""; // 缴费类型名称
		SysStudent student = null;
		List<FreeCartDetailed> detailedList = this.getFreeCartDetailedList(detailedIds);
		if (detailedList != null && detailedList.size() > 0) {
			for (FreeCartDetailed detailed : detailedList) {
				// 对应每一项的实际支付可金额
				map.put(detailed.getId(), DataUtils.subtract(detailed.getMoney(), detailed.getOrderMoney(), 2));
				
				needAmount += detailed.getMoney();
				ableAmount = DataUtils.add(ableAmount, DataUtils.subtract(detailed.getMoney(), detailed.getOrderMoney(), 2), 2);
				
				payTypeId += detailed.getPayTypeId() + ",";
				payTypeName += detailed.getPayTypeName() + ",";
			}
			student = sysStudentServicesImp.getStudentById(detailedList.get(0).getStudentId());
		}else{
			throw new BusinessException("缴费单不存在!");
		}
		if (ableAmount != payAmount) {
			throw new BusinessException("可缴金额超过需缴费金额，请查看是否有遗漏支付的订单!");
		}
		if (student == null) {
			throw new BusinessException("学生档案不存在!");
		}
		String id = com.twi.base.util.StringUtils.randomUUID();
		freePo.setId(id);
		freePo.setOrderNo(id);
		freePo.setStudentId(student.getId());
		freePo.setStudentCode(student.getCode());
		freePo.setStudentName(student.getName());
		String productName = student.getCode() + "-" + student.getName() + "-" + payTypeName.substring(0, payTypeName.length() - 1);
		try {
			if (productName.getBytes("UTF-8").length > 128) {// 商品名称长度128字节
				freePo.setProductName(productName.substring(0, 40) + "...");
			}else{
				freePo.setProductName(productName);
			}
		} catch (Exception e) {
			throw new BusinessException("商品名称长度有误");
		}
		freePo.setPayTypeId(payTypeId.substring(0, payTypeId.length() - 1));
		freePo.setPayTypeName(payTypeName.substring(0, payTypeName.length() - 1));
		freePo.setMoney(Math.round(Double.valueOf(needAmount) * 100) / 100.0);
		freePo.setPayMoney(payAmount);
		freePo.setState(2);
		freePo.setCreateTime(new Date());
	}
	
	@Override
	public FreePoItem newFreePoItem(FreePo freePo, String detailedId, Map<String, Object> map){
		FreePoItem item = new FreePoItem();
		item.setId(java.util.UUID.randomUUID().toString());
		item.setFreePoId(freePo.getId());
		item.setFreeCartDetailedId(detailedId);
		FreeCartDetailed detailed = freeCartDetailedServicesImp.getEntityById(detailedId);
		if (detailed != null && !map.isEmpty() && map.containsKey(detailedId)) {// 合并支付
			item.setPayTypeId(detailed.getPayTypeId());
			item.setPayTypeName(detailed.getPayTypeName());
			item.setMoney(detailed.getMoney());
			item.setPayMoney(Math.round(Double.valueOf(map.get(detailedId).toString()) * 100) / 100.0);
			// 增加锁定金额
			detailed.setOrderMoney(DataUtils.add(item.getPayMoney(), detailed.getOrderMoney(), 2));
			freeCartDetailedServicesImp.update(detailed);// 更新锁定金额
		}else{// 单项支付
			if (StringUtils.isNotEmpty(freePo.getPayTypeId()) && freePo.getPayTypeId().length() <= 36) {
				item.setPayTypeId(freePo.getPayTypeId());
				item.setPayTypeName(freePo.getPayTypeName());
			}
			item.setMoney(freePo.getMoney());
			item.setPayMoney(freePo.getPayMoney());
		}
		item.setOrgId(freePo.getOrgId());
		item.setOpenId(freePo.getOpenId());
		item.setCreateTime(new Date());
		item.setState(1);
		return item;
	}
	
	public List<FreeCartDetailed> getFreeCartDetailedList(String[] detailedIds) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreeCartDetailed f where 1=1 ");

		if (detailedIds != null && detailedIds.length > 0) {// 缴费明细ID
			for (int i = 0; i< detailedIds.length; i++) {
				if (i == 0) {
					hql.append(" and ( f.id = ? ");
				}else{
					hql.append(" or f.id = ? ");
				}
				list.add(detailedIds[i]);
			}
			hql.append(" )");
		}

		return (List<FreeCartDetailed>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
	}
	
	@Override
	public boolean updateEntity(FreePo freePo) {
		return hibernateBaseDao.udpEntity(freePo);
	}

	@Override
	public boolean updateEntity(FreeWxPay freeWxPay) {
		return hibernateBaseDao.udpEntity(freeWxPay);
	}

	@Override
	public boolean addEntity(FreePo freePo) {
		return hibernateBaseDao.addEntity(freePo);
	}

	@Override
	public boolean addEntity(FreeWxPay freeWxPay) {
		return hibernateBaseDao.addEntity(freeWxPay);
	}

	@Override
	public FreeWxPay getEntityById(String id) {
		return hibernateBaseDao.getEntity(FreeWxPay.class, id);
	}

	@Override
	public boolean addEntity(FreePoItem item) {
		return hibernateBaseDao.addEntity(item);
	}

	@Override
	public FreeWxPay getFreeWxPayByPoId(String freePoId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreeWxPay f where 1=1");

		if (StringUtils.isNotBlank(freePoId)) {// 订单id
			hql.append(" and f.freePoId = ? ");
			list.add(freePoId);
		}

		List<FreeWxPay> dealList = (List<FreeWxPay>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
		if (dealList != null && dealList.size() > 0) {
			return dealList.get(0);
		}
		return null;
	}

	@Override
	public FreeWxPay getFreeWxPay(String freePoId, String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreeWxPay f where 1=1");

		if (StringUtils.isNotBlank(freePoId)) {// 订单id
			hql.append(" and f.freePoId = ? ");
			list.add(freePoId);
		}

		if (StringUtils.isNotBlank(orgId)) {// 订单id
			hql.append(" and f.orgId = ? ");
			list.add(orgId);
		}

		List<FreeWxPay> dealList = (List<FreeWxPay>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
		if (dealList != null && dealList.size() > 0) {
			return dealList.get(0);
		}
		return null;
	}

	@Override
	public boolean updateMoneyAndState(FreePo freePo) {
		boolean flag = false;
		try {
			freePo.setState(3);// 已付款
			this.updateEntity(freePo);// 更新订单状态

			List<FreePoItem> itemList = this.getFreePoItemByPoId(freePo.getId());
			if (itemList != null && itemList.size() > 0) {// 合并订单时，修改多条数据
				for(FreePoItem item : itemList) {
					if(StringUtils.trimToEmpty(item.getFreeCartDetailedId()).equals("")) { //非标准收费项目，无缴费明细
						item.setState(2);
						this.hibernateBaseDao.udpEntity(item);
//***DEBUG***
//System.out.println("==updateMoneyAndState==item=="+item.getFreePoId());
					} else { //标准收费项目
						updateFreeCartDetailed(item.getPayMoney(), item.getId());
					}
				}
			}
		} catch (Exception e) {
			logger.debug("---debug--- 数据更新失败！");
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	@Override
	public List<FreePoItem> getFreePoItemByPoId(String freePoId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreePoItem i where 1=1");

		if (StringUtils.isNotBlank(freePoId)) {// 订单id
			hql.append(" and i.freePoId = ? ");
			list.add(freePoId);
		}

		return (List<FreePoItem>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
	}

	public boolean updateFreeCartDetailed(double payMoney, String itemId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		// 更新缴费明细金额 + 状态
		sql.append(" UPDATE free_cart_detailed d ");
		sql.append(" INNER JOIN free_po_item i ON i.free_Cart_Detailed_id = d.id_ ");
		sql.append(
				" SET d.pay_money = convert((d.pay_money + ?),decimal(10,2)),d.less_money = convert((d.money_ - (d.pay_money + ?)),decimal(10,2)),i.state_ = 2,d.state_ =  ");
		sql.append(" (case when convert((d.pay_money + ? - d.money_),decimal(10,2)) >= 0 then 4 else 3 end) ");
		sql.append(" where i.id_ = ? ");
		list.add(DataUtils.format(payMoney, 2));
		list.add(DataUtils.format(payMoney, 2));
		list.add(DataUtils.format(payMoney, 2));
		list.add(itemId);
		return jdbcDao.executeSql(sql.toString(), list.toArray());
	}

	@Override
	public String getWxOrderInfo(String orderId, String orgId) {
		Map<String, String> params = new HashMap<String, String>();
		SysOrgInfo orgInfo = sysOrgServicesImp.getOrgInfoById(orgId);
		if (orgInfo == null) {
			orgInfo = new SysOrgInfo();
		}
		params.put("appid", WXPayConstants.WX_APPID);
		params.put("mch_id", WXPayConstants.WX_MCHID);
		if (orgInfo.getMchIs() != 2) {
			if (orgInfo.getMchIs() == 1) {
				params.put("sub_appid", orgInfo.getAppid());
			}
			params.put("sub_mch_id", orgInfo.getMchid());
		}
		params.put("out_trade_no", orderId);
		params.put("nonce_str", com.twi.base.util.StringUtils.randomUUID());
		params.put("sign_type", "MD5");
		params.put("sign", HttpClientUtils.sign(HttpClientUtils.CHARSET, WXPayConstants.WX_MCHKEY, params, false));
		try {
			String response = HttpClientUtils.post(params, WXPayConstants.WX_ORDERQUERY_URL);
			if (StringUtils.isBlank(response)) {
				throw new BusinessException("error:wx请求异常！");
			}
			response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
			ResponseModel model = (ResponseModel) HttpClientUtils.getObjectFromXML(response, ResponseModel.class);

			logger.info("code:" + model.getReturn_code() + "------msg:" + model.getReturn_msg());

			if (model != null) {
				if (WXPayConstants.SUCCESS.equalsIgnoreCase(model.getReturn_code())
						&& WXPayConstants.SUCCESS.equalsIgnoreCase(model.getResult_code())) {
					logger.info("-----trade_state------:" + model.getTrade_state());
					// 微信订单支付状态
					return model.getTrade_state();
				}
			}
		} catch (Exception e) {
			logger.error("error:" + e.getMessage());
			e.printStackTrace();
		}
		return WXPayConstants.SUCCESS;
	}

	public synchronized void quartzUpdateOrderState() {
		try {
			String hql = "FROM FreePo WHERE TIMESTAMPDIFF(MINUTE,wxprepayCreateTime,NOW()) >= 15 and state = 2 ";
			List<FreePo> freePos = (List<FreePo>) hibernateBaseDao.getEntityList(hql, null);
			this.updateOrderState(freePos);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("-------error:" + e.getMessage());
		}
	}

	@Override
	public synchronized void updateOrderState(List<FreePo> freePos){
		try {
			String freePoIds = "";
			if (freePos != null && freePos.size() > 0) {
				for (FreePo freePo : freePos) {
					String tradeState = this.getWxOrderInfo(freePo.getId(), freePo.getOrgId());
					if (!WXPayConstants.SUCCESS.equalsIgnoreCase(tradeState)) {// 微信
																				// 订单未支付
						freePoIds += ",'" + freePo.getId() + "'";

						List<FreePoItem> itemList = this.getFreePoItemByPoId(freePo.getId());
						if (itemList != null && itemList.size() > 0) {
							for (FreePoItem item : itemList) {// 更新缴费明细项状态和缴费明细的锁定金额还原
								if(StringUtils.trimToEmpty(item.getFreeCartDetailedId()).equals("")) { //非标准收费项目，无缴费明细
									if(item.getState()==1) {
										item.setState(3);
										this.hibernateBaseDao.udpEntity(item);
									}
								} else { //标准收费项目
									boolean flag = updateOrderMoneyAndState(item);
									if (!flag) {
										logger.error("----error: 数据库异常!;缴费明细项ID：" + item.getId());
									}
								}
								
							}
						}
					}
				}
			}

			String sql = " UPDATE free_po set state_ = 4 WHERE state_ = 2 and id_ in ('' "
					+ freePoIds + ")";
			boolean flag = jdbcDao.executeSql(sql, null);
			if (!flag) {
				logger.error("----error: 数据异常!");
			} else {
				String sql1 = " UPDATE free_wx_pay set state_ = 3 WHERE state_ = 1 and free_po_id in ('' " + freePoIds
						+ ")";
				jdbcDao.executeSql(sql1, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("-------error:" + e.getMessage());
		}
	}
	
	private boolean updateOrderMoneyAndState(FreePoItem item) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		// 更新缴费明细锁定金额 + 状态
		sql.append(" UPDATE free_cart_detailed d ");
		sql.append(" INNER JOIN free_po_item i ON i.free_Cart_Detailed_id = d.id_ ");
		sql.append(" SET d.order_money = convert ((d.order_money - ?), decimal(10,2)),i.state_ = 3  ");
		// sql.append(" ,d.state_ =(case when d.order_money - d.money_ >= 0 then
		// 4 else 3 end) ");
		sql.append(" where i.id_ = ? and i.state_ = 1 ");
		list.add(DataUtils.format(item.getPayMoney(), 2));
		list.add(item.getId());
		return jdbcDao.executeSql(sql.toString(), list.toArray());
	}

	@Override
	public void statisticsDealData() {
		List<SysOrgInfo> orgList = sysOrgServicesImp.getOrgInfoList();
		if (orgList != null && orgList.size() > 0) {
			for (SysOrgInfo org : orgList) {
				Date reDate = CalendarUtils.dateAddOrSub(new Date(), Calendar.DAY_OF_MONTH, -1);
				List<Map<String, Object>> listMap = getSummaryList(reDate, org.getId());
				if (listMap != null && listMap.size() > 0) {
					for (Map<String, Object> map : listMap) {
						String orgId = String.valueOf(map.get("org_id") == null ? "" : map.get("org_id"));
						if (StringUtils.isEmpty(orgId)) {
							continue;
						}
						addOrUpdTradingSum(reDate, orgId, map);
					}
				} else {
					addOrUpdTradingSum(reDate, org.getId(), new HashMap<String, Object>());
				}
			}
		}
	}

	@Override
	public List<Map<String, Object>> getSummaryList(Date now, String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		// sql.append(" insert into free_day_trading_sum
		// (pay_number,sum_money,org_id,re_date)");
		sql.append(" select count(1) as payNumber,sum(pay_money) as sumMoney,org_id,");
		sql.append(" DATE_FORMAT(pay_time,'%Y-%m-%d') as createDate ");
		sql.append(" from free_wx_pay WHERE state_ = 2 ");

		if (now != null) {// 支付时间
			sql.append(" and pay_time >=? ");
			list.add(CalendarUtils.dayBegin(now));
		}
		if (now != null) {// 支付时间
			sql.append(" and pay_time <=? ");
			list.add(CalendarUtils.dayEnd(now));
		}
		if (StringUtils.isNotEmpty(orgId)) {
			sql.append(" and org_id = ? ");
			list.add(orgId);
		}

		sql.append(" group by createDate,org_id ");
		return jdbcDao.queryForMap(sql.toString(), list.toArray());
	}

	@Override
	public FreeDayTradingSum getTradingSumByReDate(Date reDate, String orgId) {
		String hql = " from FreeDayTradingSum where reDate='" + CalendarUtils.format(reDate, "yyyy-MM-dd")
				+ "' and orgId=? ";
		return hibernateBaseDao.findUnique(hql, orgId);
	}

	@Override
	public boolean updWxTradeSummary(FreeDayTradingSum freeDayTradingSum) {
		return hibernateBaseDao.udpEntity(freeDayTradingSum);
	}

	@Override
	public void addOrUpdTradingSum(Date reDate, String orgId, Map<String, Object> map) {
		FreeDayTradingSum model = getTradingSumByReDate(reDate, orgId);
		if (model == null) {
			model = new FreeDayTradingSum();
			model.setCreateTime(new Date());
			model.setOrgId(orgId);
		}
		model.setPayNumber(Integer.valueOf(String.valueOf(map.get("payNumber") == null ? 0 : map.get("payNumber"))));
		model.setSumMoney(DataUtils
				.format(Double.valueOf(String.valueOf(map.get("sumMoney") == null ? 0 : map.get("sumMoney"))), 2));
		model.setReDate(reDate);
		if (StringUtils.isNotEmpty(model.getId())) {
			hibernateBaseDao.udpEntity(model);
		} else {
			hibernateBaseDao.addEntity(model);
		}
	}

	public void addOrUpdWxTradingSum(Date reDate, String orgId, Map<String, Object> map) {
		FreeDayTradingSum model = getTradingSumByReDate(reDate, orgId);
		if (model == null) {
			model = new FreeDayTradingSum();
			model.setCreateTime(new Date());
			model.setOrgId(orgId);
		}
		model.setCheckWxState(map.get("wxState") == null ? 1 : Integer.valueOf(map.get("wxState").toString()));
		model.setPayWxNumber(
				Integer.valueOf(String.valueOf(map.get("payWxNumber") == null ? 0 : map.get("payWxNumber"))));
		model.setSumWxMoney(DataUtils
				.format(Double.valueOf(String.valueOf(map.get("sumWxMoney") == null ? 0 : map.get("sumWxMoney"))), 2));
		model.setRefundWxMoney(DataUtils.format(
				Double.valueOf(String.valueOf(map.get("refundWxMoney") == null ? 0 : map.get("refundWxMoney"))), 2));
		model.setServiceWxMoney(DataUtils.format(
				Double.valueOf(String.valueOf(map.get("serviceWxMoney") == null ? 0 : map.get("serviceWxMoney"))), 2));
		model.setErrorWxMsg(map.get("errCode") == null ? "" : map.get("errCode").toString());
		model.setReDate(reDate);
		if (StringUtils.isNotEmpty(model.getId())) {
			hibernateBaseDao.udpEntity(model);
		} else {
			hibernateBaseDao.addEntity(model);
		}
	}

	public FreeWxPayInfo getFreeWxPayInfoByFreePoNo(String freePoNo, String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreeWxPayInfo f where 1=1 ");

		if (StringUtils.isNotBlank(freePoNo)) {// 商户订单号
			hql.append(" and f.freePoNo = ? ");
			list.add(freePoNo);
		}
		if (StringUtils.isNotBlank(orgId)) {// 商户订单号
			hql.append(" and f.orgId = ? ");
			list.add(orgId);
		}

		List<FreeWxPayInfo> dealList = (List<FreeWxPayInfo>) hibernateBaseDao.getEntityList(hql.toString(),
				list.toArray());
		if (dealList != null && dealList.size() > 0) {
			return dealList.get(0);
		}
		return null;
	}

	@Override
	public void checkPayDate() {
		List<SysOrgInfo> orgList = sysOrgServicesImp.getOrgInfoList();
		if (orgList != null && orgList.size() > 0) {
			for (SysOrgInfo org : orgList) {
				if (org.getNotOrderDay() == 0) {
					continue;
				}
				Date beginDate = CalendarUtils.dateAddOrSub(new Date(), Calendar.DAY_OF_MONTH, -org.getNotOrderDay());
				int count = getPayCount(beginDate, org.getId());
				if (count == 0) {
					// 保存异常
					String description = String.format("开始日期：%s 至今，连续 %s 天交易汇总为0", CalendarUtils.simpleFormat(beginDate),org.getNotOrderDay());
					hibernateBaseDao.addEntity(newSysLog("SYS_ERR_002", description, org.getId()));
				}
			}
		}

	}

	private int getPayCount(Date date, String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select SUM(pay_number) as paycount from free_day_trading_sum WHERE 1=1");

		if (date != null) {// 支付时间
			sql.append(" and re_date >=? ");
			list.add(CalendarUtils.dayBegin(date));
		}

		if (StringUtils.isNotEmpty(orgId)) {
			sql.append(" and org_id = ? ");
			list.add(orgId);
		}

		List<Map<String, Object>> pList = jdbcDao.queryForMap(sql.toString(), list.toArray());
		int count = 0;
		for (Map<String, Object> map : pList) {
			if (map != null) {
				count = Integer.valueOf(map.get("payCount").toString());
			}
		}
		return count;
	}

	
}
