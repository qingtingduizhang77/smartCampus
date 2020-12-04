package com.twi.sysmenu.services.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.sysmenu.domain.SysMenu;
import com.twi.sysmenu.services.SysMenuServices;

@Service("sysMenuServices")
public class SysMenuServicesImp extends BaseService implements SysMenuServices {

	@Override
	public SysMenu getSysMenuBYRole(String roleId, String orgId) {
		String hql=" from SysMenu where roleId=?  and orgId=?";
		List<SysMenu>  list=(List<SysMenu>) this.hibernateBaseDao.getEntityList(hql, new Object[]{roleId,orgId});
		if(list!=null && list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}

}
