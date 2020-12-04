package com.twi.freecart.services.imp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.StringUtils;
import com.twi.freecart.domain.FreeCart;
import com.twi.freecart.services.FreeCartServices;
import com.twi.freecartdetailed.domain.FreeCartDetailed;
import com.twi.paytype.domain.SysPayType;
import com.twi.student.domain.SysStudent;

@Service("freeCartServices")
public class FreeCartServicesImp extends BaseService implements FreeCartServices {

	@Override
	public boolean addFreeCart(FreeCart freeCart) {
		Calendar cal = Calendar.getInstance();
		freeCart.setCreateDate(new Date());
		freeCart.setYear(cal.get(Calendar.YEAR));
		return this.hibernateBaseDao.addEntity(freeCart);
	}

	@Override
	public boolean updFreeCart(FreeCart freeCart) {
		return this.hibernateBaseDao.udpEntity(freeCart);
	}

	@Override
	public boolean delFreeCartById(String id) {
		FreeCart freeCart = this.hibernateBaseDao.getEntity(FreeCart.class, id);
		if (freeCart != null) {
			this.hibernateBaseDao.delEntity(freeCart);
		}
		return true;
	}

	@Override
	public boolean batchDelete(String[] ids) {
		if (ids != null && ids.length > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from free_cart where ");
			for (int i = 0; i < ids.length; i++) {
				if (i == 0) {
					sql.append(" id_=? ");
				} else {
					sql.append(" or id_=? ");
				}
			}
			return this.jdbcDao.executeSql(sql.toString(), ids);
		}
		return true;
	}

	@Override
	public FreeCart getFreeCartByName(String name, String orgId) {
		String hql = "from FreeCart where name=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, name, orgId);
	}

	@Override
	public FreeCart getFreeCartById(String id) {
		return this.hibernateBaseDao.getEntity(FreeCart.class, id);
	}

	@Override
	public Page<FreeCart> getFreeCartPage(Page<FreeCart> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("from FreeCart where 1=1 ");
		if (pMap.containsKey("name")) {
			hql.append(" and name like ? ");
			pList.add("%" + pMap.get("name").toString() + "%");
		}
		if (pMap.containsKey("year")) {
			hql.append(" and year = ? ");
			pList.add(Integer.parseInt(pMap.get("year").toString()));
		}
		if (pMap.containsKey("state")) {
			hql.append(" and state = ? ");
			pList.add(Integer.parseInt(pMap.get("state").toString()));
		}
		if (pMap.containsKey("orgId")) {
			hql.append(" and orgId =? ");
			pList.add(pMap.get("orgId").toString());
		}

		hql.append(" ORDER BY createDate DESC ");
		return this.hibernateBaseDao.findPage(page, hql.toString(), pList.toArray());

	}

	@Override
	public SysStudent getStudentByCode(String code, String orgId) {
		String hql = "from SysStudent where code=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, code, orgId);
	}

	@Override
	public SysPayType getPayTypeByName(String name, String orgId) {
		String hql = "from SysPayType where name=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, name, orgId);
	}

	@Override
	public boolean batchAddFreeCartDetailed(List<FreeCartDetailed> list) {
		List<String> sqlList = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (FreeCartDetailed fDetailed : list) {
				StringBuffer sql = new StringBuffer();
				sql.append(" insert into free_Cart_Detailed ");
				sql.append(" (id_,free_Cart_id,free_Cart_name,Student_code,"
						+ "Student_id,Student_name,pwd_,major_name,clazz_name,"
						+ "pay_type_id,pay_type_name,money_,state_,org_id) value(");
				if (StringUtils.strIsNotNull(fDetailed.getId())) {
					sql.append("'" + fDetailed.getId() + "','");
				} else {
					sql.append("'" + StringUtils.randomUUID() + "','");
				}
				sql.append(fDetailed.getFreeCartId() + "','" + fDetailed.getFreeCartName() + "','");
				sql.append(fDetailed.getStudentCode() + "','" + fDetailed.getStudentId() + "','"
						+ fDetailed.getStudentName() + "','" + fDetailed.getPwd() + "','");
				sql.append(fDetailed.getMajorName() + "','" + fDetailed.getClazzName() + "','");
				sql.append(fDetailed.getPayTypeId() + "','" + fDetailed.getPayTypeName() + "','" + fDetailed.getMoney()
						+ "','");
				sql.append(fDetailed.getState() + "','" + fDetailed.getOrgId() + "')");
				sql.append(" ON DUPLICATE KEY UPDATE ");
				sql.append(" money_='" + fDetailed.getMoney() + "',clazz_name='" + fDetailed.getClazzName() + "'");
				sqlList.add(sql.toString());
			}
		}
		if (sqlList != null && sqlList.size() > 0) {
			return jdbcDao.batchExecuteSql(sqlList);
		} else {
			return true;
		}
	}

	@Override
	public boolean addSysStudent(SysStudent student) {
		student.setCreateDate(new Date());
		student.setState("1");
		return this.hibernateBaseDao.addEntity(student);
	}

	@Override
	public boolean updSysStudent(SysStudent student) {
		return this.hibernateBaseDao.udpEntity(student);
	}

	@Override
	public FreeCartDetailed queryBycondition(String freeCartId, String code, String name, String payTypeName,
			String orgId) {
		String hql = "from FreeCartDetailed where freeCartId=? and studentCode=? and studentName=? and payTypeName=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, freeCartId, code, name, payTypeName, orgId);
	}

	@Override
	public List<Map<String, Object>> getPayTypeStr(String freeCartId, String orgId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 	GROUP_CONCAT(DISTINCT pay_type_name) AS payTypeName,"
				+ "GROUP_CONCAT(DISTINCT pay_type_id) AS payTypeId FROM free_cart_detailed WHERE 1=1 ");

		if (StringUtils.strIsNotNull("freeCartId")) {
			sql.append(" and free_Cart_id = ? ");
			pList.add(freeCartId);
		}
		if (StringUtils.strIsNotNull("orgId")) {
			sql.append(" and org_id =? ");
			pList.add(orgId);
		}
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

	@Override
	public List<Map<String, Object>> getFreeCartDetailedList(Map<String, Object> pMap, String[] typeNameArr) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT	d.Student_code as 学号, d.Student_name as 姓名,d.grade_name as 年级,d.major_name as 专业,d.clazz_name as 班级");
		for (int i = 0; i < typeNameArr.length; i++) {
			sql.append(", MAX( CASE d.pay_type_name	WHEN '" + typeNameArr[i] + "' THEN	d.money_ ELSE 0	END	) AS '"+ typeNameArr[i] + "_应缴',"
					+ " MAX( CASE d.pay_type_name	WHEN '" + typeNameArr[i]+ "' THEN	d.pay_money ELSE 0	END	) AS '" + typeNameArr[i] + "_实缴',"
					+ "	MAX( CASE d.pay_type_name	WHEN '" + typeNameArr[i] + "' THEN	d.Less_money ELSE 0	END	) AS '" + typeNameArr[i] + "_欠缴'");
		}
		sql.append(" FROM free_cart_detailed d WHERE 1=1 ");

		if (pMap.containsKey("freeCartId")) {
			sql.append(" and d.free_Cart_id = ? ");
			pList.add(pMap.get("freeCartId"));
		}
		if (pMap.containsKey("orgId")) {
			sql.append(" and d.org_id =? ");
			pList.add(pMap.get("orgId"));
		}
		sql.append(" GROUP BY d.Student_code ");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

}
