package com.zheman.lock.dao;

import java.util.Map;

import com.zheman.lock.model.SystemInfo;

public interface SystemInfoMapper {
	int insert(SystemInfo record);

	int insertSelective(SystemInfo record);

	SystemInfo selectByTypeAndKey(Map<String, String> params);
}