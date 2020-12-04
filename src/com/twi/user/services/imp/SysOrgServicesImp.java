package com.twi.user.services.imp;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.services.SysOrgServices;

@Service("sysOrgServicesImp")
public class SysOrgServicesImp extends BaseService implements SysOrgServices {

	@Override
	public SysOrgInfo getOrgInfoById(String id) {
		return hibernateBaseDao.getEntity(SysOrgInfo.class, id);
	}

	@Override
	public SysOrgInfo getOrgInfoByIdForEnable(String id) {
		try {
			String hql = " from SysOrgInfo where state=1 and id=? ";
			
			@SuppressWarnings("unchecked")
			List<SysOrgInfo> list = (List<SysOrgInfo>)hibernateBaseDao.getEntityList(hql, new Object[]{id}); 
			if(list!=null && list.size()>0) {
				return list.get(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SysOrgInfo getOrgInfoByKeyForEnable(String key) {
		try {
			String hql = " from SysOrgInfo where state=1 and key=? ";
			
			@SuppressWarnings("unchecked")
			List<SysOrgInfo> list = (List<SysOrgInfo>)hibernateBaseDao.getEntityList(hql, new Object[]{key}); 
			if(list!=null && list.size()>0) {
				return list.get(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public SysOrgInfo getOrgInfoByDnsForEnable(String dns) {
		try {
			String hql = " from SysOrgInfo where state=1 and dns=? ";
			
			@SuppressWarnings("unchecked")
			List<SysOrgInfo> list = (List<SysOrgInfo>)hibernateBaseDao.getEntityList(hql, new Object[]{dns}); 
			if(list!=null && list.size()>0) {
				return list.get(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String getOrgIdByKeyForEnable(String key) {
		try {
			String hql = " from SysOrgInfo where state=1 and key=? ";
			
			@SuppressWarnings("unchecked")
			List<SysOrgInfo> list = (List<SysOrgInfo>)hibernateBaseDao.getEntityList(hql, new Object[]{key}); 
			if(list!=null && list.size()>0) {
				return list.get(0).getId();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String getOrgIdByDnsForEnable(String dns) {
		try {
			String hql = " from SysOrgInfo where state=1 and dns=? ";
			
			@SuppressWarnings("unchecked")
			List<SysOrgInfo> list = (List<SysOrgInfo>)hibernateBaseDao.getEntityList(hql, new Object[]{dns}); 
			if(list!=null && list.size()>0) {
				return list.get(0).getId();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<SysOrgInfo> getOrgInfoList() {
		StringBuffer sql = new StringBuffer();

		sql.append(" select i.id_ as id,mch_is as mchIs,mchid_ as mchid,i.not_order_day as notOrderDay from sys_org_info i where i.state_ = 1 ");
		
		RowMapper<SysOrgInfo> rm = ParameterizedBeanPropertyRowMapper.newInstance(SysOrgInfo.class);
		
		return jdbcDao.getListBySql(sql.toString(), rm, null);
	}

}
