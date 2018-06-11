package com.zheman.lock.service;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.zheman.lock.dao.SmsHistoryMapper;
import com.zheman.lock.model.SmsHistory;
import com.zheman.lock.util.GenerateUtil;

@Service
public class SmsService {
	Logger logger = LoggerFactory.getLogger(SmsService.class);

	@Autowired
	SmsHistoryMapper smsHistoryMapper;

	@Value("${tencent.sms.appId}")
	private Integer appId;

	@Value("${tencent.sms.appkey}")
	private String appKey;

	@Value("${tencent.sms.registerMsgId}")
	private int registerMsgId;

	@Value("${tencent.sms.loginMsgId}")
	private int loginMsgId;

	@Value("${tencent.sms.updatepasswordmsgid}")
	private int updatePasswordMsgId;

	@Autowired
	RedisService redisService;

	/**
	 * 发送短信验证 1、根据smsType选择短信模板 2、发送短信 3、保存短信发送记录
	 * 
	 * @param mobilePhoneNumber
	 * @return true 发送成功 false 发送失败
	 * @throws Exception
	 */
	public boolean sendSms(String mobilePhone, int smsType) throws Exception {
		// 生成验证码
		String smsCode = GenerateUtil.genSmsCode();
		// 创建短信发送对象
		SmsSingleSender sender = new SmsSingleSender(appId, appKey);
		ArrayList<String> params = new ArrayList<String>();
		params.add(smsCode);
		params.add("1");
		SmsSingleSenderResult result = null;
		// 发送不同类型短信
		switch (smsType) {
		case 0:// 注册帐号
				// 保存到redis
			redisService.setRegisterSmsCode(mobilePhone, smsCode);
			result = sender.sendWithParam("86", mobilePhone, 114818, params, "", "", "1");
			break;
		case 1:// 验证码登录
				// 保存到redis
			redisService.setLoginSmsCode(mobilePhone, smsCode);
			result = sender.sendWithParam("86", mobilePhone, 114817, params, "", "", "1");
			break;
		case 2:// 找回密码
				// 保存到redis
			redisService.setUpdatePasswordSmsCode(mobilePhone, smsCode);
			result = sender.sendWithParam("86", mobilePhone, 114814, params, "", "", "1");
			break;
		case 3:// 添加房屋
				// 保存到redis
			redisService.setAddRoomSmsCode(mobilePhone, smsCode);
			result = sender.sendWithParam("86", mobilePhone, 114812, params, "", "", "1");
			break;
		default:
			break;
		}
		// 保存短信发送记录
		SmsHistory smsHistory = new SmsHistory();
		smsHistory.setDatetime(new Date());
		smsHistory.setMobilephonenumber(mobilePhone);
		smsHistory.setSmstype(smsType);
		smsHistory.setSmscode(smsCode);
		if (null == result) {
			return false;
		}
		if (StringUtils.equals(result.ext, "1")) {
			smsHistory.setResultcode(1);
			smsHistoryMapper.insert(smsHistory);
			logger.info("send sms success mobilePhone:" + mobilePhone + ", smsCode:" + smsCode);
			return true;
		}
		smsHistory.setErrormessage(result.errMsg);
		smsHistory.setResultcode(0);
		logger.info("send sms fail mobilePhone:" + mobilePhone + ", smsCode:" + smsCode + ", errormessage:"
				+ result.errMsg);
		smsHistoryMapper.insert(smsHistory);
		return false;
	}

	/**
	 * 验证短信码
	 * 
	 * @param mobilePhone
	 * @param smsType
	 * @param smsCode
	 * @return
	 */
	public boolean validSmsCode(String mobilePhone, int smsType, String smsCode) throws Exception {
		String obj = null;
		switch (smsType) {
		case 0:// 注册账户
			obj = redisService.getRegisterSmsCode(mobilePhone);
			break;
		case 1:// 验证码登录
			obj = redisService.getLoginSmsCode(mobilePhone);
			break;
		case 2:// 找回密码
			obj = redisService.getUpdatePasswordSmsCode(mobilePhone);
			break;
		case 3:// 添加房屋
			obj = redisService.getAddRoomSmsCode(mobilePhone);
			break;
		default:
			break;
		}
		if (null == obj) {
			return false;
		} else {
			obj = obj.replaceAll(" ", "").replaceAll(",", "");
			if (StringUtils.equals(smsCode, obj)) {
				return true;
			}
		}
		return false;
	}

}
