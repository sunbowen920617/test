package com.zheman.lock.dao;

import com.zheman.lock.model.Provinces;

public interface ProvincesMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Provinces record);

	int insertSelective(Provinces record);

	Provinces selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Provinces record);

	int updateByPrimaryKey(Provinces record);

	Provinces selectByProvinceId(Integer provinceId);
}