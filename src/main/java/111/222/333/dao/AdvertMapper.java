package com.zheman.lock.dao;

import java.util.List;

import com.zheman.lock.model.Advert;

public interface AdvertMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Advert record);

    int insertSelective(Advert record);

    Advert selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Advert record);

    int updateByPrimaryKey(Advert record);
    
    Advert selectHomePageAdvert(String today);
    
    List<Advert> selectRoomListAdvert(String today);
}