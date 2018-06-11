package com.zheman.lock.dao;

import com.zheman.lock.model.Areas;

public interface AreasMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Areas record);

	int insertSelective(Areas record);

	Areas selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Areas record);

	int updateByPrimaryKey(Areas record);

	Areas selectByAreaId(Integer areaId);
}