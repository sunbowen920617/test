package com.zheman.lock.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zheman.lock.model.ResidentRoom;

public interface ResidentRoomMapper {
	int insert(ResidentRoom record);

	int insertSelective(ResidentRoom record);

	ResidentRoom selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(ResidentRoom record);

	int updateByPrimaryKey(ResidentRoom record);

	List<ResidentRoom> selectByResidentId(Long residentId);

	List<ResidentRoom> selectByRoomId(Long roomId);

	ResidentRoom selectByRoomIdAndResidentId(Map<String, String> params);

	List<ResidentRoom> selectByRoomIdAndResidentId2(@Param("residentId") long residentId, @Param("roomId") long roomId);

	int deleteByResidentIdAndRoomId(Map<String, String> params);

	List<ResidentRoom> selectByInvitedIdAndRoomId(Map<String, String> params);
}