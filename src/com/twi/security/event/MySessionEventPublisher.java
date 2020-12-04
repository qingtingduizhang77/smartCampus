package com.twi.security.event;

import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

public class MySessionEventPublisher extends HttpSessionEventPublisher {
	private Log logger = LogFactory.getLog(MySessionEventPublisher.class);

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		super.sessionCreated(event);
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
//				Object principal = auth.getPrincipal();
				
			} else {
				String sessionId = event.getSession().getId();
				logger.info(String.format("%s %s",	"##sessionDestroyed#can't find prrincipal", sessionId));
			
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		super.sessionDestroyed(event);
	}
}
