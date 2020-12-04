package com.twi.freepo.services.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.freepo.domain.FreePo;
import com.twi.freepo.services.FreePoServices;
import com.twi.wechat.domain.FreeDayTradingSum;
import com.twi.wechat.domain.FreeWxPay;

@Service("freePoServicesImp")
public class FreePoServicesImp extends BaseService implements FreePoServices {

	@Override
	public Page<FreeWxPay> getDealPage(Page<FreeWxPay> page, String orgId, String studentCode, String studentName,
			String startDate, String endDate, String majorName, String className, String orderType, String payTypeId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select f.wx_order_no as wxOrderNo,f.free_po_no as freePoNo,f.free_po_id as freePoId "
					+ " ,sum(f.pay_money) as payMoney,max(f.pay_time) as payTime "
//					+ " ,f.pay_type as payType "
//					+ " ,f.pay_equipment as payEquipment,f.pay_state as payState "
//					+ " ,f.pay_Bank as payBank,f.money_type as moneyType,f.Service_money as serviceMoney,f.rate_ as rate "
//					+ " ,f.refund_state as refundState "
					+ " ,CASE f.pay_mode  WHEN 1 THEN '微信' WHEN 2 THEN '支付宝'  WHEN 3 THEN '现金'  WHEN 4 THEN '刷卡'  ELSE '' END payModeStr "
					+ " ,f.pay_mode as payMode ,f.state_ as state,f.openid_ as openId,f.org_id as orgId "
					+ " ,fo.Student_code as studentCode,fo.Student_name as studentName,fo.product_name as productName "
					+ " ,fo.pay_type_name as payTypeName,fo.pay_type_id as payTypeId "
					+ " ,s.major_name as majorName,s.clazz_name as className "
					+ " from free_wx_pay f,free_po fo "
					+ " left join sys_student_info s on s.id_ = fo.Student_id "
//					+ " ,sys_student_info s "
					+ " where fo.id_ = f.free_po_id "
//					+ " and s.id_ = fo.Student_id "
					);
			sql.append(" and f.state_ = 2 and fo.state_ = 3 ");
			if (StringUtils.isNotBlank(studentCode)) {// 学号
				sql.append(" and fo.Student_code like ? ");
				list.add("%" + studentCode + "%");
			}
			if (StringUtils.isNotBlank(studentName)) {// 学生姓名（付款方）
				sql.append(" and fo.Student_name like ? ");
				list.add("%" + studentName + "%");
			}
			if (StringUtils.isNotBlank(majorName)) {// 专业
				sql.append(" and s.major_name like ? ");
				list.add("%" + majorName + "%");
			}
			if (StringUtils.isNotBlank(className)) {// 班级
				sql.append(" and s.clazz_name like ? ");
				list.add("%" + className + "%");
			}
			if (StringUtils.isNotBlank(payTypeId)) {// 缴费类型
				sql.append(" and fo.pay_type_id like ? ");
				list.add("%" + payTypeId + "%");
			}
			if (StringUtils.isNotBlank(startDate)) {// 交易时间
				sql.append(" and f.pay_time >=? ");
				list.add(CalendarUtils.parseDate(startDate));
			}
			if (StringUtils.isNotBlank(endDate)) {//  交易时间
				sql.append(" and f.pay_time <=? ");
				list.add(CalendarUtils.dayEnd(endDate));
			}
			if (StringUtils.isNotBlank(orgId)) {// 机构ID
				sql.append(" and f.org_id = ? ");
				list.add(orgId);
			}

			sql.append(" group by f.id_ ");

			if ("date".equalsIgnoreCase(orderType)) {
				sql.append(" order by f.pay_time desc ");
			}else if ("studentCode".equalsIgnoreCase(orderType)) {
				sql.append(" order by fo.Student_code desc ");
			}else if ("studentName".equalsIgnoreCase(orderType)){
				sql.append(" order by fo.Student_name desc ");
			}


			RowMapper<FreeWxPay> rm = ParameterizedBeanPropertyRowMapper.newInstance(FreeWxPay.class);
			return jdbcDao.queryForPageList(page, rm, sql.toString(), list.toArray());
		} catch(Exception e) {
			e.printStackTrace();
		}	
		return page;
	}

	@Override
	public List<Map<String, Object>> getDealList(String orgId, String studentCode, String studentName, String startDate,
			String endDate, String majorName, String className, String orderType) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select f.wx_order_no as wxOrderNo,f.free_po_no as freePoNo,f.free_po_id as freePoId "
				+ " ,sum(f.pay_money) as payMoney,max(DATE_FORMAT(f.pay_time,'%Y-%m-%d')) as payTime "
