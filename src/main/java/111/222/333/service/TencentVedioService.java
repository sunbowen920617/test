package com.zheman.lock.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.util.HttpClientUtil;
import com.zheman.lock.util.JSONUtil;
import com.zheman.lock.util.TlsSigatureUtil;

@Service
public class TencentVedioService {

	@Value("${tencent.vedio.appId}")
	long appId;

	@Value("${tencent.vedio.admin}")
	String adminIdentifier;

	private static final Logger log = LoggerFactory.getLogger(TencentVedioService.class);

	public String genTencentVedioSign(String mobilePhone) throws IOException {
		return TlsSigatureUtil.genTencentSign(appId, mobilePhone);
	}

	/**
	 * 帐号导入
	 * 
	 * @param identifier
	 *            用户名
	 * @param nick
	 *            昵称
	 * @param faceUrl
	 *            头像地址
	 * @param type
	 */
	public void accountImport(String mobilePhone, String nick, String faceUrl) throws Exception {
		Map<String, String> params = new HashMap<>();
		String sign = genTencentVedioSign(adminIdentifier);
		params.put("Identifier", mobilePhone);
		params.put("Nick", nick);
		params.put("FaceUrl", "");
		StringBuffer url = new StringBuffer();
		url.append("https://console.tim.qq.com/v4/im_open_login_svc/account_import?").append("Identifier=")
				.append(adminIdentifier).append("&usersig=").append(sign).append("&sdkappid=").append(appId)
				.append("&random=").append(System.currentTimeMillis()).append("&contenttype=").append("json");
		String result = HttpClientUtil.doPostJson(url.toString(), JSONUtil.parseToString(params));
		log.info(url.toString());
		log.info(JSONUtil.parseToString(params));
		log.info(result);
	}

	
	/**
	 * 获取用户的在线状态
	 * 
	 * @param mobilePhone
	 * @throws Exception
	 */
	public CommonResult queryState(String mobilePhone) throws Exception {
		CommonResult commonResult = new CommonResult();
		Map<String, Object> params = new HashMap<>();
		String sign = genTencentVedioSign(adminIdentifier);
		String[] accounts = new String[1];
		params.put("To_Account", accounts);
		accounts[0] = mobilePhone;
		StringBuffer url = new StringBuffer();
		url.append("https://console.tim.qq.com/v4/openim/querystate?").append("usersig=").append(sign)
				.append("&identifier=").append(adminIdentifier).append("&sdkappid=").append(appId).append("&random=")
				.append(System.currentTimeMillis()).append("&contenttype=json");
		String paramsStr = JSONUtil.parseToString(params);
		String result = HttpClientUtil.doPostJson(url.toString(), paramsStr);
		JSONObject jsonObject = new JSONObject(result);
		String actionStatus = jsonObject.getString("ActionStatus");
		String errorInfo = jsonObject.getString("ErrorInfo");
		int errorCode = jsonObject.getInt("ErrorCode");
		if (errorCode == 0 && StringUtils.equals("OK", actionStatus)) {
			JSONArray jsonArray = jsonObject.getJSONArray("QueryResult");
			JSONObject toAccount = jsonArray.getJSONObject(0);
			String state = toAccount.getString("State");
			commonResult.setCode(CommonResult.SUCCESS);
			commonResult.setMessage(state);
			commonResult.setObj("OK");
			return commonResult;
		}
		commonResult.setCode(CommonResult.FAILURE);
		commonResult.setMessage("fail");
		commonResult.setObj("fail");
		log.info("result:", result);
		log.info(url.toString());
		log.info(paramsStr);
		return commonResult;
	}
}
