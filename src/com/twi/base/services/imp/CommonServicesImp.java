package com.twi.base.services.imp;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.services.CommonServices;
import com.twi.paymenu.domain.SysPayMenu;
import com.twi.user.domain.Role;
import com.twi.user.domain.SysOrgInfo;

@Service("commonServices")
public class CommonServicesImp extends BaseService implements CommonServices {

	@Override
	public String getOrgIdByServerName(String serverName) {
		String sql="select id_ from  sys_org_info where dns_=?";
		
		List<String> list=this.jdbcDao.queryForList(String.class, sql, new Object[]{serverName});
		if(list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}

	@Override
	public SysOrgInfo getSysOrgInfoById(String id) {
		
		return this.hibernateBaseDao.getEntity(SysOrgInfo.class, id);
	}

	@Override
	public List<Map<String,Object>> getRole(String userId) {
		String  sql="SELECT id_ AS roleId ,name_ AS roleName FROM  sys_role  WHERE id_ IN(SELECT role_id FROM sys_account_role  WHERE user_id=? ) ";
		return this.jdbcDao.queryForMap(sql, new Object[]{userId});
		
	}

	@Override
	public SysPayMenu getSysPayMenu(String orgId, String menuName) {
        String hql="from  SysPayMenu where orgId=? and name=? ";
        List<SysPayMenu> list=(List<SysPayMenu>) this.hibernateBaseDao.getEntityList(hql, new Object[]{orgId,menuName});
        if(list!=null && list.size()>0)
        {
        	return list.get(0);
        }
		return null;
	}

	@Override
	public SysOrgInfo getSysOrgInfoByKey(String key) {
		if(key==null)
		{
			return null;
		}
		String  hql="from  SysOrgInfo where key=?";
		
		List<SysOrgInfo> list=(List<SysOrgInfo>) this.hibernateBaseDao.getEntityList(hql, new Object[]{key.trim()});
		if(list!=null && list.size()>0)
		{
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public String getOrgIdByKey(String Key) {
             String sql="select id_ from  sys_org_info where key_=?";
		
		List<String> list=this.jdbcDao.queryForList(String.class, sql, new Object[]{Key});
		if(list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}

}
