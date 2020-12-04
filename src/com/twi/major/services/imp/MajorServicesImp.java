package com.twi.major.services.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.major.domain.Major;
import com.twi.major.services.MajorServices;

@Service("majorServices")
public class MajorServicesImp extends BaseService implements MajorServices {

	@Override
	public boolean addMajor(Major major) {
		major.setCreateDate(new Date());
		major.setState(1);// 默认开启

		return this.hibernateBaseDao.addEntity(major);
	}

	@Override
	public boolean updMajor(Major major) {
		Major m = getMajorById(major.getId());
		m.setPid(major.getPid());
		m.setPname(major.getPname());
		m.setRemark(major.getRemark());
		m.setName(major.getName());
		return this.hibernateBaseDao.udpEntity(m);
	}

	@Override
	public boolean delMajorById(String id) {
		Major major = this.hibernateBaseDao.getEntity(Major.class, id);
		if (major != null) {
			this.hibernateBaseDao.delEntity(major);
		}
		return true;
	}

	@Override
	public boolean batchDelMajor(String[] ids) {
		if (ids != null && ids.length > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from sys_major_info where ");
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
	public Major getMajorByName(String name, String orgId) {
		String hql = "from Major where name=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, name, orgId);
	}

	@Override
	public Major getMajorById(String id) {

		return this.hibernateBaseDao.getEntity(Major.class, id);
	}

	@Override
	public Page<Major> getMajorPage(Page<Major> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("from Major where 1=1 ");
		if (pMap.containsKey("name")) {
			hql.append(" and name like ? ");
			pList.add("%" + pMap.get("name").toString() + "%");
		}
		
		if (pMap.containsKey("beginDate")) {
			hql.append(" and create_date >= ? ");
			pList.add(pMap.get("beginDate").toString()+" 00:00:00");
		}
		if (pMap.containsKey("endDate")) {
			hql.append(" and create_date <= ? ");
			pList.add(pMap.get("endDate").toString()+" 23:59:59");
		}
		if (pMap.containsKey("orgId")) {
			hql.append(" and orgId =? ");
			pList.add(pMap.get("orgId").toString());
		}

		hql.append(" order by create_date DESC ");
		return this.hibernateBaseDao.findPage(page, hql.toString(), pList.toArray());

	}

}
