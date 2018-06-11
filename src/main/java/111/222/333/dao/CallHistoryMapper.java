package com.zheman.lock.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zheman.lock.model.CallHistory;

public interface CallHistoryMapper {
	int deleteByPrimaryKey(Long id);

	int insert(CallHistory record);

	int insertSelective(CallHistory record);

	CallHistory selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(CallHistory record);

	int updateByPrimaryKey(CallHistory record);

	CallHistory selectNewlyByRoomIdAndResidentId(Map<String, String> params);

	List<CallHistory> selectByResidentId(@Param("residentId") long residentId, @Param("offset") int offset,
			@Param("limit") int limit);

	List<CallHistory> selectByRoomIds(@Param("ids") List<Long> ids, @Param("offset") int offset, @Param("limit") int limit);

	CallHistory selectCurrentCall(@Param("residentId") Long residentId);
}