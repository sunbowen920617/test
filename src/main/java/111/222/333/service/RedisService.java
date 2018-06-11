package com.zheman.lock.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zheman.lock.util.RedisClient;

@Service
public class RedisService {

	@Value("${redis.register.prefix}")
	String registerPrefix;

	@Value("${redis.login.prefix}")
	String loginPrefix;

	@Value("${redis.updatepassword.prefix}")
	String updatePasswordPrefix;

	@Value("${redis.addroom.prefix}")
	String addRoomPrefix;

	@Value("${redis.residentroombinding.prefix}")
	String residentRoomBinding;

	@Value("${redis.sms.ttl}")
	int smsValidTime;

	@Value("${login.token.prefix}")
	String loginTokenPrefix;

	@Value("${redis.shareqrcode.prefix}")
	String shareQrcodePrefix;

	@Value("${redis.iospushtoken.prefix}")
	String iosPushToken;

	@Value("${redis.logintoken.ttl}")
	int loginTokenValidTime;

	@Value("${redis.shareqrcode.ttl}")
	int shareQrcodeValidTime;

	@Autowired
	RedisClient redisClient;

	public static String MOBILEPHONE_KEY = "mobilePhone";
	public static String DEVICE_KEY = "device";
	public static String RESIDENTID_KEY = "residentId";
	public static String LOGIN_HISTORY_KEY = "loginHistoryid";

	public void setRegisterSmsCode(String mobilePhone, String smsCode) throws Exception {
		redisClient.set(registerPrefix + mobilePhone, smsCode, smsValidTime);
	}

	public void setLoginSmsCode(String mobilePhone, String smsCode) throws Exception {
		redisClient.set(loginPrefix + mobilePhone, smsCode, smsValidTime);
	}

	public void setUpdatePasswordSmsCode(String mobilePhone, String smsCode) throws Exception {
		redisClient.set(updatePasswordPrefix + mobilePhone, smsCode, smsValidTime);
	}

	public void setAddRoomSmsCode(String mobilePhone, String smsCode) throws Exception {
		redisClient.set(addRoomPrefix + mobilePhone, smsCode, smsValidTime);
	}

	//
	public void setResidentRoomBinding(long residentId, long roomId, long timestemp) throws Exception {
		redisClient.set(residentRoomBinding + String.valueOf(residentId) + String.valueOf(roomId),
				String.valueOf(timestemp));
	}

	/**
	 * 保存ios推送token
	 * 
	 * @param residentId
	 * @param token
	 * @throws Exception
	 */
	public void setPushToken(long residentId, String token) throws Exception {
		redisClient.set(iosPushToken + residentId, token);
	}

	/**
	 * 设置房屋共享二维码
	 * 
	 * @param residentId
	 * @param timestemp
	 * @throws Exception
	 */
	public void setShareQrcode(long shareId, long timestemp) throws Exception {
		redisClient.set(shareQrcodePrefix + shareId, String.valueOf(timestemp), shareQrcodeValidTime);
	}

