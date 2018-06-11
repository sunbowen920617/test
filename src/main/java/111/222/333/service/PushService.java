package com.zheman.lock.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import com.zheman.lock.dao.LoginHistoryMapper;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.model.LoginHistory;
import com.zheman.lock.model.Resident;
import com.zheman.lock.model.enumtion.PushTypeEnum;
import com.zheman.lock.model.enumtion.SmsTypeEnum;
import com.zheman.lock.util.JSONUtil;

@Service
public class PushService {

	Logger logger = LoggerFactory.getLogger(PushService.class);

	@Value("${tencent.push.resident.iosenv}")
	String iosenv;

	@Autowired
	LoginHistoryMapper loginHistoryMapper;

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	SmsService smsService;

	@Autowired
	XingeApp xingeApp;

	@Resource(name = "iosXingeApp")
	XingeApp iosXingeApp;

	@Resource(name = "visitorXingeApp")
	XingeApp visitorXingeApp;

	/**
	 * 推送消息
	 * 
	 * @param mobilePhone
	 *            接收人的手机号码
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 */
	private boolean pushMessage(String mobilePhone, String title, String content, Map<String, Object> custom) {
		// 根据手机号码查询住户
		Resident resident = residentMapper.selectByMobilePhone(mobilePhone);
		// 查询住户最近一次的登录记录
		LoginHistory loginHistory = loginHistoryMapper.newlyByResidentId(resident.getId());
		if (null == loginHistory)
			return false;
		// 获取用户的设备信息
		String device = loginHistory.getDevice();
		if (null == device) {
			pushForAndroid(mobilePhone, title, content, Message.TYPE_NOTIFICATION, custom);
			pushForIOS(mobilePhone, title, custom);
		} else if (StringUtils.equals("ios", device.toLowerCase())) {
			pushForIOS(mobilePhone, title, custom);
		} else if (StringUtils.equals("android", device.toLowerCase())) {
			pushForAndroid(mobilePhone, title, content, Message.TYPE_NOTIFICATION, custom);
		}
		return true;
	}

	/**
	 * 推送消息的基础方法android
	 * 
	 * @param id
	 * @param title
	 * @param content
	 */
	private void pushForAndroid(String deviceId, String title, String content, int type, Map<String, Object> custom) {
		Message message = new Message();
		message.setTitle(title);
		message.setContent(content);
		message.setType(type);
		message.setCustom(custom);
		ClickAction clickAction = new ClickAction();
		clickAction.setActivity("com.zheman.resident.utils.xgpush.XGPushActivity");
		message.setAction(clickAction);
		JSONObject jsonobj = xingeApp.pushSingleAccount(XingeApp.DEVICE_ANDROID, deviceId, message);
		logger.info("pushforandroid info message" + JSONUtil.parseToString(message) + ",ret_code:" + jsonobj.toString()
				+ " , account:" + deviceId);
	}

	/**
	 * 推送消息的基础方法IOS
	 * 
	 * @param id
	 * @param title
	 * @param content
	 */

	private void pushForIOS(String deviceId, String alert, Map<String, Object> custom) {
		MessageIOS message = new MessageIOS();
		message.setExpireTime(86400);
		message.setAlert(alert);
		message.setBadge(1);
		message.setSound("beep.wav");
		message.setCustom(custom);
		if (StringUtils.equals(iosenv, "dev")) {
			JSONObject jsonObj = iosXingeApp.pushSingleAccount(XingeApp.DEVICE_IOS, deviceId, message,
					XingeApp.IOSENV_DEV);
			logger.info("pushforandroid info message:" + JSONUtil.parseToString(message) + "ret_code:"
					+ jsonObj.toString() + ",iosenv=dev" + "  ,  account:" + deviceId);
		} else {
			JSONObject jsonObj = iosXingeApp.pushSingleAccount(XingeApp.DEVICE_IOS, deviceId, message,
					XingeApp.IOSENV_PROD);
			logger.info("pushforandroid info message:" + JSONUtil.parseToString(message) + "ret_code:"
					+ jsonObj.toString() + ",iosenv=dev" + "  ,  account:" + deviceId);
		}

	}

