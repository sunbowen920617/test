package com.zheman.lock.common;

public class CommonResult {

	public final static int SUCCESS = 1;// 成功
	public final static int FAILURE = 0;// 失败
	public final static int NO_REGISTER = 2;// 未注册
	public final static int PASSWORD_ERROR = 3;// 无效的密码
	public final static int SMSCODE_ERROR = 4; // 无效的短信验证码

	public final static int MOBILEPHONE_NOT_REGISTER = 6;// 手机号码未注册

	// 房屋常量
	public final static int ROOM_HAS_LEADER = 2001;// 房屋有屋主
	public final static int UNIT_NOT_FOUND = 2002;// 未找到单元
	public final static int ROOM_NOT_FOUND = 2003;// 未找到指定房屋
	public final static int COMMUNITY_NOT_FOUND = 2004;// 未找到指定社区
	public final static int ROOM_AND_UNIT_NOT_MATCH = 2004;// 房屋与单元信息不匹配
	public final static int COMMUNITY_AND_UNIT_NOT_MATCH = 2004;// 社区与单元信息不匹配
	public final static int RESIDENT_AND_ROOM_BINDED = 2007; // 住户与房屋已经绑定
	public final static int QRCODE_INVALID = 2008; // 二维码无效
	public final static int RESIDENT_NOT_BINDED_ROOM = 2009; // 住户未绑定任何房屋

	// 个人资料
	public final static int PROFILE_FACE_TOOLONG = 3001; // 头像太大
	public final static int PROFILE_FACE_UPDATE_FAILURE = 3002;// 更新人脸信息 失败
	public final static int FACE_TOOLONG = 3003;// 人脸识别图片太大
	public final static int SAME_FACE = 3004;// 相似的图片

	// 访客记录
	public final static int NOT_FOUND_CALLHISTORY = 4001;// 未找到任何访客记录

	public final static int INVALID_TOKEN = -9999; // 无效的token
	public final static int INVALID_VERSION = -9998; // 无效的版本

	public final static int REGISTER_AND_LOGIN = 1001;

	public final static int REGISTER_NOT_FINISH = 4001;//注册未完成

	int code;
	String message;
	Object obj;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}
