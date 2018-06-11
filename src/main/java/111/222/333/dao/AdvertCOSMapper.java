package com.zheman.lock.dao;

import java.util.List;

import com.zheman.lock.model.AdvertCOS;

public interface AdvertCOSMapper {
	int deleteByPrimaryKey(Long id);

	int insert(AdvertCOS record);

	int insertSelective(AdvertCOS record);

	AdvertCOS selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(AdvertCOS record);

	int updateByPrimaryKey(AdvertCOS record);

	List<AdvertCOS> selectByAdvertId(long advertId);
}