	/**
	 * 开锁
	 * @param mobilePhone 设备注册号
	 * @param flag 标记 1、为主动开锁。2、为呼叫开锁
	 * @return
	 */
	public JSONObject unlock(String mobilePhone,Long callHistoryId, int flag) {
		Map<String, Object> custom = new HashMap<>();
		custom.put("type", String.valueOf(PushTypeEnum.UNLOCK.ordinal()));
		custom.put("flag", flag);
		custom.put("callHistoryId", callHistoryId);
		Message message = new Message();
		message.setType(Message.TYPE_MESSAGE);
		message.setCustom(custom);
		JSONObject jsonObject = visitorXingeApp.pushSingleAccount(XingeApp.DEVICE_ANDROID, mobilePhone, message);
		logger.info("unlock info jsonobject:" + jsonObject.toString() + ", mobilePhone:" + mobilePhone+",callHistoryId:"+callHistoryId+",flag:"+flag);
		return jsonObject;
	}

	/**
	 * 推送邀请房屋共享消息
	 * 
	 * @param inviterMobilePhone
	 *            邀请人的手机号码
	 * @param acceptMobilePhone
	 *            被邀请人的手机号码
	 * @param address
	 *            地址
	 * @throws Exception
	 */
	public void pushShareRoomMessage(String inviterMobilePhone, String acceptMobilePhone, String address, long noticeId)
			throws Exception {
		StringBuffer title = new StringBuffer();
		title.append("有住户邀请你共享房屋权限");
		StringBuffer message = new StringBuffer();
		message.append("手机号码为").append(inviterMobilePhone).append("的住户,邀请你共享").append(address).append("的房屋权限");
		Map<String, Object> map = new HashMap<>();
		map.put("noticeId", String.valueOf(noticeId));
		map.put("type", String.valueOf(PushTypeEnum.NOTICE.ordinal()));
		boolean bool = pushMessage(acceptMobilePhone, title.toString(), message.toString(), map);
		if (!bool) {
			smsService.sendSms(acceptMobilePhone, SmsTypeEnum.SHAREROOM.ordinal());
		}
	}

	/**
	 * 推送拒绝邀请房屋共享结果
	 * 
	 * @param noticeId
	 *            推送消息的ID
	 * @param mobilePhone
	 *            接收推送人的手机号码
	 * @param acceptMobilePhone
	 *            被邀请人
	 * @param address
	 *            地址
	 */
	public void pushRejectShareRoomMessage(long noticeId, String mobilePhone,String message) {
		StringBuffer title = new StringBuffer();
		title.append("房屋共享邀请未接受");
		Map<String, Object> map = new HashMap<>();
		map.put("type", String.valueOf(PushTypeEnum.NOTICE.ordinal()));
		map.put("noticeId", String.valueOf(noticeId));
		pushMessage(mobilePhone, title.toString(), message, map);
	}

	/**
	 * 推送接受邀请房屋共享结果
	 * 
	 * @param noticeId
	 *            推送消息的ID
	 * @param mobilePhone
	 *            接受推送人的手机号码
	 * @param acceptMobilePhone
	 *            被邀请人
	 * @param address
	 *            地址
	 */
	public void pushAcceptShareRoomMessage(long noticeId, String mobilePhone, String message) {
		StringBuffer title = new StringBuffer();
		title.append("住户已接受房屋共享邀请");
		Map<String, Object> map = new HashMap<>();
		map.put("type", String.valueOf(PushTypeEnum.NOTICE.ordinal()));
		map.put("noticeId", String.valueOf(noticeId));
		pushMessage(mobilePhone, title.toString(), message, map);
	}
	
	/**
	 * 推送房屋权限申请
	 * 
	 * @param noticeId
	 *            推送消息的ID
	 * @param mobilePhone
	 *            接受推送人的手机号码
	 * @param acceptMobilePhone
	 *            被邀请人
	 * @param address
	 *            地址
	 */
	public void pushRoomRequestAuthorization(long noticeId, String mobilePhone, String message) {
		StringBuffer title = new StringBuffer();
		title.append("住户已接受房屋共享邀请");
		Map<String, Object> map = new HashMap<>();
		map.put("type", String.valueOf(PushTypeEnum.NOTICE.ordinal()));
		map.put("noticeId", String.valueOf(noticeId));
		pushMessage(mobilePhone, title.toString(), message, map);
	}

	/**
	 * 未响应的门铃
	 * 
	 * @param mobilePhone
	 * @param message
	 */
	public void pushNoHandleForAndroid(String mobilePhone, String content) {
		// pushForAndroid(mobilePhone, PushUtil.NOHANDLE_TITLE, content);
	}

}
