package com.zheman.lock.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zheman.lock.model.Share;

public interface ShareMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Share record);

	int insertSelective(Share record);

	Share selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Share record);

	int updateByPrimaryKey(Share record);

	Share selectByResidentIdAndUUID(Map<String, String> params);

	List<Share> selectInvitedByRoomId(Long roomId);

	List<Share> selectInvitedByRoomIdAndShareResidentId(Map<String, String> params);

	Share selectNilByThreeElement(Map<String, String> params);

	Share selectAccetpByThreeElement(Map<String, String> params);

	Share selectByFourElement(@Param("shareResidentId") long shareResidentId,
			@Param("acceptResidentId") long acceptResidentId, @Param("roomId") long roomId,
			@Param("accepted") int accepted);
}