package com.zheman.lock.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zheman.lock.model.Notice;

public interface NoticeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Notice record);

    int insertSelective(Notice record);

    Notice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Notice record);

    int updateByPrimaryKey(Notice record);
    
    List<Notice> selectByResidentId(@Param("residentId") long residentId,@Param("offset") int offset,@Param("limit") int limit);
}