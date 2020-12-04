package com.twi.major.services;

import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.major.domain.Major;


public interface MajorServices {
	
	boolean addMajor(Major major);
	
	boolean updMajor(Major major);
	
	boolean delMajorById(String id);
	
	boolean batchDelMajor(String[] ids);
	
	Major getMajorByName(String name, String orgId);
	
	Major getMajorById(String id);
	
	Page<Major> getMajorPage(Page<Major> page, Map<String, Object> pMap);
	
}
