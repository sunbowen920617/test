package com.zheman.lock.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.CommunityMapper;
import com.zheman.lock.dao.FaceCOSMapper;
import com.zheman.lock.dao.IdcardLiveDetectFourCosMapper;
import com.zheman.lock.dao.LoginHistoryMapper;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.dao.ResidentRoomMapper;
import com.zheman.lock.dao.RoomMapper;
import com.zheman.lock.dao.UnitMapper;
import com.zheman.lock.model.Community;
import com.zheman.lock.model.FaceCOS;
import com.zheman.lock.model.IdcardLiveDetectFourCos;
import com.zheman.lock.model.LoginHistory;
import com.zheman.lock.model.Resident;
import com.zheman.lock.model.ResidentRoom;
import com.zheman.lock.model.Room;
import com.zheman.lock.model.Unit;
import com.zheman.lock.model.enumtion.AppRegisterState;
import com.zheman.lock.model.enumtion.LoginType;
import com.zheman.lock.util.GenerateUtil;
import com.zheman.lock.util.VerifyUtil;
import com.zheman.lock.youtu.Youtu;

@Service
public class ResidentService {
	Logger logger = LoggerFactory.getLogger(ResidentService.class);

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	LoginHistoryMapper loginHistoryMapper;

	@Autowired
	ResidentRoomMapper residentRoomMapper;

	@Autowired
	RoomMapper roomMapper;

	@Autowired
	CommunityMapper communityMapper;

	@Autowired
	UnitMapper unitMapper;

	@Autowired
	RedisService redisService;

	@Autowired
	TencentVedioService tencentVedioService;

	@Autowired
	YoutuFaceService youtuFaceService;

	@Autowired
	RoomService roomService;

	@Autowired
	TencentCOSService tencentCOSService;

	@Autowired
	FaceCOSMapper faceCOSMapper;

	@Autowired
	IdcardLiveDetectFourCosMapper idcardLiveDetectFourCosMapper;

	@Autowired
	TencentImageService tencentImageService;

	@Autowired
	Youtu youtu;

	@Autowired
	RoomRequestAuthorizationService roomRequestAuthorizationService;
	
	@Value("${resource.path}")
	String staticPath;

	/**
	 * 注册 密码使用MD5获取摘要
	 * 
	 * 1、根据注册用户的手机号码，从物业获取到住户信息并绑定。
	 * 
	 * 2、判断此房屋是否已经户主 一、没有 ： 将第一个注册的人设置为户主。 二、有：申请户主授权。
	 * 
	 * @param mobilePhone
	 * @param confirmPassword
	 * @param realName
	 * @return
	 * @throws Exception
	 */
	public boolean register(String mobilePhone, String confirmPassword, String realName) throws Exception {
		Resident resident = new Resident();
		resident.setMobilephonenumber(mobilePhone);
		// 获取密码摘要
		confirmPassword = DigestUtils.md5Hex(confirmPassword);
		resident.setPassword(confirmPassword);
		resident.setName(realName);
		resident.setNickname(GenerateUtil.genNickName());
		resident.setDatetime(new Date());
		resident.setAppregister(AppRegisterState.YES.ordinal());
		residentMapper.insert(resident);
		// 通过物业获取用户数据---------------------

		// 腾讯优图创建个体信息

		// 腾讯云独立模式帐号导入
		tencentVedioService.accountImport(mobilePhone, resident.getNickname(), null);
		// 删除掉redis中保存的注册短信验证码
		redisService.removeRegisterSmsCode(mobilePhone);
		return true;
	}

	/**
	 * 检查手机号码是否已经注册
	 * 
	 * @param mobilePhone
	 * @return true 被注册，false 未被注册
	 */
	public boolean checkMobilePhone(String mobilePhone) throws Exception {
		Resident resident = residentMapper.selectByMobilePhone(mobilePhone);
		if (null == resident) {
			return false;
		}
		return true;
	}

