package com.twi.base.controller;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.twi.base.util.DateUtils;
import com.twi.base.util.JsonUtils;

import net.sf.json.JSONObject;

public class BaseController {
	
	/**
	@RequestMapping(value="operate/transfer",method=RequestMethod.POST)
	@Token(remove=true)
	@Permission
	public ModelAndView doTransfer(HttpServletRequest request, ModelMap map){
		map.addAttribute("status", "-9");
		map.addAttribute("tips", "亲，您还没有被授权！")
		return new ModelAndView(url,map);
	}
	*/
	
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获取String类型参数值
	 * 
	 * @param request
	 * @param paramName
	 *            参数名
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	protected String getString(HttpServletRequest request, String paramName, String defaultValue) {
		String value = request.getParameter(paramName);
		if (null == value || 0 == value.trim().length()) {
			return defaultValue;
		} else {
			return value.trim();
		}
	}

	/**
	 * 获取Integer类型参数值
	 * 
	 * @param request
	 * @param paramName
	 *            参数名
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	protected Integer getInt(HttpServletRequest request, String paramName, Integer defaultValue) {
		String value = getString(request, paramName, null);
		if (null != value) {
			try {
				return Integer.valueOf(value.trim());
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}

	/**
	 * 获取Double类型参数值
	 * 
	 * @param request
	 * @param paramName
	 *            参数名
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	protected Double getDouble(HttpServletRequest request, String paramName, Double defaultValue) {
		String value = getString(request, paramName, null);
		if (null != value) {
			try {
				return Double.valueOf(value.trim());
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}

	/**
	 * 获取Long类型参数值
	 * 
	 * @param request
	 * @param paramName
	 *            参数名
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	protected Long getLong(HttpServletRequest request, String paramName, Long defaultValue) {
		String value = getString(request, paramName, null);
		if (null != value) {
			try {
				return Long.valueOf(value.trim());
			} catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	
	
	protected String[] getStrArray(HttpServletRequest request, String paramName)
	{
		
		String[] value=request.getParameterValues(paramName);
		
		if (null == value || 0 == value.length) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * 获取Boolean类型参数值
	 * 
	 * @param request
	 * @param paramName
	 *            参数名
	 * @param defaultValue
	 *            默认值
	 * @return
	 */
	protected Boolean getBoolean(HttpServletRequest request, String paramName, Boolean defaultValue) {
		String value = getString(request, paramName, null);
		if (null != value) {
			try {
				return Integer.valueOf(value) != 0;
			} catch (Exception e) {
			}
			try {
				return Boolean.valueOf(value);
			} catch (Exception e) {
			}
		}
		return defaultValue;
	}
	
	/**
	 * 获取Date类型参数值
	 */
	protected Date getDate(HttpServletRequest request, String paramName, Date defaultValue) {
		String value = getString(request, paramName, null);
		if (null != value) {
			try {
				return DateUtils.parse(value, DateUtils.C_TIME_PATTON_DEFAULT);
			} catch (Exception e) {
				try {
					return DateUtils.parse(value, DateUtils.C_DATE_PATTON_DEFAULT);
				} catch (Exception e2) {
				}
			}
		}
		return defaultValue;
	}
	
	protected <T> T toBeanFromJson(Class<T> clazz, String jsonStr) {
		return JsonUtils.toBeanFromJson(clazz, jsonStr);
	}
	
	protected <T> T toBeanFromJson(Class<T> clazz, String jsonStr, Map<String, Class<?>> classMap) {
		return JsonUtils.toBeanFromJson(clazz, jsonStr, classMap);
	}
	
	protected <T> List<T> toBeanListFromJson(Class<T> clazz, String jsonStr) {
		return JsonUtils.toBeanListFromJson(clazz, jsonStr);
	}
	
	protected <T> List<T> toBeanListFromJson(Class<T> clazz, String jsonStr, Map<String, Class<?>> classMap) {
		return JsonUtils.toBeanListFromJson(clazz, jsonStr, classMap);
	}
	
	protected Map<String, Object> createSuccessResult(Object content) {
		return createSuccessResult(content, "");
	}
	
	protected Map<String, Object> createSuccessResult(Object content, String msg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("state", "1");
		resultMap.put("msg", StringUtils.isEmpty(msg) ? "" : msg);
		resultMap.put("content", (content == null) ? "" : content);
		return resultMap;
	}
	
	protected Map<String, Object> createErrorResult(String errorMsg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("state", "0");
		resultMap.put("msg", (errorMsg == null) ? "" : errorMsg);
		return resultMap;
	}
	
	protected Map<String, Object> createErrorResult(String errorState,String errorMsg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("state", (errorMsg == null) ? 0 : errorState);
		resultMap.put("msg", (errorMsg == null) ? "" : errorMsg);
		return resultMap;
	}
	
	protected void render(HttpServletResponse response, final String contentType, final String content) {
		try {
			String encoding = "UTF-8";
			boolean noCache = true;
			String fullContentType = contentType + ";charset=" + encoding;
			response.setContentType(fullContentType);
			if (noCache) {
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
			}

			response.getWriter().write(content);
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	protected void renderJson(HttpServletResponse response, Map<String, ?> map) {
		String jsonString = JSONObject.fromObject(map).toString();
		render(response, "application/json", jsonString);
	}


	protected String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	
	
}