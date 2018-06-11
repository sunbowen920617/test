package com.zheman.lock.dao;

import com.zheman.lock.model.ProfileFaceCOS;

public interface ProfileFaceCOSMapper {
	int deleteByPrimaryKey(Long id);

	int insert(ProfileFaceCOS record);

	int insertSelective(ProfileFaceCOS record);

	ProfileFaceCOS selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ProfileFaceCOS record);

	int updateByPrimaryKey(ProfileFaceCOS record);

	ProfileFaceCOS selectByResidentId(long residentId);
}