package com.twi.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.twi.security.domain.SSCUserDetails;

import net.sf.json.JSONObject;

public class LoginSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		if(logger.isDebugEnabled()){
			logger.debug("登录成功");
		}
		SSCUserDetails userDetails = (SSCUserDetails)authentication.getPrincipal();
		//String userId = request.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);

		
		String rolesStr=null;
		List<Map<String,Object>> roles=userDetails.getRoles();
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("state", 1);
		jsonObject.put("msg", "登录成功！");
		jsonObject.put("userId", userDetails.getUserId());
		jsonObject.put("userName", userDetails.getUsername());
		jsonObject.put("roles", roles);
		jsonObject.put("sessionId", request.getSession().getId());
		jsonObject.put("orgId", userDetails.getOrgId());
		jsonObject.put("orgName", userDetails.getOrgName());
		
		
		
		
		
		//登录日志
		logger.info(String.format("【%s】登录日志", userDetails.getUserId()));		
		

		
		//super.onAuthenticationSuccess(request, response, authentication);
       
		String str=jsonObject.toString();
		response.getWriter().println(new String(str.getBytes("utf-8"),"utf-8"));
	}

}
