package com.zheman.lock.dao;

import java.util.List;

import com.zheman.lock.model.Unit;

public interface UnitMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Unit record);

    int insertSelective(Unit record);

    Unit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Unit record);

    int updateByPrimaryKey(Unit record);
    
    List<Unit> selectByCommunityId(long communityId);
    
    
}