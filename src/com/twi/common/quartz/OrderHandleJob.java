package com.twi.common.quartz;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.twi.wechat.services.WxPayServices;

@Component
public class OrderHandleJob {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WxPayServices wxPayServicesImp;
	
	@Scheduled(cron ="0 1 0 * * ?")
	public void updateState(){
		logger.info("start update state : " + new Date());
		wxPayServicesImp.quartzUpdateOrderState();
		logger.info("end update state : " + new Date());
	}	
}
