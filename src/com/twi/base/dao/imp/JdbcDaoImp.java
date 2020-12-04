package com.twi.base.dao.imp;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.twi.base.dao.JdbcDao;
import com.twi.base.domain.Page;
@Service("jdbcDao")
public class JdbcDaoImp  extends NamedParameterJdbcDaoSupport implements JdbcDao {
	
	@Resource
	public void setJb(JdbcTemplate jb) {
	 super.setJdbcTemplate(jb);
	    }
	
	@Override
	public <T> List<T> getListBySql(String sql, RowMapper<T> objMapper, Object[] objs) {
		try {

			return this.getJdbcTemplate().query(sql, objMapper, objs);

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public <T> T queryForObject(Class<T> c, String sql, Object[] objs) {

		try {
			return this.getJdbcTemplate().queryForObject(sql, c, objs);

		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public boolean executeSql(String sql, Object[] objs) {

		this.getJdbcTemplate().update(sql, objs);
		return true;
	}

	@Override
	public List<Map<String, Object>> queryForMap(String sql, Object[] objs) {

		try {
			return this.getJdbcTemplate().queryForList(sql, objs);
		} catch (EmptyResultDataAccessException e) {

			return null;
		}
	}
	
	@Override
	public List<Map<String, Object>> queryForMap(String sql) {
		try {
			return this.getJdbcTemplate().queryForList(sql);
		} catch (EmptyResultDataAccessException e) {

			return null;
		}
	}

	@Override
	public <T> List<T> queryForList(Class<T> c, String sql, Object[] objs) {
		try {
			return this.getJdbcTemplate().queryForList(sql, c, objs);
		} catch (EmptyResultDataAccessException e) {

			return null;
		}
	}

	@Override
	public Page<Map<String, Object>> queryForPageMap(Page<Map<String, Object>> page,String sql, Object[] objs) {
		String pageSQL=this.getPageSQL(sql, page);
		
		page.setResult(this.getJdbcTemplate().queryForList(pageSQL, objs));
		if (page.isAutoCount()) {
			long totalCount = countHqlResult(sql, objs);
			page.setTotalCount(totalCount);
		}
		
		return page;
	}

	@Override
	public <T> Page<T> queryForPageList(Page<T> page,RowMapper<T> objMapper, String sql, Object[] objs) {
        
		String pageSQL=this.getPageSQL(sql, page);
		page.setResult(this.getJdbcTemplate().query(pageSQL,objMapper, objs));
		if (page.isAutoCount()) {
			long totalCount = countHqlResult(sql, objs);
			page.setTotalCount(totalCount);
		}
		
		return page;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private String getPageSQL(String querySQL ,Page<?> page) {
		
		
		int firstNum = page.getPageSize() * (page.getPageNo() - 1) + 1;

		int lastNum =  page.getPageSize() * page.getPageNo();
		
		    StringBuffer SQL =new StringBuffer();
				SQL.append("select ttt.* from ("+querySQL+" )ttt ");
				firstNum=firstNum-1;
				if(lastNum>firstNum)
				{
					SQL.append(" limit "+firstNum+","+(lastNum-firstNum));
				}
				else
				{
					SQL.append(" limit "+firstNum+",0");
				}
				return SQL.toString();
	}

	
	private String getCountSQL(String querySQL) {
		
		return new StringBuilder("select count(*) from ( ").append(querySQL)
				.append(") target").toString();

	}
	
	
	private int  countHqlResult( String sql, Object[] objs)
	{
		String countSQL=getCountSQL(sql);
		
		return this.getJdbcTemplate().queryForObject(countSQL, objs, Integer.class);
	}
	
	public int getCountBySql(String sql, Object[] objs){
		return this.getJdbcTemplate().queryForObject(sql, objs, Integer.class);
	}
	
	@Override
	public boolean batchExecuteSql(List<String> sqlList) {
		
		String[] sqlArray = new String[sqlList.size()];
		for (int i = 0; i < sqlList.size(); i++) {
			sqlArray[i] = sqlList.get(i);
		}
		this.getJdbcTemplate().batchUpdate(sqlArray);
		return true;
	}


}
