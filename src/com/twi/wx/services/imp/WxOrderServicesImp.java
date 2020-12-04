package com.twi.wx.services.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.StringUtils;
import com.twi.wechat.domain.UserOrder;
import com.twi.wx.services.WxOrderServices;

@Service("wxOrderServices")
public class WxOrderServicesImp extends BaseService implements WxOrderServices {

	@Override
	public List<Map<String, Object>> getOrderList(String openId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	p.ID_ as poId,p.order_no as orderNo,	p.product_name as productName,	DATE_FORMAT(p.creater_time,'%Y-%m-%d %H:%i:%s') as createrTime,"
				+ "	DATE_FORMAT(w.pay_time,'%Y-%m-%d %H:%i:%s') as payTime, p.pay_money as payMoney, p.state_ as payState FROM free_po p "
				+ "LEFT JOIN free_wx_pay w ON w.free_po_id = p.ID_ WHERE 1=1 ");
		
		if (StringUtils.strIsNotNull("openId")) {
			sql.append(" and p.openid_ =? ");
			pList.add(openId);
		}
		sql.append(" order by p.creater_time DESC");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

	@Override
	public List<Map<String, Object>> getOrderItemList(String poId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT i.ID_ as itemId,d.free_Cart_name as freeCartName, IF(ISNULL(i.free_Cart_Detailed_id),i.pay_type_name,d.pay_type_name) as payTypeName, i.pay_money as payItemMoney, DATE_FORMAT(i.creater_time,'%Y-%m-%d %H:%i:%s') as createrTime "
				+ "FROM free_po_item i LEFT JOIN free_cart_detailed d ON d.ID_ = i.free_Cart_Detailed_id WHERE 1=1 ");
		
		if (StringUtils.strIsNotNull("poId")) {
			sql.append(" and i.free_po_id =? ");
			pList.add(poId);
		}
		sql.append(" order by i.creater_time DESC");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

	@Override
	public List<Map<String, Object>> getPoItemList(String openId, String detailedId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT p.ID_ as poId,p.order_no as orderNo,	DATE_FORMAT(p.creater_time,'%Y-%m-%d %H:%i:%s') as createrTime,"
				+ "	DATE_FORMAT(w.pay_time,'%Y-%m-%d %H:%i:%s')  as payTime,i.pay_money as payItemMoney,d.free_Cart_name as freeCartName,"
				+ "	d.pay_type_name as payTypeName,	p.pay_money as payMoney,p.state_ as payState,i.ID_ as itemId,"
				+ " (SELECT count(1) FROM (SELECT free_po_id FROM free_po_item aa ) ff WHERE ff.free_po_id = p.ID_ ) as count"
				+ " FROM free_po_item i LEFT JOIN free_po p ON i.free_po_id = p.ID_ LEFT JOIN free_wx_pay w ON w.free_po_id = p.ID_ "
				+ " LEFT JOIN free_cart_detailed d ON d.ID_ = i.free_Cart_Detailed_id WHERE 1=1 ");
		if (StringUtils.strIsNotNull("openId")) {
			sql.append(" AND i.openid_ =? ");
			pList.add(openId);
		}
		if (StringUtils.strIsNotNull("detailedId")) {
			sql.append(" AND i.free_Cart_Detailed_id =? ");
			pList.add(detailedId);
		}
		sql.append(" GROUP BY i.free_po_id");
		sql.append(" order by i.creater_time DESC");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

	@Override
	public List<Map<String, Object>> getStuDetailedList(String openId, String bindId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	d.ID_ as detaileId,	d.free_Cart_name as freeCartName,  d.pay_type_name as payTypeName,"
				+ " d.money_ as money,  d.pay_money as payMoney FROM sys_wx_student s "
				+ " LEFT JOIN free_cart_detailed d ON  d.Student_id=s.Student_id "
				+ " LEFT JOIN free_cart c ON c.ID_=d.free_Cart_id WHERE d.state_ > 1 ");
		
