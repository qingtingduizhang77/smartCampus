package com.twi.base.util;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springside.modules.utils.PropertiesLoader;

public class PropertiesMsg {
	private static final PropertiesLoader propertiesLoader = new PropertiesLoader(
			"classpath:return_msg.properties");

//	private final static Logger logger = LoggerFactory.getLogger(PropertiesMsg.class);
	public static String getProperty(String key) {
		String value = null;
		try {
			value = propertiesLoader.getProperty(key);
			value = new String(value.getBytes("ISO8859-1"), "UTF-8");
//			logger.debug("key=" + key + " value=" + value);
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		return value;
	}

	public static String getProperty(String key, String defaultValue) {
		String value = null;
		try {
			value = propertiesLoader.getProperty(key, defaultValue);
//			logger.debug("key=" + key + " value=" + value);
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		return value;
	}

	public static int getProperty(String key, int defaultValue) {
		return propertiesLoader.getInteger(key, defaultValue);
	}
}
