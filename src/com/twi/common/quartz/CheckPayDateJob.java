package com.twi.common.quartz;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twi.wechat.services.WxPayServices;

@Component
public class CheckPayDateJob {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	WxPayServices wxPayServices;
	
	public void checkPayDate(){
		logger.info("--------检查学校支付周期----" + new Date());
		wxPayServices.checkPayDate();
	}
	
}
