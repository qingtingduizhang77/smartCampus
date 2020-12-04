package com.twi.base.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.twi.base.domain.Page;

public interface JdbcDao {
	
	public <T> List<T> getListBySql(String sql, RowMapper<T> objMapper, Object[] objs);

	public <T> T queryForObject(Class<T> c, String sql, Object[] objs);

	boolean executeSql(String sql, Object[] objs);

	List<Map<String, Object>> queryForMap(String sql, Object[] objs);
	
	List<Map<String, Object>> queryForMap(String sql);

	public <T> List<T> queryForList(Class<T> c, String sql, Object[] objs);
	
	
	<T> Page<T> queryForPageList(Page<T> page,RowMapper<T> objMapper, String sql, Object[] objs);

	Page<Map<String, Object>> queryForPageMap(Page<Map<String, Object>> page ,String sql, Object[] objs);
	
	int getCountBySql(String sql, Object[] objs);

	boolean batchExecuteSql(List<String> sqlList);
}
