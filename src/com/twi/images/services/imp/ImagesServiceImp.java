package com.twi.images.services.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.StringUtils;
import com.twi.images.domain.Images;
import com.twi.images.services.ImagesServices;

@Service("imagesServices")
public class ImagesServiceImp extends BaseService implements ImagesServices {

	@Override
	public boolean add(Images entity) {
		entity.setCreateDate(new Date());
		entity.setState(1);
		return this.hibernateBaseDao.addEntity(entity);
	}

	@Override
	public boolean update(Images entity) {
		return this.hibernateBaseDao.udpEntity(entity);
	}

	@Override
	public boolean deleteById(String id) {
		Images entity = this.hibernateBaseDao.getEntity(Images.class, id);
		if (entity != null) {
			this.hibernateBaseDao.delEntity(entity);
		}
		return true;
	}

	@Override
	public boolean batchDelete(String[] ids) {
		if (ids != null && ids.length > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from sys_ad_img where ");
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
	public Images getEntityByName(String name, String orgId) {
		String hql = "from Images where title=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, name, orgId);
	}

	@Override
	public Images getEntityById(String id) {
		return this.hibernateBaseDao.getEntity(Images.class, id);
	}

	@Override
	public Page<Images> getPageList(Page<Images> page, Map<String, Object> pMap) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("from Images where 1=1 ");
		if (pMap.containsKey("orgId")) {
			hql.append(" and orgId =? ");
			pList.add(pMap.get("orgId").toString());
		}
		hql.append(" order by orderNumber ");
		return this.hibernateBaseDao.findPage(page, hql.toString(), pList.toArray());
	}

	@Override
	public List<Map<String, Object>> getImageList(String orgId) {
		List<Object> pList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ID_ as id,title_ as title, order_number as orderNumber,img_url as imgUrl FROM sys_ad_img WHERE 1=1 ");
		if (StringUtils.strIsNotNull("orgId")) {
			sql.append(" and org_id =? ");
			pList.add(orgId);
		}
		sql.append(" order by order_number ");
		return jdbcDao.queryForMap(sql.toString(), pList.toArray());
	}

}
