package com.twi.freecartdetailed.services.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.freecartdetailed.domain.DetailedItemReport;
import com.twi.freecartdetailed.domain.DetailedModel;
import com.twi.freecartdetailed.domain.DetailedSummary;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.freecartdetailed.services.FreeCartDetailedServices;

@Service("freeCartDetailedServices")
public class FreeCartDetailedServicesImp extends BaseService implements FreeCartDetailedServices {

	@Override
	public boolean add(FreeCartDetailed entity) {
		entity.setState(2); // 默认待缴费
		return this.hibernateBaseDao.addEntity(entity);
	}

	@Override
	public boolean update(FreeCartDetailed entity) {
		return this.hibernateBaseDao.udpEntity(entity);
	}

	@Override
	public boolean deleteById(String id) {
		FreeCartDetailed entity = this.hibernateBaseDao.getEntity(FreeCartDetailed.class, id);
		if (entity != null && entity.getState() < 3) {
			return this.hibernateBaseDao.delEntity(entity);
		}else{
			return false;
		}		
	}

	@Override
	public int canDeleteCount(String[] ids){
		if (ids != null && ids.length > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(ff.id) FROM (SELECT f.ID_ as id,COUNT(i.ID_) countid FROM free_po_item i "
					+ " RIGHT JOIN free_Cart_Detailed f ON f.ID_ = i.free_Cart_Detailed_id WHERE ");
			for (int i = 0; i < ids.length; i++) {
				if (i == 0) {
					sql.append(" f.ID_ =? ");
				} else {
					sql.append(" or f.ID_ =? ");
				}
			}
			sql.append(" GROUP BY f.ID_ HAVING countid = 0 ) ff");
			return jdbcDao.getCountBySql(sql.toString(), ids);
		}
		return 0;
	}
	
	@Override
	public boolean batchDelete(String[] ids) {
		if (ids != null && ids.length > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from free_Cart_Detailed where ");
			sql.append(" ID_ in (SELECT ff.id FROM (SELECT f.ID_ as id,COUNT(i.ID_) countid FROM free_po_item i "
					+ " RIGHT JOIN free_Cart_Detailed f ON f.ID_ = i.free_Cart_Detailed_id WHERE ");
			for (int i = 0; i < ids.length; i++) {
				if (i == 0) {
					sql.append(" f.ID_ =? ");
				} else {
					sql.append(" or f.ID_ =? ");
				}
			}
			sql.append(" GROUP BY f.ID_ HAVING countid = 0 ) ff)");
			sql.append(" and state_ < 3 ");
			return this.jdbcDao.executeSql(sql.toString(), ids);
		}
		return true;
	}

	@Override
	public FreeCartDetailed getEntityByStuCode(String studentCode, String orgId) {
		String hql = "from FreeCartDetailed where studentCode=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, studentCode, orgId);
	}

	@Override
	public FreeCartDetailed getEntityById(String id) {
		return this.hibernateBaseDao.getEntity(FreeCartDetailed.class, id);
	}

	@Override
	public Page<FreeCartDetailed> getPageList(Page<FreeCartDetailed> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("from FreeCartDetailed where 1=1 ");
		if (pMap.containsKey("studentName")) {
			hql.append(" and studentName like ? ");
			pList.add("%" + pMap.get("studentName").toString() + "%");
		}
		if (pMap.containsKey("studentCode")) {
			hql.append(" and studentCode like ? ");
			pList.add("%" + pMap.get("studentCode").toString() + "%");
		}
		if (pMap.containsKey("clazzName")) {
			hql.append(" and clazzName like ? ");
			pList.add("%" + pMap.get("clazzName").toString() + "%");
		}
		if (pMap.containsKey("majorName")) {
			hql.append(" and majorName like ? ");
			pList.add("%" + pMap.get("majorName").toString() + "%");
		}
		if (pMap.containsKey("payTypeId")) {
			hql.append(" and payTypeId = ? ");
			pList.add(pMap.get("payTypeId").toString());
		}
		if (pMap.containsKey("freeCartId")) {
			hql.append(" and freeCartId = ? ");
			pList.add(pMap.get("freeCartId").toString());
		}
		if (pMap.containsKey("orgId")) {
			hql.append(" and orgId =? ");
			pList.add(pMap.get("orgId").toString());
		}

		return this.hibernateBaseDao.findPage(page, hql.toString(), pList.toArray());
	}

