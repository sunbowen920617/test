package com.zheman.lock.dao;

import java.util.List;
import java.util.Map;

import com.zheman.lock.model.Community;

public interface CommunityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Community record);

    int insertSelective(Community record);

    Community selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Community record);

    int updateByPrimaryKey(Community record);
    
    Community selectByResidentIdAndRoomId(Map<String, String> map);
    
    List<Community> selectCommunitys();
}