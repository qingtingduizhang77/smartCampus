package com.twi.images.services;

import java.util.List;
import java.util.Map;

import com.twi.base.domain.Page;
import com.twi.images.domain.Images;

public interface ImagesServices {
	
	boolean add(Images entity);

	boolean update(Images entity);

	boolean deleteById(String id);

	boolean batchDelete(String[] ids);

	Images getEntityByName(String name, String orgId);

	Images getEntityById(String id);

	Page<Images> getPageList(Page<Images> page, Map<String, Object> pMap);
	
	List<Map<String, Object>> getImageList(String orgId);

}
