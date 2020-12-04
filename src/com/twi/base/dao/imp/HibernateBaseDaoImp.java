package com.twi.base.dao.imp;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.twi.base.dao.HibernateBaseDao;
import com.twi.base.domain.Page;
import com.twi.base.util.StringUtils;

@Service("hibernateBaseDao")
public class HibernateBaseDaoImp extends HibernateDaoSupport implements HibernateBaseDao {

	@Autowired
	public void setSessionFactoryOverride(SessionFactory sessionFactory) {

		super.setSessionFactory(sessionFactory);
	}

	@Override
	public boolean delEntity(Object entity) {
		try {
			this.getHibernateTemplate().delete(entity);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean udpEntity(Object entity) {
		try {
			this.getHibernateTemplate().update(entity);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean addEntity(Object entity) {
		try {
			this.getHibernateTemplate().save(entity);// .save(entityName, entity);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public <T> T getEntity(Class<T> clazz, String id) {

		return (T) this.getHibernateTemplate().get(clazz, id);

	}

	@Override
	public List<?> getEntityList(String hql, Object[] objs) {
		try {

			return this.getHibernateTemplate().find(hql, objs);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<Map<String, Object>> getEntityMap(String hql, Object[] objs) {
		try {

			Query query = createQuery(hql, objs);

			return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected Query createQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString不能为空");

		Query query = getHibernateSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	public <T> T findUnique(String hql, Object... values) {
		return (T) createQuery(hql, values).uniqueResult();

	}

	@SuppressWarnings("unchecked")
	public <T> Page<T> findPage(Page<T> page, String hql, Object... values) {
		Assert.notNull(page, "page不能为空");
		Query query = createQuery(hql, values);

		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);
			page.setTotalCount(totalCount);
		}

		setPageParameter(query, page);
		List<T> result = query.list();
		page.setResult(result);

		return page;
	}

	@Override
	public Page<Map<String, Object>> findPageMap(Page<Map<String, Object>> page, String hql, Object... values) {
		Query query = createQuery(hql, values);

		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);
			page.setTotalCount(totalCount);
		}

		setPageParameter(query, page);
		List<Map<String, Object>> result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		page.setResult(result);

		return page;
	}

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 */
	protected long countHqlResult(final String hql, final Object... values) {
		String fromHql = hql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			Long count = findUnique(countHql, values);
			return count;
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}
	}

	/**
	 * 设置分页参数
	 * 
	 * @param query
	 * @param page
	 *            pageSize必须>0, 否则不分页
	 */
	public void setPageParameter(final Query query, final Page<?> page) {
		if (page.getPageSize() > 0) {
			query.setFirstResult(page.getFirst());
			query.setMaxResults(page.getPageSize());
		}
	}

	protected Session getHibernateSession() {
		return this.getHibernateTemplate().getSessionFactory().getCurrentSession();
	}

	@Override
	public boolean bulkUpdate(String sql, Object... values) {
		int num = this.getHibernateTemplate().bulkUpdate(sql, values);
		return num > 0 ? true : false;
	}

}
