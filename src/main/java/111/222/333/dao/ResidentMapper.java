package com.zheman.lock.dao;

import com.zheman.lock.model.Resident;

public interface ResidentMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Resident record);

	int insertSelective(Resident record);

	Resident selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Resident record);

	int updateByPrimaryKey(Resident record);

	Resident selectByMobilePhone(String mobilePhone);

}