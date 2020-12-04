package com.twi.common.quartz;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twi.wechat.services.WxPayServices;

@Component
public class BillHandleJob {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WxPayServices wxPayServicesImp;
	
	public void downloadBill(){
		logger.info("---------download bill : " + new Date());
		wxPayServicesImp.quartzDownloadBill();
		logger.info("---------download bill : " + new Date());
	}	
}