	public void setLoginToken(String token, String mobile, String device, String residentId, String loginHistory)
			throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put(this.MOBILEPHONE_KEY, mobile);
		map.put(this.DEVICE_KEY, device);
		map.put(this.RESIDENTID_KEY, residentId);
		map.put(this.LOGIN_HISTORY_KEY, loginHistory);
		redisClient.mSet(loginTokenPrefix + token, map, loginTokenValidTime);
	}

	public String getRegisterSmsCode(String mobilePhone) throws Exception {
		Object obj = redisClient.get(registerPrefix + mobilePhone);
		if (null == obj)
			return null;
		else {
			return obj.toString();
		}
	}

	public String getLoginSmsCode(String mobilePhone) throws Exception {
		Object obj = redisClient.get(loginPrefix + mobilePhone);
		if (null == obj)
			return null;
		else {
			return obj.toString();
		}
	}

	public String getUpdatePasswordSmsCode(String mobilePhone) throws Exception {
		Object obj = redisClient.get(updatePasswordPrefix + mobilePhone);
		if (null == obj)
			return null;
		else {
			return obj.toString();
		}
	}

	public String getAddRoomSmsCode(String mobilePhone) throws Exception {
		Object obj = redisClient.get(addRoomPrefix + mobilePhone);
		if (null == obj)
			return null;
		else {
			return obj.toString();
		}
	}

	/**
	 * 获取房屋共享二维码
	 * 
	 * @param inviterId
	 * @return
	 * @throws Exception
	 */
	public String getShareQrcode(long shareId) throws Exception {
		Object obj = redisClient.get(shareQrcodePrefix + shareId);
		if (null == obj)
			return null;
		else {
			return obj.toString();
		}
	}

	/**
	 * 通过登录token获取设备类型
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public String getDeviceByLoginToken(String token) throws Exception {
		List<String> obj = redisClient.mGet(loginTokenPrefix + token, this.DEVICE_KEY);
		if (null == obj || obj.size() <= 0)
			return null;
		else {
			if (null == obj.get(0))
				return null;
			return obj.get(0);
		}
	}

	/**
	 * 通过登录token获取手机号码
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public String getMobilePhoneByLoginToken(String token) throws Exception {
		List<String> obj = redisClient.mGet(loginTokenPrefix + token, this.MOBILEPHONE_KEY);
		if (null == obj || obj.size() <= 0)
			return null;
		else {
			if (null == obj.get(0))
				return null;
			return obj.get(0);
		}
	}

	/**
	 * 通过登录token获取住户ID
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public Long getResidentIdByLoginToken(String token) throws Exception {
		List<String> obj = redisClient.mGet(loginTokenPrefix + token, this.RESIDENTID_KEY);
		if (null == obj || obj.size() <= 0)
			return null;
		else {
			if (null == obj.get(0))
				return null;
			return Long.valueOf(obj.get(0));
		}
	}

	/**
	 * 通过登录token获取登录记录ID
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public Long getLoginHistoryIdByLoginToken(String token) throws Exception {
		List<String> obj = redisClient.mGet(loginTokenPrefix + token, this.LOGIN_HISTORY_KEY);
		if (null == obj || obj.size() <= 0)
			return null;
		else {
			if (null == obj.get(0))
				return null;
			return Long.valueOf(obj.get(0));
		}
	}

	/**
	 * 根据residentid和roomid查询是否已经在绑定
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public String getResidentRoomBinding(long residentId, long roomId) throws Exception {
		Object obj = redisClient.get(residentRoomBinding + String.valueOf(residentId) + String.valueOf(roomId));
		if (null == obj) {
			return null;
		} else {
			return obj.toString();
		}
	}

	/**
	 * 获取ios的pushtoken
	 * 
	 * @param residentId 住户id
	 * @return
	 * @throws Exception
	 */
	public String getPushToken(long residentId) throws Exception {
		Object obj = redisClient.get(iosPushToken + residentId);
		if (null == obj) {
			return null;
		} else {
			return obj.toString();
		}
	}

	public boolean existsToken(String token) {
		return redisClient.exists(loginTokenPrefix + token);
	}

	/**
	 * 删除上一次登录 token
	 * 
	 * @param token
	 * @return
	 */
	public boolean removeNewlyLoginToken(String token) {
		redisClient.remove(loginTokenPrefix + token);
		return true;
	}

	/**
	 * 删除登陸驗證碼
	 * 
	 * @param mobilePhone
	 * @return
	 */
	public boolean removeLoginSmsCode(String mobilePhone) {
		redisClient.remove(loginPrefix + mobilePhone);
		return true;
	}

	/**
	 * 删除注册验证码
	 * 
	 * @param mobilePhone
	 * @return
	 */
	public boolean removeRegisterSmsCode(String mobilePhone) {
		redisClient.remove(registerPrefix + mobilePhone);
		return true;
	}

	/**
	 * 删除修改密码验证码
	 * 
	 * @return
	 */
	public boolean removeUpdateSmsCode(String mobilePhone) {
		redisClient.remove(updatePasswordPrefix + mobilePhone);
		return true;
	}

	/**
	 * 删除添加房屋验证码
	 * 
	 * @param mobilePhone
	 * @return
	 */
	public boolean removeAddRoomSmsCode(String mobilePhone) {
		redisClient.remove(addRoomPrefix + mobilePhone);
		return true;
	}

	/**
	 * 删除二维码共享信息
	 * 
	 * @param mobilePhone
	 * @return
	 */
	public boolean removeShareQrcodePrefix(long mobilePhone) {
		redisClient.remove(shareQrcodePrefix + mobilePhone);
		return true;
	}

	/**
	 * 刪除绑定过程
	 * 
	 * @param residentId
	 * @param roomId
	 * @return
	 */
	public boolean removeResidentRoomBinding(long residentId, long roomId) {
		redisClient.remove(residentRoomBinding + String.valueOf(residentId) + String.valueOf(roomId));
		return true;
	}
	
	/**
	 * 
	 * @param residentId
	 * @return
	 */
	public boolean removeIosPushToken(long residentId) {
		redisClient.remove(iosPushToken+residentId);
		return true;
	}
}