//				+ " ,f.pay_type as payType "
//				+ " ,f.pay_equipment as payEquipment,f.pay_state as payState "
//				+ " ,f.pay_Bank as payBank,f.money_type as moneyType,f.Service_money as serviceMoney,f.rate_ as rate "
//				+ " ,f.refund_state as refundState "
				+ " ,CASE f.pay_mode  WHEN 1 THEN '微信' WHEN 2 THEN '支付宝'  WHEN 3 THEN '现金'  WHEN 4 THEN '刷卡'  ELSE '' END payModeStr "
				+ " ,f.pay_mode  as payMode,f.state_ as state,f.openid_ as openId,f.org_id as orgId "
				+ " ,fo.Student_code as studentCode,fo.Student_name as studentName,fo.product_name as productName "
				+ " ,fo.pay_type_name as payTypeName,fo.pay_type_id as payTypeId "
				+ " ,s.major_name as majorName,s.clazz_name as className "
				+ " from free_wx_pay f,free_po fo "
				+ " left join sys_student_info s on s.id_ = fo.Student_id "
//				+ " ,sys_student_info s "
				+ " where fo.id_ = f.free_po_id "
//				+ " and s.id_ = fo.Student_id "
				);
		sql.append(" and f.state_ = 2 and fo.state_ = 3 ");
		if (StringUtils.isNotBlank(studentCode)) {// 学号
			sql.append(" and fo.Student_code like ? ");
			list.add("%" + studentCode + "%");
		}
		if (StringUtils.isNotBlank(studentName)) {// 学生姓名（付款方）
			sql.append(" and fo.Student_name like ? ");
			list.add("%" + studentName + "%");
		}
		if (StringUtils.isNotBlank(majorName)) {// 专业
			sql.append(" and s.major_name like ? ");
			list.add("%" + majorName + "%");
		}
		if (StringUtils.isNotBlank(className)) {// 班级
			sql.append(" and s.clazz_name like ? ");
			list.add("%" + className + "%");
		}
		if (StringUtils.isNotBlank(startDate)) {// 交易时间
			sql.append(" and f.pay_time >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {//  交易时间
			sql.append(" and f.pay_time <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			sql.append(" and f.org_id = ? ");
			list.add(orgId);
		}

		sql.append(" group by f.id_ ");

		if ("date".equalsIgnoreCase(orderType)) {
			sql.append(" order by f.pay_time desc ");
		}else if ("studentCode".equalsIgnoreCase(orderType)) {
			sql.append(" order by fo.Student_code desc ");
		}else if ("studentName".equalsIgnoreCase(orderType)){
			sql.append(" order by fo.Student_name desc ");
		}

		return jdbcDao.queryForMap(sql.toString(), list.toArray());
	}

	@Override
	public Page<FreeDayTradingSum> getDealSummaryPage(Page<FreeDayTradingSum> page, String orgId, String startDate,
			String endDate) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreeDayTradingSum f where 1=1");
		if (StringUtils.isNotBlank(startDate)) {// 对账日期
			hql.append(" and f.reDate >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {//  对账日期
			hql.append(" and f.reDate <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			hql.append(" and f.orgId = ? ");
			list.add(orgId);
		}

		hql.append(" order by f.reDate desc ");

		return hibernateBaseDao.findPage(page, hql.toString(), list.toArray());
	}

	@Override
	public List<Map<String, Object>> getDealSummaryList(String orgId, String startDate, String endDate) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select f.pay_number,f.sum_money,f.pay_wx_number,f.sum_wx_money,f.refund_wx_money,f.Service_wx_money,f.error_wx_msg,"
				+ "(case when f.check_wx_state = 0 then '未对账' else '已对账' end) as wx_check_state,"
				+ "DATE_FORMAT(f.re_date,'%Y-%m-%d') as re_date, "
				+ "DATE_FORMAT(f.creater_time,'%Y-%m-%d') as creater_time"
				+ " from free_day_trading_sum f where 1=1 ");
		if (StringUtils.isNotBlank(startDate)) {// 创建日期
			sql.append(" and f.re_date >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {//  创建日期
			sql.append(" and f.re_date <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			sql.append(" and f.org_id = ? ");
			list.add(orgId);
		}

		sql.append(" order by f.re_date desc ");

		return jdbcDao.queryForMap(sql.toString(), list.toArray());
	}

	@Override
	public FreeDayTradingSum getDealSummary(String orgId, String startDate, String endDate) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from FreeDayTradingSum f where 1=1 ");
		if (StringUtils.isNotBlank(startDate)) {// 创建日期
			hql.append(" and f.reDate >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {//  创建日期
			hql.append(" and f.reDate <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			hql.append(" and f.orgId = ? ");
			list.add(orgId);
		}

		List<FreeDayTradingSum> dealList = (List<FreeDayTradingSum>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
		if (dealList != null && dealList.size() > 0) {
			return dealList.get(0);
		}
		return null;
	}

	@Override
	public FreePo getEntityById(String id) {
		return hibernateBaseDao.getEntity(FreePo.class, id);
	}

	@Override
	public FreePo getFreePoById(String id, Integer state) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM FreePo p WHERE 1=1 ");
		if (StringUtils.isNotEmpty(id)) {
			hql.append(" and p.id = ? ");
			list.add(id);
		}
		if (state != null) {
			hql.append(" and p.state = ? ");
			list.add(state.intValue());
		}
		List<FreePo> freePos = (List<FreePo>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
		if (freePos != null && freePos.size() > 0) {
			return freePos.get(0);
		}
		return null;
	}

}
