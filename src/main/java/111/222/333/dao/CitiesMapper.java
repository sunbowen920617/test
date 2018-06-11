package com.zheman.lock.dao;

import com.zheman.lock.model.Cities;

public interface CitiesMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Cities record);

	int insertSelective(Cities record);

	Cities selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Cities record);

	int updateByPrimaryKey(Cities record);

	Cities selectByCityId(Integer areaId);
}