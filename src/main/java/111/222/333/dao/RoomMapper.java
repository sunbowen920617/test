package com.zheman.lock.dao;

import java.util.List;

import com.zheman.lock.model.Room;

public interface RoomMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Room record);

	int insertSelective(Room record);

	Room selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Room record);

	int updateByPrimaryKey(Room record);

	List<Room> selectRoomsByUnitId(long unitId);

	List<Room> selectRoomsNotHaveResident(long unitId);

}