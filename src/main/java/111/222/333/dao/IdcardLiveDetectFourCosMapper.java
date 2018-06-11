package com.zheman.lock.dao;

import com.zheman.lock.model.IdcardLiveDetectFourCos;

public interface IdcardLiveDetectFourCosMapper {
    int deleteByPrimaryKey(Long id);

    int insert(IdcardLiveDetectFourCos record);

    int insertSelective(IdcardLiveDetectFourCos record);

    IdcardLiveDetectFourCos selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(IdcardLiveDetectFourCos record);

    int updateByPrimaryKey(IdcardLiveDetectFourCos record);
}