package com.twi.paymenu.services.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.honzel.core.util.BeanHelper;
import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.StringUtils;
import com.twi.paymenu.domain.SysPayMenu;
import com.twi.paymenu.services.PayMenuServices;

@Service("payMenuServicesImp")
public class PayMenuServicesImp extends BaseService implements PayMenuServices{

	@Override
	public Page<SysPayMenu> getPayMenuPage(Page<SysPayMenu> page, String orgId, String name) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from SysPayMenu p where 1=1 ");
		if (StringUtils.isNotBlank(name)) {// 名称
			hql.append(" and p.name like ? ");
			list.add("%" + name + "%");
		}
		if (StringUtils.isNotEmpty(orgId)) {
			hql.append(" and p.orgId = ? ");
			list.add(orgId);
		}
		hql.append(" order by p.orderNumber asc,p.createDate desc ");

		return this.hibernateBaseDao.findPage(page, hql.toString(), list.toArray());
	}

	@Override
	public boolean savePayMenu(SysPayMenu payMenu) {
		boolean flag = true;
		try {
			if (StringUtils.isNotEmpty(payMenu.getId())) {
				SysPayMenu payMenu1 = getPayMenuById(payMenu.getId());
				Date createDate = payMenu1.getCreateDate();
				BeanHelper.copyProperties(payMenu, payMenu1);
				payMenu1.setCreateDate(createDate);
				hibernateBaseDao.udpEntity(payMenu1);
			}else{
				payMenu.setCreateDate(new Date());
				hibernateBaseDao.addEntity(payMenu);
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	@Override
	public SysPayMenu getPayMenuById(String id) {
		return hibernateBaseDao.getEntity(SysPayMenu.class, id);
	}

	@Override
	public List<SysPayMenu> getPayMenuList(String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from SysPayMenu p where 1=1 and state = 1");
		if (StringUtils.isNotEmpty(orgId)) {
			hql.append(" and p.orgId = ? ");
			list.add(orgId);
		}else{
			hql.append(" and p.orgId = '666666' ");
		}
		
		hql.append(" order by p.orderNumber asc,p.createDate desc ");

		return (List<SysPayMenu>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
	}

}
