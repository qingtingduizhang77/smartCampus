package com.twi.wx.services.imp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.student.domain.SysWxStudent;
import com.twi.wx.services.WxServices;

@Service("wxServices")
public class WxServicesImp extends BaseService implements WxServices {

	/**
	 * getWxOauthJson:<br/>
	 * 获取微信网页授权后的access_token<br/>
	 * 
	 * @param wxAppid
	 *            微信公众号的appId
	 * @param wxAppSecret
	 *            微信公众号的AppSecret
	 * @param wxCode
	 *            用户同意授权，获取的code
	 * @return 返回换取网页授权的个人用户信息
	 */
	public String getWxOauthJson(String wxAppid, String wxAppSecret, String wxCode) {
		String result = "";
		// 通过code换取网页授权access_token
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + wxAppid + "&secret=" + wxAppSecret + "&code=" + wxCode + "&grant_type=authorization_code";
		try {
			StringBuffer buffer = new StringBuffer();
			URL uurl = new URL(url);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) uurl.openConnection();
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod("POST");
			// 设置请求超时
			httpUrlConn.setConnectTimeout(180000); // timeout after 180 seconds
			httpUrlConn.setReadTimeout(180000);// timeout after 180 seconds

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			result = buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public SysWxStudent getWxStudentInfo(String studentId, String studentName, String studentCode) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from SysWxStudent ws where 1=1 ");
		
		
		if (StringUtils.isNotBlank(studentId)) {// 姓名
			hql.append(" and ws.studentId = ? ");
			list.add(studentId);
		}
		if (StringUtils.isNotBlank(studentName)) {// 姓名
			hql.append(" and ws.studentName = ? ");
			list.add(studentName);
		}
		if (StringUtils.isNotBlank(studentCode)) {// 学号
			hql.append(" and ws.studentCode = ? ");
			list.add(studentCode);
		}
		
		
		List<SysWxStudent> dealList = (List<SysWxStudent>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
		if (dealList != null && dealList.size() > 0) {
			return dealList.get(0);
		}
		return null;
	}

	@Override
	public boolean addWxStudent(SysWxStudent wxStudent) {
		return hibernateBaseDao.addEntity(wxStudent);
	}

	@Override
	public SysWxStudent getWxStudentById(String id) {
		return hibernateBaseDao.getEntity(SysWxStudent.class, id);
	}

	@Override
	public boolean delWxStudent(SysWxStudent wxStudent) {
		return hibernateBaseDao.delEntity(wxStudent);
	}

	@Override
	public List<SysWxStudent> getWxStudentList(String openId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select ws.*,ws.id_ as id,i.clazz_name as className,i.major_name as majorName ");
		sql.append(" from sys_wx_student ws ");
		sql.append(" left join sys_student_info i on i.id_ = ws.Student_id ");
		sql.append(" where 1=1 and i.state_ = 1");
		
		if (StringUtils.isNotBlank(openId)) {// 姓名
			sql.append(" and ws.openid_ = ? ");
			list.add(openId);
		}
		
		RowMapper<SysWxStudent> rm = ParameterizedBeanPropertyRowMapper.newInstance(SysWxStudent.class);
		
		return jdbcDao.getListBySql(sql.toString(), rm, list.toArray());
	}
	@Override
	public List<SysWxStudent> getWxStudentList(String orgId,String openId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select ws.*,ws.id_ as id,i.clazz_name as className,i.major_name as majorName ");
		sql.append(" from sys_wx_student ws ");
		sql.append(" left join sys_student_info i on i.id_ = ws.Student_id ");
		sql.append(" where 1=1 and i.state_ = 1");
		
		if (StringUtils.isNotBlank(orgId)) {// 学校
			sql.append(" and i.org_id = ? ");
			list.add(orgId);
		}
		if (StringUtils.isNotBlank(openId)) {// openid
			sql.append(" and ws.openid_ = ? ");
			list.add(openId);
		}
		
		RowMapper<SysWxStudent> rm = ParameterizedBeanPropertyRowMapper.newInstance(SysWxStudent.class);
		
		return jdbcDao.getListBySql(sql.toString(), rm, list.toArray());
	}

}
