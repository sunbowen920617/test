package com.zheman.lock.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.dao.SystemInfoMapper;
import com.zheman.lock.model.SystemInfo;

@Service
public class SystemInfoService {

	private static final Logger log = LoggerFactory.getLogger(SystemInfoService.class);

	public static final String VERSION = "version";

	@Autowired
	SystemInfoMapper systemInfoMapper;

	public void addVersion(String key, String value) {
		SystemInfo systemInfo = new SystemInfo();
		systemInfo.setDatetime(new Date());
		systemInfo.setKey(key);
		systemInfo.setType(VERSION);
		systemInfo.setValue(value);
		systemInfoMapper.insert(systemInfo);
	}

	public String getVersion(String key) {
		Map<String, String> params = new HashMap<>();
		params.put("type", VERSION);
		params.put("key", key);
		SystemInfo systemInfo = systemInfoMapper.selectByTypeAndKey(params);
		if(null==systemInfo) {
			return null;
		}
		return systemInfo.getValue();
	}

}
