package com.twi.user.services.imp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.MD5;
import com.twi.base.util.StringUtils;
import com.twi.user.domain.User;
import com.twi.user.services.UserServices;

@Service("userServices")
public class UserServicesImp extends BaseService implements UserServices {

	@Override
	public boolean addUser(User user) {
		try {
			user.setPwd(MD5.getMD5Encode(user.getPwd()).toUpperCase());
		} catch (Exception e) {
			throw new AuthenticationServiceException("用户密码加密出错啦!!");
		}
		return this.hibernateBaseDao.addEntity(user);
	}

	@Override
	public boolean updUser(User user) {
		User user2 = hibernateBaseDao.getEntity(User.class, user.getId());
		String oldPwd = user2.getPwd() == null ? "" : user2.getPwd();
		String newPwd = "";
		try {
			newPwd = user.getPwd() == null ? "" : MD5.getMD5Encode(user.getPwd().trim()).toUpperCase();
		} catch (Exception e) {
			throw new AuthenticationServiceException("用户密码加密出错啦!!");
		}

		if (!oldPwd.equals(newPwd)) {
			user.setPwd(newPwd);
		}

		return this.hibernateBaseDao.udpEntity(user);
	}

	@Override
	public User getUserByAccount(String account, String orgId) {
		String hql = " from User where account=? and orgId=? ";
		return this.hibernateBaseDao.findUnique(hql, account, orgId);
	}

	@Override
	public User getUserById(String id) {

		return this.hibernateBaseDao.getEntity(User.class, id);
	}

	@Override
	public boolean delUserById(String id) {
		User user = this.hibernateBaseDao.getEntity(User.class, id);
		if (user != null) {
			this.hibernateBaseDao.delEntity(user);
		}
		return true;
	}

	@Override
	public boolean batchDelUser(String[] ids) {
		if (ids != null && ids.length > 0) {

			StringBuffer sql = new StringBuffer();
			sql.append("delete from  sys_user where  ");

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
	public Page<User> getUserPage(Page<User> page, User user) {
		List<Object> objList = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from User where 1=1 ");
		if (user != null) {
			if (StringUtils.strIsNotNull(user.getName())) {
				hql.append(" and name like ? ");
				objList.add("%" + user.getName() + "%");
			}

			if (StringUtils.strIsNotNull(user.getAccount())) {
				hql.append(" and account like ? ");
				objList.add("%" + user.getAccount() + "%");
			}
		}

		Object[] obj = new Object[objList.size()];
		for (int j = 0; j < objList.size(); j++) {
			obj[j] = objList.get(j);
		}

		return this.hibernateBaseDao.findPage(page, hql.toString(), obj);
	}

}
