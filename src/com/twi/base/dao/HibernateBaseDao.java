package com.twi.base.dao;

import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;

/**
 * Hibernate通用操作DAO
 * 
 * @author ouwt
 *
 */
public interface HibernateBaseDao {

	/**
	 * 更新数据
	 * 
	 * @param sql
	 *            更新SQL
	 * @param values
	 *            数量可变的查询参数,按顺序绑定.
	 * @return
	 */
	public boolean bulkUpdate(String sql, Object... values);

	/**
	 * 删除实体
	 * 
	 * @param entity
	 *            实体
	 * @return
	 */
	public boolean delEntity(Object entity);

	/**
	 * 更新实体
	 * 
	 * @param entity
	 *            实体
	 * @return
	 */
	public boolean udpEntity(Object entity);

	/**
	 * 新增实体
	 * 
	 * @param entity
	 *            实体
	 * @return
	 */
	public boolean addEntity(Object entity);

	/**
	 * 根据主键取实体
	 * 
	 * @param clazz
	 *            实体 。class
	 * @param id
	 *            主键
	 * @return
	 */
	public <T> T getEntity(Class<T> clazz, String id);

	public List<?> getEntityList(String hql, Object[] objs);

	public List<Map<String, Object>> getEntityMap(String hql, Object[] objs);

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param hql
	 *            hql语句
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 */
	public <T> T findUnique(final String hql, final Object... values);

	/**
	 * 按HQL分页查询.
	 * 
	 * @param page
	 *            分页参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            数量可变的查询参数,按顺序绑定.
	 * @return
	 */
	public <T> Page<T> findPage(Page<T> page, String hql, Object... values);

	public Page<Map<String, Object>> findPageMap(Page<Map<String, Object>> page, String hql, Object... values);

}
