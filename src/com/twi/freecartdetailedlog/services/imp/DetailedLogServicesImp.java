package com.twi.freecartdetailedlog.services.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.freecartdetailedlog.domain.DetailedLog;
import com.twi.freecartdetailedlog.services.DetailedLogServices;

@Service("detailedLogServices")
public class DetailedLogServicesImp extends BaseService implements DetailedLogServices {

	@Override
	public boolean add(DetailedLog entity) {
		return this.hibernateBaseDao.addEntity(entity);
	}

	@Override
	public boolean update(DetailedLog entity) {
		return this.hibernateBaseDao.udpEntity(entity);
	}

	@Override
	public boolean deleteById(String id) {
		DetailedLog entity = this.hibernateBaseDao.getEntity(DetailedLog.class, id);
		if (entity != null) {
			this.hibernateBaseDao.delEntity(entity);
		}
		return true;
	}

	@Override
	public boolean batchDelete(String[] ids) {
		if (ids != null && ids.length > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from free_cart_detailed_log where ");
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
	public Page<DetailedLog> getPageList(Page<DetailedLog> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("from DetailedLog where 1=1 ");
		
		if (pMap.containsKey("orgId")) {
			hql.append(" and orgId =? ");
			pList.add(pMap.get("orgId").toString());
		}

		return this.hibernateBaseDao.findPage(page, hql.toString(), pList.toArray());
	}

}
