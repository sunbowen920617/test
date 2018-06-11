package com.zheman.lock.dao;

import com.zheman.lock.model.ResidentRoomBackup;

public interface ResidentRoomBackupMapper {
    int insert(ResidentRoomBackup record);

    int insertSelective(ResidentRoomBackup record);
}