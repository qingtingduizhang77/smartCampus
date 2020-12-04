package com.twi.student.services.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.honzel.core.util.BeanHelper;
import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.CalendarUtils;
import com.twi.base.util.StringUtils;
import com.twi.student.domain.SysStudent;
import com.twi.student.services.SysStudentServices;

@Service("sysStudentServicesImp")
public class SysStudentServicesImp extends BaseService implements SysStudentServices{

	@Override
	public Page<SysStudent> getStudentPage(Page<SysStudent> page, SysStudent student, String startDate,
			String endDate) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from SysStudent s where 1=1 ");
		if (StringUtils.isNotBlank(student.getState())) {
			hql.append(" and s.state = ? ");
			list.add(student.getState());
		}
		if (StringUtils.isNotBlank(student.getName())) {// 名称
			hql.append(" and s.name like ? ");
			list.add("%" + student.getName() + "%");
		}
		if (StringUtils.isNotBlank(student.getCode())) {// 学号
			hql.append(" and s.code like ? ");
			list.add("%" + student.getCode() + "%");
		}
		if (StringUtils.isNotBlank(student.getNation())) {// 民族
			hql.append(" and s.nation like ? ");
			list.add("%" + student.getNation() + "%");
		}
		if (StringUtils.isNotBlank(student.getClassName())) {// 班级
			hql.append(" and s.className like ? ");
			list.add("%" + student.getClassName() + "%");
		}
		if (StringUtils.isNotBlank(student.getGradeName())) {// 年级
			hql.append(" and s.gradeName like ? ");
			list.add("%" + student.getGradeName() + "%");
		}
		if (StringUtils.isNotBlank(student.getMajorName())) {// 专业名称
			hql.append(" and s.majorName like ? ");
			list.add(student.getMajorName());
		}
		if (StringUtils.isNotBlank(startDate)) {// 入学日期
			hql.append(" and s.entrance >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {//  入学日期
			hql.append(" and s.entrance <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(student.getOrgId())) {// 机构ID
			hql.append(" and s.orgId = ? ");
			list.add(student.getOrgId());
		}
		
		hql.append("  order by s.createDate desc  ");

		return this.hibernateBaseDao.findPage(page, hql.toString(), list.toArray());
	}

	@Override
	public boolean saveOrUpdateStudent(List<SysStudent> studentList, String orgId) {
		List<String> sqlList = new ArrayList<String>();
		if (studentList != null && studentList.size() > 0) {
			for (SysStudent student : studentList) {
				StringBuffer sql = new StringBuffer();
				sql.append(" insert into sys_student_info ");
				sql.append(" (id_,code_,org_id,name_,id_card,sex_");
				if (student.getEntrance() != null) {
					sql.append(",entrance_");
				}
				sql.append(" ,major_name,grade_name,clazz_name,Nation_,Native_place,Political_,address_,state_,creater_date) ");
				
				
				sql.append(" values ('"+StringUtils.randomUUID()+"','"+student.getCode()+"',");
				sql.append("'"+orgId+"','"+student.getName()+"','"+student.getIdCard()+"','"+student.getSex()+"',");
				if (student.getEntrance() != null) {
					sql.append("'"+CalendarUtils.format(student.getEntrance(), "yyyy-MM-dd")+"',");
				}
				sql.append("'"+student.getMajorName()+"','"+student.getGradeName()+"','"+student.getClassName()+"',");
				sql.append("'"+student.getNation()+"','"+student.getNativePlace()+"','"+student.getPolitical()+"','"+student.getAddress()+"',1,NOW())");
				
				
				sql.append(" ON DUPLICATE KEY UPDATE ");
				sql.append(" name_ = '"+student.getName()+"',id_card = '"+student.getIdCard()+"',sex_ = '"+student.getSex()+"',major_name = '"+student.getMajorName()+"',grade_name = '"+student.getGradeName()+"',clazz_name = '"+student.getClassName()+"',");
				if (student.getEntrance() != null) {
					sql.append("entrance_ = '"+CalendarUtils.format(student.getEntrance(), "yyyy-MM-dd")+"',");
				}
				sql.append(" Nation_ = '"+student.getNation()+"',Native_place = '"+student.getNativePlace()+"',Political_ = '"+student.getPolitical()+"',address_ = '"+student.getAddress()+"',state_ = 1");
				
				sqlList.add(sql.toString());
			}
		}
		if (sqlList != null && sqlList.size() > 0) {
			return jdbcDao.batchExecuteSql(sqlList);
		}else{
			return true;
		}
	}

	@Override
	public boolean updateStudent(SysStudent student) {
		boolean flag = true;
		try {
			if (StringUtils.isNotEmpty(student.getId())) {
				SysStudent student1 = getStudentById(student.getId());
				Date createDate = student1.getCreateDate();
				BeanHelper.copyProperties(student, student1);
				student1.setCreateDate(createDate);
				hibernateBaseDao.udpEntity(student1);
			}else{
				student.setCreateDate(new Date());
				hibernateBaseDao.addEntity(student);
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	@Override
	public SysStudent getStudentById(String id) {
		return hibernateBaseDao.getEntity(SysStudent.class, id);
	}

	@Override
	public boolean updateState(String ids, String state) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append("update SysStudent set state = ? where id in ( ");
		list.add(state);
		String[] idsArr = ids.split(",");
		for (int i = 0; i < idsArr.length; i++) {
			hql.append("'" + idsArr[i] + "'");
			if (idsArr.length - i != 1) {
				hql.append(",");
			}
		}
		hql.append(")");
		
		return hibernateBaseDao.bulkUpdate(hql.toString(), list.toArray());
	}

	@Override
	public boolean batchDelete(String orgId) {
		StringBuffer hql = new StringBuffer();

		hql.append("update SysStudent set state = 2 where orgId = ?");
		return hibernateBaseDao.bulkUpdate(hql.toString());
	}

	@Override
	public List<Map<String, Object>> getStudentList(SysStudent student, String startDate, String endDate) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();

		sql.append(" select * from sys_student_info s where 1=1 ");
		if (StringUtils.isNotBlank(student.getState())) {// 状态
			sql.append(" and s.state_ = ? ");
			list.add(student.getState());
		}
		if (StringUtils.isNotBlank(student.getName())) {// 名称
			sql.append(" and s.name_ like ? ");
			list.add("%" + student.getName() + "%");
		}
		if (StringUtils.isNotBlank(student.getCode())) {// 学号
			sql.append(" and s.code_ like ? ");
			list.add("%" + student.getCode() + "%");
		}
		if (StringUtils.isNotBlank(student.getNation())) {// 民族
			sql.append(" and s.nation_ like ? ");
			list.add("%" + student.getNation() + "%");
		}
		if (StringUtils.isNotBlank(student.getClassName())) {// 班级
			sql.append(" and s.clazz_name like ? ");
			list.add("%" + student.getClassName() + "%");
		}
		if (StringUtils.isNotBlank(student.getGradeName())) {// 年级
			sql.append(" and s.grade_name like ? ");
			list.add("%" + student.getGradeName() + "%");
		}
		if (StringUtils.isNotBlank(student.getMajorName())) {// 专业名称
			sql.append(" and s.major_name like ? ");
			list.add(student.getMajorName());
		}
		if (StringUtils.isNotBlank(startDate)) {// 入学日期
			sql.append(" and s.entrance_ >=? ");
			list.add(CalendarUtils.parseDate(startDate));
		}
		if (StringUtils.isNotBlank(endDate)) {//  入学日期
			sql.append(" and s.entrance_ <=? ");
			list.add(CalendarUtils.dayEnd(endDate));
		}
		if (StringUtils.isNotBlank(student.getOrgId())) {// 机构ID
			sql.append(" and s.org_id = ? ");
			list.add(student.getOrgId());
		}else{// 无数据导出
			sql.append(" and s.org_id = '11111111' ");
		}
		
		sql.append(" order by s.creater_date desc ");

		return jdbcDao.queryForMap(sql.toString(), list.toArray());
	}

	@Override
	public SysStudent getStudentByNameAndCode(String orgId, String studentName, String studentCode, String state) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();

		hql.append(" from SysStudent s where 1=1 ");
		if (StringUtils.isNotBlank(state)) {
			hql.append(" and s.state = ? ");
			list.add(state);
		}
		if (StringUtils.isNotBlank(studentName)) {// 姓名
			hql.append(" and s.name = ? ");
			list.add(studentName);
		}
		if (StringUtils.isNotBlank(studentCode)) {// 学号
			hql.append(" and s.code = ? ");
			list.add(studentCode);
		}
		if (StringUtils.isNotBlank(orgId)) {// 机构ID
			hql.append(" and s.orgId = ? ");
			list.add(orgId);
		}
		
		List<SysStudent> dealList = (List<SysStudent>) hibernateBaseDao.getEntityList(hql.toString(), list.toArray());
		if (dealList != null && dealList.size() > 0) {
			return dealList.get(0);
		}
		return null;
	}
}
