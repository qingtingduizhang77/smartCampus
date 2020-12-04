package com.twi.security.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twi.base.services.CommonServices;
import com.twi.security.domain.SSCUserDetails;
import com.twi.user.domain.SysOrgInfo;
import com.twi.user.domain.User;
import com.twi.user.services.UserServices;

@Transactional
@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Resource(name = "userServices")
	private UserServices userServices;
	@Autowired
	private CommonServices commonServices;
	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		String[] data=name.split("##");
		User user=userServices.getUserByAccount(data[0],data[1]);
		List<Map<String,Object>> roles=commonServices.getRole(user.getId());
		SysOrgInfo org=commonServices.getSysOrgInfoById(user.getOrgId());
		SSCUserDetails userDetail = new SSCUserDetails(user.getId(), user.getName(), user.getPwd(),user.getAccount(),user.getOrgId(),org.getName(),roles);
		return userDetail;
	}

}
