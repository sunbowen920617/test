package com.zheman.lock.dao;

import com.zheman.lock.model.RoomRequestAuthorization;

public interface RoomRequestAuthorizationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoomRequestAuthorization record);

    int insertSelective(RoomRequestAuthorization record);

    RoomRequestAuthorization selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoomRequestAuthorization record);

    int updateByPrimaryKey(RoomRequestAuthorization record);
}