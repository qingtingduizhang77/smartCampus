package com.twi.security.event;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.twi.base.WebHelper;
import com.twi.security.domain.SSCUserDetails;
import com.twi.security.service.Token;


/**
 * <p>
 * 防止重复提交过滤器
 * </p>
 *
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {
	private Logger logger = LoggerFactory.getLogger(getClass());
 
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
    	//logger.debug("【TokenInterceptor】 start -------------------------------------------- ");
        SSCUserDetails user = null;
        try {
			user = WebHelper.getUser();
		} catch (Exception e) {
			//logger.error("【TokenInterceptor】" + e.getMessage());
		}
        if (user != null) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
 
            Token annotation = method.getAnnotation(Token.class);
            if (annotation != null) {
                boolean needSaveSession = annotation.save();
                if (needSaveSession) {
                	logger.info("【TokenInterceptor】 create token -------------------------------------------- ");
                    request.getSession(false).setAttribute("token", TokenProcessor.getInstance().generateToken(request));
                }
 
                boolean needRemoveSession = annotation.remove();
                if (needRemoveSession) {
                	logger.info("【TokenInterceptor】 remove token -------------------------------------------- ");
                    if (isRepeatSubmit(request)) {
                        logger.warn("please don't repeat submit,[user:" + user.getUsername() + ",url:"  + request.getServletPath() + "]");
                        return false;
                    }
                    request.getSession(false).removeAttribute("token");
                }
            }
        }
        return true;
    }
 
    private boolean isRepeatSubmit(HttpServletRequest request) {
        String serverToken = (String) request.getSession(false).getAttribute("token");
        if (serverToken == null) {
            return true;
        }
        String clinetToken = request.getParameter("token");
        if (clinetToken == null) {
            return true;
        }
        if (!serverToken.equals(clinetToken)) {
            return true;
        }
        return false;
    }
 
}