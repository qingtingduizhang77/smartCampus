package com.twi.base.util;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

/**
 * 返回值格式化
 * @author lihw
 *
 */
public class ResultFormat {
	public static JSONObject trim(JSONObject json) {
		try {
			JsonConfig jsonConf = new JsonConfig();
			jsonConf.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object value) {
					try {
						if (value==null || value.equals(null) || "".equals(value)) {
							return true;
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					return false;
				}
			});
			json = JSONObject.fromObject(json, jsonConf);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 失败JSON格式
	 * @param msg	失败提示
	 * @return
	 */
	public static String jsonStrFail(String msg) {
		JSONObject json_result = new JSONObject();
		json_result.put("return_code", "FAIL");
		json_result.put("return_msg", msg);
		return trim(json_result).toString();
	}

	/**
	 * 成功JSON格式
	 * @param msg		成功提示
	 * @param content	内容对象（一般为JsonString或JsonObject）
	 * @return
	 */
	public static String jsonStrSuccess(String msg, Object content) {
		return jsonStrSuccess(msg, content, null);
	}

	/**
	 * 成功JSON格式
	 * @param content	内容对象（一般为JsonString或JsonObject）
	 * @return
	 */
	public static String jsonStrSuccess(Object content) {
		return jsonStrSuccess(null, content, null);
	}

	/**
	 * 成功JSON格式
	 * @param content	内容对象（一般为JsonString或JsonObject）
	 * @param session	会话对象（一般为JsonString或JsonObject）
	 * @return
	 */
	public static String jsonStrSuccess(Object content, Object session) {
		return jsonStrSuccess(null, content, session);
	}

	/**
	 * 成功JSON格式
	 * @param msg		成功提示
	 * @param content	内容对象（一般为JsonString或JsonObject）
	 * @param session	会话对象（一般为JsonString或JsonObject）
	 * @return
	 */
	public static String jsonStrSuccess(String msg, Object content, Object session) {
		String result = jsonStrFail("strJsonSuccess格式化异常！");
		try {
			JSONObject json_content = null;
			JSONObject json_session = null;
			if(content!=null && StringUtils.isNotBlank(content.toString())) {
				json_content = JSONObject.fromObject(content);
			}
			if(session!=null && StringUtils.isNotBlank(session.toString())) {
				json_session = JSONObject.fromObject(session);
			}
			JSONObject json_result = new JSONObject();
			json_result.put("return_code", "SUCCESS");
			json_result.put("return_msg", msg);
			if(json_content!=null && json_content.has("return_code")) {
				return json_content.toString();
			} else {
				json_result.put("return_content", json_content);
			}
			json_result.put("return_session", json_session);

			result = trim(json_result).toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
