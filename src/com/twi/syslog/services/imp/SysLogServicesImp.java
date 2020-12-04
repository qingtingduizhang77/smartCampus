package com.twi.syslog.services.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.syslog.domain.SysLog;
import com.twi.syslog.services.SysLogServices;

@Service("sysLogServices")
public class SysLogServicesImp extends BaseService implements SysLogServices{

	@Override
	public boolean add(SysLog entity) {
		return this.hibernateBaseDao.addEntity(entity);
	}

	@Override
	public boolean update(SysLog entity) {
		return this.hibernateBaseDao.udpEntity(entity);
	}

	@Override
	public SysLog getEntityById(String id) {
		return this.hibernateBaseDao.getEntity(SysLog.class, id);
	}

	@Override
	public Page<SysLog> getPageList(Page<SysLog> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("from SysLog where 1=1 ");

		if (pMap.containsKey("beginTime")) {// 开始时间
			hql.append(" and createTime >=? ");
			pList.add(CalendarUtils.dayBegin(pMap.get("beginTime").toString()));
		}
		if (pMap.containsKey("endTime")) {//结束时间			
			hql.append(" and createTime <=? ");
			pList.add(CalendarUtils.dayEnd(pMap.get("endTime").toString()));
		}
		if (pMap.containsKey("orgId")) {
			hql.append(" and orgId =? ");
			pList.add(pMap.get("orgId").toString());
		}

		hql.append(" order by createTime DESC ");
		return this.hibernateBaseDao.findPage(page, hql.toString(), pList.toArray());
	}

}