	@Override
	public List<Map<String, Object>> getConditionList(Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from free_Cart_Detailed where 1=1 ");
		if (pMap.containsKey("studentName")) {
			sql.append(" and Student_name like ? ");
			pList.add("%" + pMap.get("studentName").toString() + "%");
		}
		if (pMap.containsKey("studentCode")) {
			sql.append(" and Student_code like ? ");
			pList.add("%" + pMap.get("studentCode").toString() + "%");
		}
		if (pMap.containsKey("clazzName")) {
			sql.append(" and clazz_name like ? ");
			pList.add("%" + pMap.get("clazzName").toString() + "%");
		}
		if (pMap.containsKey("majorName")) {
			sql.append(" and major_name like ? ");
			pList.add("%" + pMap.get("majorName").toString() + "%");
		}
		if (pMap.containsKey("payTypeId")) {
			sql.append(" and pay_type_id = ? ");
			pList.add(pMap.get("payTypeId").toString());
		}
		if (pMap.containsKey("freeCartId")) {
			sql.append(" and free_Cart_id = ? ");
			pList.add(pMap.get("freeCartId").toString());
		}
		if (pMap.containsKey("deleteState")) {
			sql.append(" and state_ < 3 ");
		}
		if (pMap.containsKey("orgId")) {
			sql.append(" and org_id =? ");
			pList.add(pMap.get("orgId").toString());
		} else {// 无数据导出
			sql.append(" and org_id = '11111111' ");
		}

		sql.append(" order by Student_code ");

		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

	@Override
	public Page<DetailedSummary> getSummaryList(Page<DetailedSummary> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	c.year_ AS schYear,	d.free_Cart_name AS freeCartName, "
				+ "COUNT(d.Student_code) AS tpeopleCount, COUNT(d.ID_) AS tpayCount,ROUND(SUM(d.money_),3) AS tmoney,"
				+ " ROUND(SUM(d.pay_money),3) AS tpayMoney, ROUND(SUM(d.money_) - SUM(d.pay_money),3) AS tlessMoney,"
				+ "(case when SUM(d.money_) - SUM(d.pay_money) >0 then 0 else 1 end) as payState "
				+ "FROM	`free_cart_detailed` d LEFT JOIN free_cart c ON d.free_Cart_id = c.ID_ WHERE 1=1");
		
		if (pMap.containsKey("clazzName")) {
			sql.append(" and d.clazz_name like ? ");
			pList.add("%" + pMap.get("clazzName").toString() + "%");
		}
		if (pMap.containsKey("majorName")) {
			sql.append(" and d.major_name like ? ");
			pList.add("%" + pMap.get("majorName").toString() + "%");		
		}
		if (pMap.containsKey("payTypeId")) {
			sql.append(" and d.pay_type_id = ? ");
			pList.add(pMap.get("payTypeId").toString());
		}
		if (pMap.containsKey("freeCartId")) {
			sql.append(" and d.free_Cart_id = ? ");
			pList.add(pMap.get("freeCartId").toString());
		}
		if (pMap.containsKey("orgId")) {
			sql.append(" and d.org_id =? ");
			pList.add(pMap.get("orgId").toString());
		}
		sql.append(" GROUP BY d.free_Cart_id ");

		RowMapper<DetailedSummary> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DetailedSummary.class);
		return jdbcDao.queryForPageList(page,rowMapper, sql.toString(), pList.toArray());
	}

	@Override
	public Page<DetailedItemReport> getItemReportList(Page<DetailedItemReport> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	d.major_name AS majorName, GROUP_CONCAT(DISTINCT d.clazz_name) AS clazzName, d.pay_type_name as payTypeName,"
				+ "	COUNT(DISTINCT d.Student_code) AS tpeopleCount,  COUNT(d.ID_) AS tpayCount,	ROUND(SUM(d.money_),3) AS tmoney,"
				+ " ROUND(SUM(d.pay_money),3) AS tpayMoney, ROUND(SUM(d.money_) - SUM(d.pay_money),3) AS tlessMoney,"
				+ " (case when SUM(d.money_) - SUM(d.pay_money) >0 then 0 else 1 end) as payState FROM "
				+ "`free_cart_detailed` d WHERE 1=1 ");
		
		if (pMap.containsKey("clazzName")) {
			sql.append(" and clazz_name like ? ");
			pList.add("%" + pMap.get("clazzName").toString() + "%");
		}
		if (pMap.containsKey("majorName")) {
			sql.append(" and major_name like ? ");
			pList.add("%" + pMap.get("majorName").toString() + "%");
		}
		if (pMap.containsKey("payTypeId")) {
			sql.append(" and pay_type_id = ? ");
			pList.add(pMap.get("payTypeId").toString());
		}
		if (pMap.containsKey("freeCartId")) {
			sql.append(" and free_Cart_id = ? ");
			pList.add(pMap.get("freeCartId").toString());
		}
		if (pMap.containsKey("orgId")) {
			sql.append(" and org_id =? ");
			pList.add(pMap.get("orgId").toString());
		} 

		sql.append(" GROUP BY d.major_name,d.pay_type_id ");

		RowMapper<DetailedItemReport> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DetailedItemReport.class);
		return jdbcDao.queryForPageList(page, rowMapper, sql.toString(), pList.toArray());
	}

	@Override
	public List<Map<String, Object>> getPayReportList(Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	d.major_name AS majorName, GROUP_CONCAT(DISTINCT d.clazz_name) AS clazzName, d.pay_type_name as payTypeName,"
				+ "	COUNT(DISTINCT d.Student_code) AS tpeopleCount,  COUNT(d.ID_) AS tpayCount,	ROUND(SUM(d.money_),3) AS tmoney,"
				+ " ROUND(SUM(d.pay_money),3) AS tpayMoney, ROUND(SUM(d.money_) - SUM(d.pay_money),3) AS tlessMoney,"
				+ " (case when SUM(d.money_) - SUM(d.pay_money) >0 then '未缴清' else '已缴清' end) as payState FROM "
				+ "`free_cart_detailed` d WHERE 1=1 ");
		
		if (pMap.containsKey("clazzName")) {
			sql.append(" and clazz_name like ? ");
			pList.add("%" + pMap.get("clazzName").toString() + "%");
		}
		if (pMap.containsKey("majorName")) {
			sql.append(" and major_name like ? ");
			pList.add("%" + pMap.get("majorName").toString() + "%");
		}
		if (pMap.containsKey("payTypeId")) {
			sql.append(" and pay_type_id = ? ");
			pList.add(pMap.get("payTypeId").toString());
		}
		if (pMap.containsKey("freeCartId")) {
			sql.append(" and free_Cart_id = ? ");
			pList.add(pMap.get("freeCartId").toString());
		}
		if (pMap.containsKey("orgId")) {
			sql.append(" and org_id =? ");
			pList.add(pMap.get("orgId").toString());
		} 

		sql.append(" GROUP BY d.major_name,d.pay_type_id ");

		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

	@Override
	public List<DetailedModel> getFreeCartDetailList(String orgId, String payMenuId, String studentName, 
			String studentCode, String pwd, boolean isPwd){
		List<Object> objList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select fd.*,fd.id_ as id,fd.money_ as money,t.type_code as payTypeCode,convert((fd.money_-fd.order_money), decimal(10,2)) as ableAmount");
		sql.append(" from free_cart_detailed fd ");
		sql.append(" left join free_cart f on f.id_ = fd.free_Cart_id ");
		sql.append(" left join sys_pay_type t on t.id_ = fd.pay_type_id ");
		sql.append(" where fd.state_ in (2,3) and f.state_ = 2 ");
		if (StringUtils.isNotEmpty(studentName)) {
			sql.append(" and fd.Student_name = ? ");
			objList.add(studentName);
		}
		if (StringUtils.isNotEmpty(studentCode)) {
			sql.append(" and fd.Student_code = ? ");
			objList.add(studentCode);
		}
		if (StringUtils.isNotEmpty(orgId)) {
			sql.append(" and fd.org_id = ? ");
			objList.add(orgId);
		}
		if (StringUtils.isNotEmpty(pwd) && isPwd) {
			sql.append(" and fd.pwd_ = ? ");
			objList.add(pwd);
		}
		sql.append(" and fd.pay_type_id in (");
		sql.append(" select id_ from sys_pay_type where state_ = 1 and  pay_menu_ID = '" + payMenuId + "'");
		sql.append(" )");
		
		RowMapper<DetailedModel> rm = ParameterizedBeanPropertyRowMapper.newInstance(DetailedModel.class);
		
		return  jdbcDao.getListBySql(sql.toString(), rm, objList.toArray());
	}
	
}
