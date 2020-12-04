package com.twi.user.web;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twi.base.WebHelper;
import com.twi.base.controller.BaseController;
import com.twi.base.util.MD5;
import com.twi.base.util.StringUtils;
import com.twi.security.domain.SSCUserDetails;
import com.twi.user.domain.User;
import com.twi.user.services.UserServices;

/**
 * 用户
 * 
 * @author ouwt
 *
 */
@Controller
@RequestMapping("admin_back/user")
public class UserController extends BaseController {

	@Resource(name = "userServices")
	private UserServices userServices;

	@ResponseBody
	@RequestMapping(value = "update", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> update(HttpServletRequest request, @RequestParam(value = "oldPwd", required = true) String oldPwd, @RequestParam(value = "newPwd", required = true) String newPwd) {
		Map<String, Object> result = new HashMap<String, Object>();
		String regexPwd = "^[a-z0-9A-Z]{6,16}$";
		Pattern pattern = Pattern.compile(regexPwd);
		Matcher matcher = pattern.matcher(newPwd);
		try {
			SSCUserDetails sscUser = WebHelper.getUser();
			User user = userServices.getUserById(sscUser.getUserId());
			if (user.getPwd().equals(MD5.getMD5Encode(oldPwd).toUpperCase())) {
				if (!matcher.find()) {
					return createErrorResult("新密码格式不正确");
				} else {
					
					user.setPwd(newPwd);
				}
			} else {
				return createErrorResult("旧密码输入错误");
			}
			boolean flag = userServices.updUser(user);

			if (!flag) {
				return createErrorResult("修改失败");
			} else {
				result.put("user", flag);
				return createSuccessResult(result, "修改成功");
			}
		} catch (Exception e) {
			logger.info("修改密码异常" + e.getMessage());
			return createErrorResult("修改异常");
		}

	}

}