	/**
	 * 通过密码登录 一、验证密码 二、生成token存放到redis 三、保存登录历史记录
	 * 
	 * @param mobilePhone
	 * @param password
	 * @param device
	 * @return
	 */
	public CommonResult loginByPassword(String mobilePhone, String password, String device, String ipAddress,
			String deviceId, String timestemp) throws Exception {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		// 验证登录密码
		Resident resident = residentMapper.selectByMobilePhone(mobilePhone);
		if (null == resident) {
			commonResult.setCode(CommonResult.NO_REGISTER);
			commonResult.setMessage("帐号未注册");
			return commonResult;
		}
		password = DigestUtils.md5Hex(password);
		logger.info("login By password : " + password);
		if (!StringUtils.equals(password, resident.getPassword())) {
			commonResult.setCode(CommonResult.PASSWORD_ERROR);
			commonResult.setMessage("密码错误");
			return commonResult;
		}
		// 登录之后的操作
		Map<String, String> map = afterLogin(mobilePhone, device, ipAddress, deviceId, timestemp,
				LoginType.PASSWORD.ordinal(), resident.getId(), "");
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj(map);
		return commonResult;
	}

	/**
	 * 通过短信验证码登录 从redis中获取短信验证码并验证
	 * 
	 * @param mobilePhone
	 * @param password
	 * @param device
	 * @param ipAddress
	 * @param deviceId
	 * @param timestemp
	 * @return
	 */
	public CommonResult loginBySmsCode(String mobilePhone, String smsCode, String device, String ipAddress,
			String deviceId, String timestemp) throws Exception {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		Object obj = redisService.getLoginSmsCode(mobilePhone);
		if (null == obj) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("请先获取验证码");
			return commonResult;
		}
		Resident resident = residentMapper.selectByMobilePhone(mobilePhone);
		if (null != resident) {
			if (StringUtils.equals(smsCode, obj.toString())) {
				commonResult.setCode(CommonResult.SUCCESS);
				Map<String, String> map = afterLogin(mobilePhone, device, ipAddress, deviceId, timestemp,
						LoginType.SMSCODE.ordinal(), resident.getId(), smsCode);
				commonResult.setObj(map);
				// 登录完成删除掉redis中保存的验证码数据
				redisService.removeLoginSmsCode(mobilePhone);
				return commonResult;
			} else {
				commonResult.setCode(CommonResult.SMSCODE_ERROR);
				commonResult.setMessage("短信验证码无效");
			}
		} else {
			commonResult.setCode(CommonResult.NO_REGISTER);
			commonResult.setMessage("帐号未注册");
			return commonResult;
		}
		return commonResult;
	}

	/**
	 * 登录完成之后 保存到redis和数据库
	 * 
	 * @param mobilePhone
	 * @param password
	 * @param device
	 * @param ipAddress
	 * @param deviceId
	 * @param timestemp
	 * @param loginType
	 * @param residentId
	 * @param smsCode
	 *            短信验证码登录需要传递这个参数
	 * @throws Exception
	 */
	public Map<String, String> loginByAfterRegister(String ipAddress, Long residentId, String device, String deviceId,
			String timestemp) throws Exception {
		Map<String, String> map = new HashMap<>();
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		String mobilePhone = resident.getMobilephonenumber();
		// 生成token保存到redis 通过md5生成token
		String token = GenerateUtil.genToken(mobilePhone, deviceId, timestemp);
		// 生成sign签名
		String sign = tencentVedioService.genTencentVedioSign(mobilePhone);
		map.put("token", token);
		map.put("sign", sign);
		map.put("registerStep", resident.getRegisterstep().toString());
		// 删除掉上一次的登录历史记录
		LoginHistory beforeLoginHistory = loginHistoryMapper.newlyByResidentId(residentId);
		if (null != beforeLoginHistory) {
			String newlyToken = beforeLoginHistory.getToken();
			redisService.removeNewlyLoginToken(newlyToken);
		}
		// 保存登录记录
		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setDatetime(new Date());
		loginHistory.setDevice(device);
		loginHistory.setIpaddress(ipAddress);
		loginHistory.setResidentid(residentId);
		loginHistory.setTimestemp(timestemp);
		loginHistory.setLogintype(1);
		loginHistory.setDeviceid(deviceId);
		loginHistory.setTencentsign(sign);
		loginHistory.setToken(token);
		loginHistoryMapper.insert(loginHistory);
		// 保存到redis 过期时间为30天
		redisService.setLoginToken(token, mobilePhone, device, String.valueOf(residentId),
				loginHistory.getId().toString());
		return map;
	}

	/**
	 * 登录完成之后 保存到redis和数据库
	 * 
	 * @param mobilePhone
	 * @param password
	 * @param device
	 * @param ipAddress
	 * @param deviceId
	 * @param timestemp
	 * @param loginType
	 * @param residentId
	 * @param smsCode
	 *            短信验证码登录需要传递这个参数
	 * @throws Exception
	 */
	public Map<String, String> afterLogin(String mobilePhone, String device, String ipAddress, String deviceId,
			String timestemp, int loginType, long residentId, String smsCode) throws Exception {
		Map<String, String> map = new HashMap<>();

		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		// 生成token保存到redis 通过md5生成token
		String token = GenerateUtil.genToken(mobilePhone, deviceId, timestemp);
		// 生成sign签名
		String sign = tencentVedioService.genTencentVedioSign(mobilePhone);
		map.put("token", token);
		map.put("sign", sign);
		map.put("registerStep", resident.getRegisterstep().toString());
		// 删除掉上一次的登录历史记录
		LoginHistory beforeLoginHistory = loginHistoryMapper.newlyByResidentId(residentId);
		if (null != beforeLoginHistory) {
			String newlyToken = beforeLoginHistory.getToken();
			redisService.removeNewlyLoginToken(newlyToken);
		}
		// 保存登录记录
		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setDatetime(new Date());
		loginHistory.setDevice(device);
		loginHistory.setIpaddress(ipAddress);
		loginHistory.setResidentid(residentId);
		loginHistory.setTimestemp(timestemp);
		loginHistory.setLogintype(loginType);
		loginHistory.setDeviceid(deviceId);
		loginHistory.setTencentsign(sign);
		if (loginType == LoginType.SMSCODE.ordinal()) {
			loginHistory.setSmscode(smsCode);
		}
		loginHistory.setToken(token);
		loginHistoryMapper.insert(loginHistory);
		// 保存到redis 过期时间为30天
		redisService.setLoginToken(token, mobilePhone, device, String.valueOf(residentId),
				loginHistory.getId().toString());
		return map;
	}

	/**
	 * 修改密码 1、查询到数据 2、更新数据
	 * 
	 * @param mobilePhone
	 * @param smsCode
	 * @return
	 */
	public boolean updatePassword(String mobilePhone, String password) throws Exception {
		Resident resident = residentMapper.selectByMobilePhone(mobilePhone);
		resident.setPassword(DigestUtils.md5Hex(password));
		residentMapper.updateByPrimaryKeySelective(resident);
		// 删除掉redis中保存的短信验证码
		redisService.removeUpdateSmsCode(mobilePhone);
		return true;
	}

	/**
	 * 绑定住户房屋 1、添加房屋 2、查询到住户信息 3、绑定 房屋和住户的信息 4、通过物业借口确认用户身份 5、优图个体创建
	 * 
	 * @param token
	 *            登录token
	 * @param communityId
	 *            小区ID
	 * @param unitId
	 *            单元ID
	 * @param roomId
	 *            房间ID
	 * @param mobilePhone
	 * @return
	 * @throws Exception
	 */
	public boolean bindRoom(String token, int communityId, long unitId, long roomId, String mobilePhone)
			throws Exception {
		// redis获取登录住户的ID
		Long residentId = redisService.getResidentIdByLoginToken(token);
		if (null == residentId) {
			logger.error("bindRoom has an error ：can not found redis by logintokenkey：" + token);
			return false;
		}
		// 通过ID查询到住户的信息
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		if (null == resident) {
			logger.error("bindRoom has an error : can not found resident by residentId:" + residentId);
			return false;
		}
		// 通过ID查询room信息
		Room room = roomMapper.selectByPrimaryKey(roomId);
		if (null == room) {
			logger.error("bindRoom has an error: can not found room by roomid:" + roomId);
		}
		// 保存到数据库
		ResidentRoom residentRoom = new ResidentRoom();
		residentRoom.setResidentid(resident.getId());
		residentRoom.setRoomid(roomId);
		return true;
	}

	/**
	 * 退出登录
	 * 
	 * @param token
	 *            登录token
	 * @param timestemp
	 *            时间戳
	 * @return
	 * @throws Exception
	 */
	public CommonResult logout(String token) throws Exception {
		CommonResult commonResult = new CommonResult();
		Long residentId = redisService.getResidentIdByLoginToken(token);
		if (null != residentId) {
			redisService.removeNewlyLoginToken(token);
			redisService.removeIosPushToken(residentId);
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("已退出");
		commonResult.setObj("");
		return commonResult;
	}

	/**
	 * 保存iospushtoken
	 * 
	 * @param token
	 *            用户登录token
	 * @param pushToken
	 *            iospushtoken
	 * @return
	 * @throws Exception
	 */
	public CommonResult iosPushToken(String token, String pushToken) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		commonResult.setCode(CommonResult.SUCCESS);
		redisService.setPushToken(residentId, pushToken);
		commonResult.setMessage("");
		commonResult.setObj("ok");
		return commonResult;
	}

	/**
	 * 获取住户所在的所有单元
	 * 
	 * @param residentId
	 * @return
	 */
	public Set<Long> getUnitsByResident(long residentId) {
		Set<Long> units = new HashSet<>();
		List<ResidentRoom> residentRooms = residentRoomMapper.selectByResidentId(residentId);
		Room room = null;
		for (ResidentRoom residentRoom : residentRooms) {
			room = roomMapper.selectByPrimaryKey(residentRoom.getRoomid());
			units.add(room.getUnitid());
		}
		return units;
	}

	/**
	 * 
	 * @param mobilePhone
	 * @param smsCode
	 * @param password
	 * @param type
	 *            1、业主。
	 * @param name
	 * @param idcard
	 * @return
	 * @throws Exception
	 */
	public CommonResult register2Step1(String mobilePhone, String smsCode, String password, String name, String idcard,
			Integer idcardType, int type) throws Exception {
		CommonResult commonResult = new CommonResult();
		Map<String, String> result = new HashMap<>();
		if (!VerifyUtil.isMobile(mobilePhone)) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("手机号码格式不正确");
			return commonResult;
		}
		boolean bool = checkMobilePhone(mobilePhone);
		if (bool) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("手机号码已被注册");
			return commonResult;
		}
		Resident resident = new Resident();
		resident.setMobilephonenumber(mobilePhone);
		// 获取密码摘要
		password = DigestUtils.md5Hex(password);
		resident.setPassword(password);
		resident.setNickname(GenerateUtil.genNickName());
		resident.setDatetime(new Date());
		resident.setAppregister(AppRegisterState.YES.ordinal());
		resident.setIdcardnumber(idcard);
		resident.setIdcardtype(idcardType);
		resident.setName(name);
		resident.setRegisterstep(2);
		resident.setType(type);
		residentMapper.insert(resident);
		// 通过物业获取用户数据---------------------

		// 腾讯优图创建个体信息

		// 腾讯云独立模式帐号导入
		tencentVedioService.accountImport(mobilePhone, resident.getNickname(), null);
		// 删除掉redis中保存的注册短信验证码
		redisService.removeRegisterSmsCode(mobilePhone);
		result.put("residentId", resident.getId().toString());
		result.put("registerStep", String.valueOf(2));
		if (type == 1) {
			result.put("idcardNumber", "411324199206170079");
			result.put("name", "孙博文");

		} else {
			result.put("idcardNumber", idcard);
			result.put("name", name);
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("");
		commonResult.setObj(result);
		return commonResult;
	}

	/**
	 * 注册第二步
	 * 
	 * @param residentId
	 *            住户ID
	 * @param communityId
	 *            社区ID
	 * @param unitId
	 *            单元ID
	 * @param roomId
	 *            房屋ID
	 * @param type
	 *            住户类型
	 * @return
	 * @throws Exception
	 */
	public CommonResult register2Step2(Long residentId, Long communityId, Long unitId, Long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		//
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		Map<String, String> params = new HashMap<>();
		params.put("roomId", String.valueOf(roomId));
		params.put("residentId", String.valueOf(residentId));
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		if (null != residentRoom) {
			commonResult.setCode(CommonResult.RESIDENT_AND_ROOM_BINDED);
			commonResult.setMessage("此房屋已经绑定,不能重复添加");
			return commonResult;
		}
		// 判断信息是否匹配
		Community community = communityMapper.selectByPrimaryKey(communityId);
		Unit unit = unitMapper.selectByPrimaryKey(unitId);
		Room room = roomMapper.selectByPrimaryKey(roomId);
		if (null == room) {
			logger.error("addRooms has an error , roomid not exists");
			commonResult.setCode(CommonResult.ROOM_NOT_FOUND);
			commonResult.setMessage("未找到匹配的房屋信息");
			return commonResult;
		}
		if (null == unit) {
			logger.error("addRooms has an error , unitid not exists");
			commonResult.setCode(CommonResult.UNIT_NOT_FOUND);
			commonResult.setMessage("未找到匹配的单元信息");
			return commonResult;
		}
		if (null == community) {
			logger.error("addRooms has an error , communityid not exists");
			commonResult.setCode(CommonResult.COMMUNITY_NOT_FOUND);
			commonResult.setMessage("未找到匹配的社区信息");
			return commonResult;
		}
		if (unit.getCommunityid() != communityId) {
			logger.error("addRooms has an error , community,unit not match , community:" + communityId + ",unitid:"
					+ unitId);
			commonResult.setCode(CommonResult.COMMUNITY_AND_UNIT_NOT_MATCH);
			commonResult.setMessage("此小区下未找到匹配的单元信息");
			return commonResult;
		}
		if (room.getUnitid() != unitId) {
			logger.error("addRooms has an error , room,unit not match , roomid:" + roomId + ",unitid:" + unitId);
			commonResult.setCode(CommonResult.ROOM_AND_UNIT_NOT_MATCH);
			commonResult.setMessage("此单元下未找到匹配的房屋信息");
			return commonResult;
		}
		// 查询房屋是否已经有屋主
		List<ResidentRoom> residentRooms = residentRoomMapper.selectByRoomId(roomId);
		if (residentRooms.size() > 0) {
			commonResult.setCode(CommonResult.ROOM_HAS_LEADER);
			commonResult.setMessage("此房屋已有屋主,请通过屋主共享权限的方式添加房屋");
			return commonResult;
		}
		resident.setRegisterstep(3);
		// 更新Face信息到新加单元组中
		youtuFaceService.resetPerson(residentId);
		// 保存房屋与住户信息
		residentRoom = new ResidentRoom();
		residentRoom.setResidentid(residentId);
		residentRoom.setRoomid(roomId);
		residentRoom.setLeader(1);// 第一个添加房屋信息的用户设置为屋主
		residentRoom.setMobilephone(resident.getMobilephonenumber());
		residentRoom.setDatetime(new Date());
		residentRoom.setSharedauth(1);
		residentRoom.setType(resident.getType());// 设置住户类型
		// 保存到数据库
		residentRoomMapper.insert(residentRoom);
		// 清除resident的type值
		resident.setType(null);
		residentMapper.updateByPrimaryKey(resident);

		// 删除redis重复提交验证
		redisService.removeResidentRoomBinding(residentId, roomId);
		// 获取唇语验证码
		String validateCode = tencentImageService.liveGetFour();
		Map<String, String> result = new HashMap<>();
		result.put("validateCode", validateCode);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("房屋添加完成");
		commonResult.setObj(result);
		return commonResult;
	}

	/**
	 * 注册第三步
	 * 
	 * @param residentId
	 *            住户ID
	 * @param file
	 *            文件
	 * @return
	 */
	public CommonResult register2step3(String validateCode, String etag, String key, Long residentId,
			String idcardLiveDetectFourResults) throws Exception {
		// 修改当前注册步骤为第三步
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		resident.setRegisterstep(-1);
		residentMapper.updateByPrimaryKey(resident);

		CommonResult commonResult = new CommonResult();
		JSONObject jsonObject = new JSONObject(idcardLiveDetectFourResults);
		int code = jsonObject.getInt("code");
		String message = jsonObject.getString("message");
		if (code == 0) {
			JSONObject data = jsonObject.getJSONObject("data");
			int liveStatus = data.getInt("live_status");
			String liveMsg = data.getString("live_msg");
			int compareStatus = data.getInt("compare_status");
			String compareMsg = data.getString("compare_msg");
			int sim = data.getInt("sim");
			String videoPhoto = data.getString("video_photo");
			if (sim > 90) {
				// 生成注册时返回的第一张人脸信息的名字
				String imageKey = GenerateUtil.getRegisterImage(residentId);
				// 文件地址
				String path = "D:" + File.separatorChar + ".jpg";
				// base64转换为图片
				File file = GenerateUtil.Base64ToImage(videoPhoto, path);
				// 保存视频到cos结果
				IdcardLiveDetectFourCos idcardLiveDetectFourCos = new IdcardLiveDetectFourCos();
				idcardLiveDetectFourCos.setDatetime(new Date());
				idcardLiveDetectFourCos.setEtag(etag);
				idcardLiveDetectFourCos.setKeyname(key);
				idcardLiveDetectFourCos.setValidatecode(validateCode);
				idcardLiveDetectFourCos.setResidentId(residentId);
				idcardLiveDetectFourCosMapper.insert(idcardLiveDetectFourCos);
				// 文件为null
				if (null == file) {
					logger.error(
							"register2step3,residentId:" + residentId + ",base64转换文件失败," + idcardLiveDetectFourResults);
					commonResult.setCode(CommonResult.SUCCESS);
					commonResult.setMessage("注册失败");
					return commonResult;
				}
				// 上传到cos
				String imageEtag = tencentCOSService.uploadFaceByFile(file, imageKey);
				// 删除文件
				file.deleteOnExit();
				// 保存人脸识别信息到数据库
				FaceCOS faceCOS = new FaceCOS();
				faceCOS.setDatetime(new Date());
				faceCOS.setEtag(imageEtag);
				faceCOS.setKeyname(imageKey);
				faceCOS.setResidentid(residentId);
				faceCOSMapper.insert(faceCOS);
				// 返回值
				commonResult.setCode(CommonResult.SUCCESS);
				commonResult.setMessage("核身成功");
				commonResult.setObj(resident);
				return commonResult;
			} else {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("人脸核身失败");
				return commonResult;
			}
		} else {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("失败");
			return commonResult;
		}
	}

	/**
	 * 注册第三步
	 * 
	 * @param validateCode
	 * @param residentId
	 * @param file
	 * @param idcardNumber
	 * @param idcardName
	 * @return
	 * @throws Exception
	 */
	public CommonResult register2step3(String validateCode, Long residentId, MultipartFile file, String idcardNumber,
			String idcardName) throws Exception {
		CommonResult commonResult = new CommonResult();
		// 生成Vedio名字
		StringBuffer idcardLiveDetectFourVedioName = new StringBuffer();
		idcardLiveDetectFourVedioName.append(staticPath).append(System.currentTimeMillis())
				.append(idcardNumber).append(".mp4");
		File targetFile = new File(idcardLiveDetectFourVedioName.toString());
		file.transferTo(targetFile);
		// 生成Image名字
		StringBuffer idcardLiveDetectFourImageName = new StringBuffer();
		idcardLiveDetectFourImageName.append(staticPath).append(System.currentTimeMillis())
				.append(idcardNumber).append(".jpg");

		// 第一步活体唇语检测
		commonResult = tencentImageService.idcardLiveDetectFour(validateCode, targetFile, idcardNumber, idcardName);
		if (commonResult.getCode() != 0 && commonResult.getCode() != 1) {
			if (commonResult.getCode() == -5804) {
				commonResult.setMessage("身份证号码无效");
			} else if (commonResult.getCode() == -5016) {
				commonResult.setMessage("活体检测");
			} else if (commonResult.getCode() == -5801) {
				commonResult.setMessage("缺少身份证号码或身份证姓名");
			} else if (commonResult.getCode() == -5010) {
				commonResult.setMessage("唇动检测失败，视频里的人嘴巴未张开或者张开幅度小");
			} else if (commonResult.getCode() == -5002) {
				commonResult.setMessage("唇语失败");
			} else if (commonResult.getCode() == -5007) {
				commonResult.setMessage("视频没有声音");
			} else if (commonResult.getCode() == -5005) {
				commonResult.setMessage("自拍照解析照片不足，视频里检测到的人脸较少");
			} else if (commonResult.getCode() == -5013) {
				commonResult.setMessage("视频里的声音太小");
			} else if (commonResult.getCode() == -5012) {
				commonResult.setMessage("视频中噪声太大");
			} else if (commonResult.getCode() == -5806) {
				commonResult.setMessage("身份证信息不匹配");
			} else {
				commonResult.setMessage("注册失败");
			}
			commonResult.setCode(CommonResult.FAILURE);
			return commonResult;
		}
		String base64 = commonResult.getObj().toString();
		// 第二部上传视频到cos
		String key = GenerateUtil.getRegisterVedio(residentId);
		String etag = tencentCOSService.uploadIdcardLiveDetectFourVedio(key, targetFile);
		// 保存到数据库
		IdcardLiveDetectFourCos idcardLiveDetectFourCos = new IdcardLiveDetectFourCos();
		idcardLiveDetectFourCos.setDatetime(new Date());
		idcardLiveDetectFourCos.setEtag(etag);
		idcardLiveDetectFourCos.setKeyname(key);
		idcardLiveDetectFourCos.setResidentId(residentId);
		idcardLiveDetectFourCos.setValidatecode(validateCode);
		idcardLiveDetectFourCosMapper.insert(idcardLiveDetectFourCos);
		// 第三步上传图片到cos
		String imageKey = GenerateUtil.getRegisterImage(residentId);
		File image = GenerateUtil.Base64ToImage(base64, idcardLiveDetectFourImageName.toString());
		String imageEtag = tencentCOSService.uploadFaceByFile(image, imageKey);
		// 获取图片地址url
		String url = tencentCOSService.getFacePresignedUrl(imageKey);
		// 提交信息到youtu
		// 创建youtuPerson
		List<ResidentRoom> residentRooms = residentRoomMapper.selectByResidentId(residentId);
		ResidentRoom residentRoom = residentRooms.get(0);
		Room room = roomMapper.selectByPrimaryKey(residentRoom.getRoomid());
		List<String> groupIds = new ArrayList<>();
		groupIds.add(room.getUnitid().toString());
		// 创建人脸信息
		youtuFaceService.newPerson(url, residentId, groupIds);
		// 创建youtuFace
		CommonResult newFaceCommonResult = youtuFaceService.newFace(residentId, url);
		FaceCOS faceCOS = new FaceCOS();
		faceCOS.setDatetime(new Date());
		faceCOS.setEtag(imageEtag);
		faceCOS.setKeyname(imageKey);
		faceCOS.setResidentid(residentId);
		if (newFaceCommonResult.getCode() == CommonResult.SUCCESS)
			faceCOS.setYoutuid(newFaceCommonResult.getObj().toString());
		faceCOSMapper.insert(faceCOS);
		// 修改resident注册步骤
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		resident.setRegisterstep(-1);
		residentMapper.updateByPrimaryKey(resident);
		// 刪除之前的视频
		targetFile.deleteOnExit();
		// 删除之前的图片
		image.deleteOnExit();
		// 发送推送房屋请求推送到服务器
		if(residentRoom.getType()==2||residentRoom.getType()==3){
			roomRequestAuthorizationService.register(residentId, room.getId());
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("注册完成");
		return commonResult;
	}

}
