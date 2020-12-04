package com.twi.base.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

@SuppressWarnings("unchecked")
public class JsonUtils {

	public static <T> T toBeanFromJson(Class<T> clazz, String jsonStr) {
		Assert.hasText(jsonStr);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);// 据说是为了防止自己包含自己的死循环情况.
		return (T) JSONObject.toBean(JSONObject.fromObject(jsonStr, jsonConfig), clazz);
	}

	public static <T> T toBeanFromJson(Class<T> clazz, String jsonStr, Map<String, Class<?>> classMap) {
		Assert.hasText(jsonStr);
		return (T) JSONObject.toBean(JSONObject.fromObject(jsonStr), clazz, classMap);
	}

	public static <T> List<T> toBeanListFromJson(Class<T> clazz, String jsonStr) {
		Assert.hasText(jsonStr);
		List<T> result = new ArrayList<T>();
		JSONArray array = JSONArray.fromObject(jsonStr);
		for (int i = 0, j = array.size(); i < j; i++) {
			result.add((T) JSONObject.toBean(array.getJSONObject(i), clazz));
		}
		return result;
	}

	public static <T> List<T> toBeanListFromJson(Class<T> clazz, String jsonStr, Map<String, Class<?>> classMap) {
		Assert.hasText(jsonStr);
		List<T> result = new ArrayList<T>();
		JSONArray array = JSONArray.fromObject(jsonStr);
		for (int i = 0, j = array.size(); i < j; i++) {
			result.add((T) JSONObject.toBean(array.getJSONObject(i), clazz, classMap));
		}
		return result;
	}

	public static String toJsonString(Object bean) {
		Assert.notNull(bean);
		String jsonString = JSONObject.fromObject(bean).toString();
		return jsonString;
	}
	
	/**
	 * 获取对象的属性及属性值
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> getObjValueMap(Object obj) {
		if (obj == null) {
			return new HashMap<String, Object>();
		}
		Map<String, Object> valueMap = getParentValueMap(obj);
		Field[] fields = obj.getClass().getDeclaredFields();
		Field.setAccessible(fields, true);
		List<Class<?>> typeList = Arrays.asList(new Class<?>[] { String.class, Integer.class, Date.class,
				Boolean.class, Long.class, Double.class });
		for (Field field : fields) {
			try {
				if (typeList.contains(field.getType())) {
					Object value = field.get(obj);
					if (value == null && field.getType().equals(String.class)) {
						value = "";
					} if (value != null && field.getType().equals(Double.class)) {
						value = DataUtils.format((Double) value, 2);
					}
					valueMap.put(field.getName(), value);
				}
			} catch (Exception e) {
			}
		}
		return valueMap;
	}
	/**
	 * 获取对象父类的属性及属性值
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> getParentValueMap(Object obj) {
		if (obj == null) {
			return new HashMap<String, Object>();
		}
		Field[] fields = obj.getClass().getSuperclass().getDeclaredFields();
		Field.setAccessible(fields, true);
		List<Class<?>> typeList = Arrays.asList(new Class<?>[] { String.class, Integer.class, Date.class,
				Boolean.class, Long.class, Double.class });
		Map<String, Object> valueMap = new HashMap<String, Object>();
		for (Field field : fields) {
			try {
				if (typeList.contains(field.getType())) {
					Object value = field.get(obj);
					if (value == null && field.getType().equals(String.class)) {
						value = "";
					}
					valueMap.put(field.getName(), value);
				}
			} catch (Exception e) {
			}
		}
		return valueMap;
	}
}

