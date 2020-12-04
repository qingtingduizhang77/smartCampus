package com.twi.base.util;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springside.modules.utils.PropertiesLoader;

public class PropertiesUtils {
	private static final PropertiesLoader propertiesLoader = new PropertiesLoader(
			"classpath:application.properties");

//	private final static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

	public static String getProperty(String key) {
//		String value = null;
//		value = propertiesLoader.getProperty(key,null);
//		logger.debug("key=" + key + " value=" + value);
		return getProperty(key,null);
	}

	public static String getProperty(String key, String defaultValue) {
//		String value = null;
//		value = propertiesLoader.getProperty(key, defaultValue);
//		logger.debug("key=" + key + " value=" + value);
		return propertiesLoader.getProperty(key, defaultValue);
	}

	public static int getProperty(String key, int defaultValue) {
		return propertiesLoader.getInteger(key, defaultValue);
	}
}
