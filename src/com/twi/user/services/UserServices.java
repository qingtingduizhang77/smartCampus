package com.twi.user.services;

import com.twi.base.domain.Page;
import com.twi.user.domain.User;

public interface UserServices {
	
	boolean addUser(User user);
	
	boolean updUser(User user);
	
	User getUserByAccount(String account,String orgId);
	
	User getUserById(String id);
	
	boolean delUserById(String id);
	
	boolean batchDelUser(String[] ids);
	
	Page<User>  getUserPage(Page<User> page ,User user);
	

}
