package com.twi.paytype.services.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.honzel.core.util.BeanHelper;
import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.base.util.StringUtils;
import com.twi.paytype.domain.SysPayType;
import com.twi.paytype.services.PayTypeServices;

@Service("payTypeServicesImp")
public class PayTypeServicesImp extends BaseService implements PayTypeServices{

	@Override
	public Page<SysPayType> getPayTypePage(Page<SysPayType> page, String name, String startDate, String endDate, String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from SysPayType p where 1=1 ");
		if (StringUtils.isNotBlank(name)) {// 名称
			hql.append(" and p.name like ? ");
			list.add("%" + name + "%");
		}
		if (StringUtils.isNotBlank(startDate)) {// 创建日期
			hql.append(" and p.createDate >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {//  创建日期
			hql.append(" and p.createDate <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			hql.append(" and p.orgId = ? ");
			list.add(orgId);
		}
		
		hql.append(" order by p.createDate desc ");

		return this.hibernateBaseDao.findPage(page, hql.toString(), list.toArray());
	}

	@Override
	public boolean savePayType(SysPayType payType) {
		boolean flag = true;
		try {
			if (StringUtils.isNotEmpty(payType.getId())) {
				SysPayType payType1 = getPayTypeById(payType.getId());
				Date createDate = payType1.getCreateDate();
				BeanHelper.copyProperties(payType, payType1);
				payType1.setCreateDate(createDate);
				hibernateBaseDao.udpEntity(payType1);
			}else{
				payType.setCreateDate(new Date());
				hibernateBaseDao.addEntity(payType);
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	@Override
	public boolean updateState(String ids, String state) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("update SysPayType set state = ? where id in ( ");
		list.add(state);
		String[] idsArr = ids.split(",");
		for (int i = 0; i < idsArr.length; i++) {
			hql.append("'" + idsArr[i] + "'");
			if (idsArr.length - i != 1) {
				hql.append(",");
			}
		}
		hql.append(")");
		
		return hibernateBaseDao.bulkUpdate(hql.toString(), list.toArray());
	}

	@Override
	public SysPayType getPayTypeById(String id) {
		return hibernateBaseDao.getEntity(SysPayType.class, id);
	}

	@Override
	public List<SysPayType> getPayTypeList(String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from SysPayType p where state = 1");
		if (StringUtils.isNotEmpty(orgId)) {
			hql.append(" and p.orgId = ? ");
			list.add(orgId);
		}
		
		hql.append(" order by p.createDate desc ");

		return (List<SysPayType>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
	}

	@Override
	public SysPayType findPayTypeByOrgCode(String orgId,String feeTypeCode,String feeTypeName) {
		if(StringUtils.isBlank(orgId) || StringUtils.isBlank(feeTypeCode) || StringUtils.isBlank(feeTypeName)) {
			return null;
		}
		SysPayType payType = null;
		try {
			List<Object> list = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer();

			hql.append(" from SysPayType p where state = 1");
			hql.append(" and p.orgId = ? ");
			hql.append(" and p.code = ? ");
			hql.append(" order by p.createDate desc ");
			list.add(orgId);
			list.add(feeTypeCode);

			List<?> listO = hibernateBaseDao.getEntityList(hql.toString(), list.toArray());

			if(listO.size()>0) {
				payType = (SysPayType)listO.get(0);
			} else {
				payType = new SysPayType();
				payType.setId(StringUtils.randomUUID());
				payType.setName(feeTypeName);
				payType.setCode(feeTypeCode);
				payType.setFromCode(9);
				payType.setFromName("定制收费");
				payType.setTypeCode(1);
				payType.setTypeName("一次性缴费");
				payType.setState(1);
				payType.setCreateDate(new Date());
				payType.setOrgId(orgId);
				if(!hibernateBaseDao.addEntity(payType)) {
					payType = null;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return payType;
	}
}
