package com.zheman.lock.dao;

import com.zheman.lock.model.LoginHistory;

public interface LoginHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoginHistory record);

    int insertSelective(LoginHistory record);

    LoginHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoginHistory record);

    int updateByPrimaryKey(LoginHistory record);
    
    LoginHistory newlyByResidentId(long residentId);
    
    
}