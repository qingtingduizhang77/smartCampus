package com.twi.syslog.services.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.syslog.domain.CheckErrLog;
import com.twi.syslog.services.CheckErrLogServices;

@Service("checkErrLogServices")
public class CheckErrLogServicesImp extends BaseService implements CheckErrLogServices {

	@Override
	public boolean add(CheckErrLog entity) {
		entity.setState(0);
		entity.setCreaterTime(new Date());
		return this.hibernateBaseDao.addEntity(entity);
	}

	@Override
	public boolean update(CheckErrLog entity) {
		return this.hibernateBaseDao.udpEntity(entity);
	}

	@Override
	public Page<CheckErrLog> getPageList(Page<CheckErrLog> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("from CheckErrLog where 1=1 ");
		if (pMap.containsKey("wxOrderNo")) {
			hql.append(" and wxOrderNo = ? ");
			pList.add(pMap.get("wxOrderNo").toString());
		}
		if (pMap.containsKey("freePoNo")) {
			hql.append(" and freePoNo = ? ");
			pList.add(pMap.get("freePoNo").toString());
		}
		if (pMap.containsKey("checkMode")) {
			hql.append(" and checkMode = ? ");
			pList.add(pMap.get("checkMode"));
		}
		if (pMap.containsKey("errCode")) {
			hql.append(" and errCode = ? ");
			pList.add(pMap.get("errCode"));
		}
		if (pMap.containsKey("state")) {
			hql.append(" and state = ? ");
			pList.add(pMap.get("state"));
		}
		if (pMap.containsKey("beginPayTime")) {// 支付时间
			hql.append(" and payTime >=? ");
			pList.add(CalendarUtils.dayBegin(pMap.get("beginPayTime").toString()));
		}
		if (pMap.containsKey("endPayTime")) {// 支付时间			
			hql.append(" and payTime <=? ");
			pList.add(CalendarUtils.dayEnd(pMap.get("endPayTime").toString()));
		}
		if (pMap.containsKey("orgId")) {
			hql.append(" and orgId =? ");
			pList.add(pMap.get("orgId").toString());
		}

		hql.append(" order by payTime DESC ");
		return this.hibernateBaseDao.findPage(page, hql.toString(), pList.toArray());
	}

	@Override
	public CheckErrLog getEntityById(String id) {		
		return this.hibernateBaseDao.getEntity(CheckErrLog.class, id);
	}

	@Override
	public CheckErrLog getEntity(String freePoNo, String orgId) {
		String hql = "from CheckErrLog where freePoNo=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, freePoNo, orgId);
	}

}
