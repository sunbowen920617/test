package com.zheman.lock.dao;

import com.zheman.lock.model.UnlockHistory;

public interface UnlockHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UnlockHistory record);

    int insertSelective(UnlockHistory record);

    UnlockHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UnlockHistory record);

    int updateByPrimaryKey(UnlockHistory record);
}