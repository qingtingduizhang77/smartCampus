package com.twi.security.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserFilter implements Filter {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private Set<String> exempts;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException,
			ServletException {
		// logger.info("【BrowserFilter】Start ...");
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	  //  response.setHeader("Access-Control-Max-Age", "3600");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/json; charset=utf-8");  
	   arg2.doFilter(arg0, arg1);
//		String uri = request.getRequestURI();
//		int p = uri.indexOf("/", 1);
//		if (p > 0) {
//			uri = uri.substring(p, uri.length());
//		}
//		if (this.isExempted(uri)) {
//			arg2.doFilter(arg0, arg1);
//			return;
//		}
//
//		response.setHeader("Set-Cookie", "name=value; HttpOnly");
//		String ua = request.getHeader("User-Agent");
//		if(null != ua && !"".equals(ua)){
//			UserAgent userAgent = UserAgent.parseUserAgentString(ua);
//			Browser browser = userAgent.getBrowser();
//			OperatingSystem os = userAgent.getOperatingSystem();
//			logger.debug("客户端操作系统-------------------" + os.getName());
//			logger.debug("客户端浏览器名称 -------------------" + browser.name());
//			logger.debug("客户端浏览器类型 -------------------" + browser.getBrowserType());
////			if (browser.getBrowserType() != BrowserType.UNKNOWN) {
//				arg2.doFilter(arg0, arg1);
////			} else {
////				logger.error("无效的客户端访问！");
////				response.sendRedirect("err.jsp?errmsg=browser");
////				return;
////			}
//		}else{
//			logger.error("无效的客户端访问！"); 
//			response.setCharacterEncoding("UTF-8");  
//		    response.setContentType("application/json; charset=utf-8");  
//		    PrintWriter out = null;  
//		    try {  
//		        out = response.getWriter();  
//		        out.append("{'code':'500','error':'无效的访问，请更换访问方式!'}");  
////		        logger.debug("返回是\n");   
////		        logger.debug(responseJSONObject.toString());  
//		    } catch (IOException e) {  
//		        e.printStackTrace();  
//		    } finally {  
//		        if (out != null) {  
//		            out.close();  
//		        }  
//		    }  
//		}
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	private boolean isExempted(String uri) {
		boolean exempted = false;
		for (String exempt : this.exempts) {
			if (uri.startsWith(exempt)) {
				exempted = true;
				break;
			}
		}
		return exempted;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		exempts = new HashSet<String>();
		String exempt = config.getInitParameter("Exempt");
		logger.info("exempt:" + exempt);
		if (exempt != null) {
			StringTokenizer st = new StringTokenizer(exempt, ",");
			while (st.hasMoreTokens()) {
				this.exempts.add(st.nextToken().trim());
			}
		}
	}

}
