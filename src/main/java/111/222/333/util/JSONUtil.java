package com.zheman.lock.util;

import com.google.gson.Gson;
import com.zheman.lock.model.trans.TransIdcardLiveDetectFour;

public class JSONUtil {

	private static Gson gson = new Gson();

	public static String parseToString(Object obj) {
		return gson.toJson(obj);
	}

	public static TransIdcardLiveDetectFour toIdcardLiveDetectFour(String data) {
		return gson.fromJson(data, TransIdcardLiveDetectFour.class);
	}

}
