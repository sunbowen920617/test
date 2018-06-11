package com.zheman.lock.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.DictionaryMapper;
import com.zheman.lock.model.Dictionary;
import com.zheman.lock.model.enumtion.DictionaryType;

@Service
public class DictionaryService {

	@Autowired
	DictionaryMapper dictionaryMapper;

	/**
	 * 根据type查询字典信息
	 * 
	 * @param type
	 * @return
	 */
	public CommonResult selectDictionaryByType(String type) {
		CommonResult commonResult = new CommonResult();
		List<Dictionary> dictionaries = dictionaryMapper.selectChildrenByType(type);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询完成");
		commonResult.setObj(dictionaries);
		return commonResult;
	}

	public Map<String, Integer> selectRequestRoomAuthorization() {
		Map<String, Integer> map = new HashMap<>();
		List<Dictionary> dictionaries = dictionaries = dictionaryMapper
				.selectChildrenByType(DictionaryType.REQUEST_ROOM_AUTHORIZATION.toString());
		for (Dictionary dictionary : dictionaries) {
			map.put(dictionary.getType(), dictionary.getValue());
		}
		return map;
	}

}
