package com.zheman.lock.dao;

import com.zheman.lock.model.AdvertHistory;

public interface AdvertHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AdvertHistory record);

    int insertSelective(AdvertHistory record);

    AdvertHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AdvertHistory record);

    int updateByPrimaryKey(AdvertHistory record);
}