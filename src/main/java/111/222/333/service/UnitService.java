package com.zheman.lock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.UnitMapper;
import com.zheman.lock.model.Unit;

@Service
public class UnitService {

	@Autowired
	UnitMapper unitMapper;

	public Unit selectById(Long id) {
		return unitMapper.selectByPrimaryKey(id);
	}

	public CommonResult selectByCommunityId(long communityId) {
		CommonResult commonResult=new CommonResult();
		List<Unit> units = unitMapper.selectByCommunityId(communityId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj(units);
		return commonResult;
	}

}
