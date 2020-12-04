package com.twi.base;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.twi.base.dao.HibernateBaseDao;
import com.twi.base.dao.JdbcDao;

public abstract class BaseService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("rawtypes")
	@Resource(name = "jdbcDao")
	protected JdbcDao jdbcDao;
	
	@SuppressWarnings("rawtypes")
	@Resource(name = "hibernateBaseDao")
	protected HibernateBaseDao hibernateBaseDao;
	

}
