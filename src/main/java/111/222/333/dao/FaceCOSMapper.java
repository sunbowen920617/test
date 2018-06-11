package com.zheman.lock.dao;

import java.util.List;

import com.zheman.lock.model.FaceCOS;

public interface FaceCOSMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FaceCOS record);

    int insertSelective(FaceCOS record);

    FaceCOS selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FaceCOS record);

    int updateByPrimaryKey(FaceCOS record);
    
    List<FaceCOS> selectByResidentId(long residentId);
}