		if (StringUtils.strIsNotNull("openId")) {
			sql.append(" AND s.openid_ =? ");
			pList.add(openId);
		}
		if (StringUtils.strIsNotNull("bindId")) {
			sql.append(" AND s.ID_ =? ");
			pList.add(bindId);
		}
		sql.append(" order by c.create_date DESC");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}
	@Override
	public List<Map<String, Object>> getStuDetailedList(String openId, String bindId, String orgId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	d.ID_ as detaileId,	d.free_Cart_name as freeCartName,  d.pay_type_name as payTypeName,"
				+ " d.money_ as money,  d.pay_money as payMoney FROM sys_wx_student s "
				+ " LEFT JOIN free_cart_detailed d ON  d.Student_id=s.Student_id "
				+ " LEFT JOIN free_cart c ON c.ID_=d.free_Cart_id WHERE d.state_ > 1 ");
		
		if (StringUtils.strIsNotNull("openId")) {
			sql.append(" AND s.openid_ =? ");
			pList.add(openId);
		}
		if (StringUtils.strIsNotNull("bindId")) {
			sql.append(" AND s.ID_ =? ");
			pList.add(bindId);
		}
		if (StringUtils.strIsNotNull("orgId")) {
			sql.append(" and d.org_id =? ");
			pList.add(orgId);
		}
		sql.append(" order by c.create_date DESC");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}
	
	@Override
	public List<Map<String, Object>> getStuOrderList(String bindId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	p.ID_ as poId,p.order_no as orderNo, p.product_name as productName,	DATE_FORMAT(p.creater_time,'%Y-%m-%d %H:%i:%s') as createrTime,"
				+ "	DATE_FORMAT(w.pay_time,'%Y-%m-%d %H:%i:%s') as payTime, p.pay_money as payMoney, p.state_ as payState FROM free_po p "
				+ "LEFT JOIN free_wx_pay w ON w.free_po_id = p.ID_ WHERE 1=1 ");
		sql.append(" AND p.Student_code IN ( SELECT	s.Student_code	FROM sys_wx_student s WHERE 1=1	");
		
		if (StringUtils.strIsNotNull("bindId")) {
			sql.append(" AND s.ID_ =? ");
			pList.add(bindId);
		}
		sql.append(") order by p.creater_time DESC");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}
	@Override
	public List<Map<String, Object>> getStuOrderList(String bindId, String orgId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	p.ID_ as poId,p.order_no as orderNo, p.product_name as productName,	DATE_FORMAT(p.creater_time,'%Y-%m-%d %H:%i:%s') as createrTime,"
				+ "	DATE_FORMAT(w.pay_time,'%Y-%m-%d %H:%i:%s') as payTime, p.pay_money as payMoney, p.state_ as payState FROM free_po p "
				+ "LEFT JOIN free_wx_pay w ON w.free_po_id = p.ID_ WHERE 1=1 ");
		sql.append(" AND p.Student_code IN ( SELECT	s.Student_code	FROM sys_wx_student s WHERE 1=1	");
		
		if (StringUtils.strIsNotNull("bindId")) {
			sql.append(" AND s.ID_ =? ");
			pList.add(bindId);
		}
		if (StringUtils.strIsNotNull("orgId")) {
			sql.append(" and p.org_id =? ");
			pList.add(orgId);
		}
		sql.append(") order by p.creater_time DESC");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

	@Override
	public Page<UserOrder> getUserOrderPage(Page<UserOrder> page, String openId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	p.ID_ as poId,p.order_no as orderNo, p.product_name as productName,	DATE_FORMAT(p.creater_time,'%Y-%m-%d %H:%i:%s') as createrTime,"
				+ "	DATE_FORMAT(w.pay_time,'%Y-%m-%d %H:%i:%s') as payTime, p.pay_money as payMoney, p.state_ as payState FROM free_po p "
				+ "LEFT JOIN free_wx_pay w ON w.free_po_id = p.ID_ WHERE 1=1 ");
		
		if (StringUtils.strIsNotNull("openId")) {
			sql.append(" and p.openid_ =? ");
			pList.add(openId);
		}
		sql.append(" order by p.creater_time DESC");
		RowMapper<UserOrder> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(UserOrder.class);
		return jdbcDao.queryForPageList(page,rowMapper, sql.toString(), pList.toArray());
	}
	@Override
	public Page<UserOrder> getUserOrderPage(Page<UserOrder> page, String openId, String orgId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	p.ID_ as poId,p.order_no as orderNo, p.product_name as productName,	DATE_FORMAT(p.creater_time,'%Y-%m-%d %H:%i:%s') as createrTime,"
				+ "	DATE_FORMAT(w.pay_time,'%Y-%m-%d %H:%i:%s') as payTime, p.pay_money as payMoney, p.state_ as payState FROM free_po p "
				+ "LEFT JOIN free_wx_pay w ON w.free_po_id = p.ID_ WHERE 1=1 ");
		
		if (StringUtils.strIsNotNull("openId")) {
			sql.append(" and p.openid_ =? ");
			pList.add(openId);
		}
		if (StringUtils.strIsNotNull("orgId")) {
			sql.append(" and p.org_id =? ");
			pList.add(orgId);
		}
		sql.append(" order by p.creater_time DESC");
		RowMapper<UserOrder> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(UserOrder.class);
		return jdbcDao.queryForPageList(page,rowMapper, sql.toString(), pList.toArray());
	}

}
