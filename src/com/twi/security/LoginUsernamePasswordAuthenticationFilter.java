package com.twi.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.twi.base.services.CommonServices;
import com.twi.base.util.MD5;
import com.twi.base.util.PropertiesUtils;
import com.twi.user.domain.User;
import com.twi.user.services.UserServices;

/**
 * Servlet Filter implementation class LoginUsernamePasswordAuthenticationFilter
 */
public class LoginUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	
	
	@Autowired
	private UserServices userServices;
	@Autowired
	private CommonServices commonServices;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
		//检测验证码
		//checkValidateCode(request);
		
		
		String orgId=(String) request.getSession().getAttribute("orgId");
		if(orgId==null)
		{
			String serverName=request.getServerName();
			orgId=commonServices.getOrgIdByServerName(serverName);
			if(orgId==null)
			{
				orgId=PropertiesUtils.getProperty("testorgId");
			}
		}
		
		
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		//验证用户账号与密码是否对应
		username = username.trim();
		boolean isExists =false;
		
		
		User user=userServices.getUserByAccount(username.trim(),orgId);
		
		if(user!=null )
		{
			isExists=true;
		}
	
		if(!isExists) {
			throw new AuthenticationServiceException("用户账号不存在!!"); 
		}
		
		if(!user.getPwd().equals(password))
		{
			throw new AuthenticationServiceException("密码不正确！"); 
		}
	
		username=username+"##"+orgId;
		
		//UsernamePasswordAuthenticationToken实现 Authentication
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		// Place the last username attempted into HttpSession for views
		
		// 允许子类设置详细属性
        setDetails(request, authRequest);
		
        // 运行UserDetailsService的loadUserByUsername 再次封装Authentication
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	

	
	
	/**
	 * 取用户名
	 */
	@Override
	protected String obtainUsername(HttpServletRequest request) {
		Object obj = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		return null == obj ? "" : obj.toString();
	}

	/**
	 * 取密码
	 */
	@Override
	protected String obtainPassword(HttpServletRequest request) {
		try{
		Object obj = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
		return null == obj ? "" :MD5.getMD5Encode(obj.toString()).toUpperCase() ;
		} catch (Exception e) {
			
		}
		return "";
		
	}
	
	
}
