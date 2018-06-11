package com.zheman.lock.dao;

import com.zheman.lock.model.SmsHistory;

public interface SmsHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsHistory record);

    int insertSelective(SmsHistory record);

    SmsHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsHistory record);

    int updateByPrimaryKey(SmsHistory record);
}