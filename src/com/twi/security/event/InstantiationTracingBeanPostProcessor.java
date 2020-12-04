package com.twi.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.twi.base.ContextService;
import com.twi.base.SpringContextHolder;
/**
 * 
 * @author ouwt
 *
 */
public class InstantiationTracingBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>{

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		 if(event.getApplicationContext().getParent() == null){//root application context 没有parent，他就是老大.
	          //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
			 ContextService contextService = SpringContextHolder.getBean(ContextService.class);
			 logger.info(" --------------- 执行初始化数据 ---------------");
			 contextService.init();
	      }
	}

}
