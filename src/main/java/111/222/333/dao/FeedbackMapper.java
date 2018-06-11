package com.zheman.lock.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zheman.lock.model.Feedback;

public interface FeedbackMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Feedback record);

	int insertSelective(Feedback record);

	Feedback selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Feedback record);

	int updateByPrimaryKey(Feedback record);

	List<Feedback> selectFeedbacksByResidentId(long residentId);
	
	List<Feedback> selectDailyByResidentId(@Param("residentId") Long residentId,@Param("begin") String begin,@Param("end") String end);
}