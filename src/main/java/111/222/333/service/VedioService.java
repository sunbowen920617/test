package com.zheman.lock.service;

import java.util.TreeMap;

import org.apache.commons.lang3.RandomUtils;

import com.qcloud.QcloudApiModuleCenter;
import com.qcloud.Module.Base;
import com.qcloud.Module.Vod;

public class VedioService {

	public static void main(String[] args) {
		Base base = new Vod();
		QcloudApiModuleCenter qcloudApiModuleCenter = new QcloudApiModuleCenter(base, null);
		TreeMap<String, Object> params = new TreeMap<>();
		params.put("fileId", "7447398155447367891");
		params.put("Action", "GetVideoInfo");
		params.put("Region", "sh");
		params.put("Timestamp", System.currentTimeMillis());
		params.put("Nonce",RandomUtils.nextInt(0, 1000000));
		String result = base.call("GetVideoInfo", params);
		System.out.println(result);
	}

}
