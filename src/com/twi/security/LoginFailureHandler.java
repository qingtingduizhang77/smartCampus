package com.twi.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;


public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {


	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		if(logger.isDebugEnabled()){
			logger.debug("登录失败");
		}

//		super.onAuthenticationFailure(request, response, exception);
		String str="{\"state\":0,\"msg\":\"用户帐号或密码错误！\"}";
		
		response.getWriter().println(new String(str.getBytes("utf-8"),"utf-8"));
	}

}
