package com.twi.common.quartz;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twi.wechat.services.WxPayServices;

@Component
public class StatHandleJob {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WxPayServices wxPayServicesImp;
	
	public void dataStatistics(){
		logger.info("---------data statistics : " + new Date());
		wxPayServicesImp.statisticsDealData();
		logger.info("---------data statistics : " + new Date());
	}	
}
