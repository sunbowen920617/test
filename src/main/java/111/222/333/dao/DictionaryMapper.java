package com.zheman.lock.dao;

import java.util.List;

import com.zheman.lock.model.Dictionary;

public interface DictionaryMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Dictionary record);

	int insertSelective(Dictionary record);

	Dictionary selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Dictionary record);

	int updateByPrimaryKey(Dictionary record);

	List<Dictionary> selectChildrenByType(String type);
}