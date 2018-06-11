package com.zheman.lock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.CommunityMapper;
import com.zheman.lock.model.Community;

@Service
public class CommunityService {

	@Autowired
	CommunityMapper communityMapper;

	public Community selectById(Long id) {
		return communityMapper.selectByPrimaryKey(id);
	}
	
	public CommonResult selectCommunitys() {
		CommonResult commonResult=new CommonResult();
		List<Community> communities=communityMapper.selectCommunitys();
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj(communities);
		return commonResult;
	}

}
