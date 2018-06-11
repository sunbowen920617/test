package com.zheman.lock.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.CommunityMapper;
import com.zheman.lock.dao.DeviceMapper;
import com.zheman.lock.dao.FaceCOSMapper;
import com.zheman.lock.dao.LoginHistoryMapper;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.dao.ResidentRoomBackupMapper;
import com.zheman.lock.dao.ResidentRoomMapper;
import com.zheman.lock.dao.RoomMapper;
import com.zheman.lock.dao.ShareMapper;
import com.zheman.lock.dao.UnitMapper;
import com.zheman.lock.dao.UnlockHistoryMapper;
import com.zheman.lock.model.Community;
import com.zheman.lock.model.Device;
import com.zheman.lock.model.Resident;
import com.zheman.lock.model.ResidentRoom;
import com.zheman.lock.model.ResidentRoomBackup;
import com.zheman.lock.model.Room;
import com.zheman.lock.model.Share;
import com.zheman.lock.model.Unit;
import com.zheman.lock.model.enumtion.ShareTypeEnum;
import com.zheman.lock.model.enumtion.SmsTypeEnum;
import com.zheman.lock.model.trans.TransResidentRoom;
import com.zheman.lock.util.GenerateUtil;

@Service
public class RoomService {

	Logger logger = LoggerFactory.getLogger(RoomService.class);

	@Autowired
	RoomMapper roomMapper;

	@Autowired
	RedisService redisService;

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	ResidentRoomMapper residentRoomMapper;

	@Autowired
	CommunityMapper communityMapper;

	@Autowired
	UnitMapper unitMapper;

	@Autowired
	ShareMapper shareMapper;

	@Autowired
	SmsService smsService;

	@Autowired
	LoginHistoryMapper loginHistoryMapper;

	@Autowired
	PushService pushService;

	@Autowired
	NoticeService noticeService;

	@Autowired
	DeviceMapper deviceMapper;

	@Autowired
	UnlockHistoryMapper unlockHistoryMapper;

	@Autowired
	ResidentRoomBackupMapper residentRoomBackupMapper;

	@Autowired
	TencentCOSService tencentCOSService;

	@Autowired
	FaceCOSMapper faceCOSMapper;

	@Autowired
	YoutuFaceService youtuFaceService;

	@Autowired
	AdvertService advertService;

	@Autowired
	ResidentService residentService;

	@Value("${share.qrcode.width}")
	int width;

	@Value("${share.qrcode.height}")
	int height;
	
	@Value("${resource.path}")
	String staticPath;

	/**
	 * 查詢住戶的房屋列表 添加广告信息
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public CommonResult findAllRoomsByLogin(String token) throws Exception {
		CommonResult commonResult = new CommonResult();
		List<TransResidentRoom> transResidentRooms = new ArrayList<>();
		// 通過登陸token從redis中查詢到對應的ID
		long residentId = redisService.getResidentIdByLoginToken(token);
		List<ResidentRoom> residentRooms = residentRoomMapper.selectByResidentId(residentId);
		for (ResidentRoom residentRoom : residentRooms) {
			transResidentRooms.add(convertToTrans(residentRoom));
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询成功");
		commonResult.setObj(transResidentRooms);
		return commonResult;
	}

	/**
	 * 添加房屋 1、判断房屋信息是否匹配 2、确认房屋内是否有屋主 3、保存屋主信息 4、更新用户人脸识别信息
	 * 
	 * @param communityId
	 *            社區ID
	 * @param unitId
	 *            单元ID
	 * @param roomId
	 *            房屋ID
	 * @param mobilePhone
	 *            手机号码
	 * @param smsCode
	 *            短信码
	 * @throws Exception
	 */
	public CommonResult addRoomResetPerson(String token, long communityId, long unitId, long roomId, String mobilePhone,
			String smsCode) throws Exception {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		//

		// 根据token查询redis中的信息
		long residentId = redisService.getResidentIdByLoginToken(token);
		//
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
		// 判断手机验证码
		String redisSmsCode = redisService.getAddRoomSmsCode(mobilePhone);
		if (!StringUtils.equals(redisSmsCode, smsCode)) {
			commonResult.setCode(CommonResult.SMSCODE_ERROR);
			commonResult.setMessage("手机验证码不正确");
			return commonResult;
		}
		// 查询房屋是否已经有屋主
		List<ResidentRoom> residentRooms = residentRoomMapper.selectByRoomId(roomId);
		if (residentRooms.size() > 0) {
			commonResult.setCode(CommonResult.ROOM_HAS_LEADER);
			commonResult.setMessage("此房屋已有屋主,请通过屋主共享权限的方式添加房屋");
			return commonResult;
		}

		// 更新Face信息到新加单元组中
		youtuFaceService.resetPerson(residentId);
		// 保存房屋与住户信息
		residentRoom = new ResidentRoom();
		residentRoom.setResidentid(residentId);
		residentRoom.setRoomid(roomId);
		residentRoom.setLeader(1);// 第一个添加房屋信息的用户设置为屋主
		residentRoom.setMobilephone(mobilePhone);
		residentRoom.setDatetime(new Date());
		residentRoom.setSharedauth(1);
		// 保存到数据库
		residentRoomMapper.insert(residentRoom);
		// 删除redis中的短信验证码
		redisService.removeAddRoomSmsCode(mobilePhone);
		// 删除redis重复提交验证
		redisService.removeResidentRoomBinding(residentId, roomId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("房屋添加完成");
		commonResult.setObj(residentId);
		return commonResult;
	}

	/**
	 * 根据ID查询房屋信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TransResidentRoom findByRoomIdAndResidentId(String token, long roomId) throws Exception {
		Map<String, String> params = new HashMap<>();
		long residentId = redisService.getResidentIdByLoginToken(token);
		params.put("roomId", String.valueOf(roomId));
		params.put("residentId", String.valueOf(residentId));
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		return convertToTrans(residentRoom);
	}

	/**
	 * 房屋信息转换为VO
	 * 
	 * @param residentRoom
	 * @return
	 */
	private TransResidentRoom convertToTrans(ResidentRoom residentRoom) {
		StringBuffer address = new StringBuffer();
		TransResidentRoom transResidentRoom = new TransResidentRoom();
		// 房屋
		Room room = roomMapper.selectByPrimaryKey(residentRoom.getRoomid());
		transResidentRoom.setRoomCode(room.getRoomcode());
		transResidentRoom.setRoomId(residentRoom.getRoomid());
		// 單元
		Unit unit = unitMapper.selectByPrimaryKey(room.getUnitid());
		transResidentRoom.setUnit(unit.getUnitcode());
		// 社区
		Community community = communityMapper.selectByPrimaryKey(unit.getCommunityid());
		// 设备
		Device device = deviceMapper.selectByPrimaryKey(unit.getDeviceid());
		if (null != device) {
			if (null != device.getGpsaddress()) {
				String[] gpsAddress = StringUtils.split(device.getGpsaddress(), ",");
				transResidentRoom.setLon(gpsAddress[1]);
				transResidentRoom.setLat(gpsAddress[0]);
			}
		}
		transResidentRoom.setCommunity(community.getName());
		address.append(community.getAddress()).append(unit.getUnitcode()).append("单元").append(room.getRoomcode());
		transResidentRoom.setAddress(address.toString());
		if (residentRoom.getLeader() != null && residentRoom.getLeader() == 1)
			transResidentRoom.setLeader(true);
		// 可共享权限
		if (null != residentRoom.getSharedauth() && residentRoom.getSharedauth() == 1)
			transResidentRoom.setEnableShare(true);
		else
			transResidentRoom.setEnableShare(false);
		transResidentRoom.setImageUrl(community.getImageurl());
		transResidentRoom.setTelePhone(community.getTelephone());
		// 1、房屋 。 2、广告
		transResidentRoom.setType(1);
		return transResidentRoom;
	}

	/**
	 * 生成二维码并返回静态资源URL
	 * 
	 * @param token
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CommonResult genShareQrCode(String url, String token, long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		// 查詢登陸用戶的ID
		long residentId = redisService.getResidentIdByLoginToken(token);
		StringBuffer bufUrl = new StringBuffer();
		// 獲取當前時間戳
		long timestemp = System.currentTimeMillis();
		// 保存共享记录到数据库
		Share share = new Share();
		share.setShareresidentid(residentId);
		share.setSharetype(ShareTypeEnum.QRCODE.ordinal());
		share.setDatetime(new Date());
		share.setUuid(String.valueOf(timestemp));
		share.setRoomid(roomId);
		share.setAccepted(0);
		// 插入数据并返回ID
		shareMapper.insert(share);
		// 組裝二維碼數據
		bufUrl.append("/share/sharebyqrcode").append("?").append("shareId=").append(share.getId());
		String dataHandle = new String(bufUrl.toString().getBytes());
		BitMatrix bitMatrix = new MultiFormatWriter().encode(dataHandle, BarcodeFormat.QR_CODE, width, height);
		// 生成二維碼名字
		String name = GenerateUtil.genShareQrcodeName(timestemp, residentId);
		String qrcodeFileName = name + ".png";
		logger.info("qrcode name is :" + name);
		File file = new File(staticPath + File.separatorChar + qrcodeFileName);
		logger.info("qrcode path is :" + file.getAbsolutePath());
		MatrixToImageWriter.writeToFile(bitMatrix, "png", file);
		// 存放到cos
		Map<String, String> result = tencentCOSService.uploadQrcode(residentId, file);
		if (!file.delete()) {
			logger.info("genShareQrCode has an error : file delete failure:" + file.getAbsolutePath());
		}
		String etag = result.get("etag");
		share.setQrcodepath(etag);
		String key = result.get("key");
		share.setKeyname(key);
		shareMapper.updateByPrimaryKeySelective(share);
		// 存放到redis
		redisService.setShareQrcode(share.getId(), timestemp);
		// 获取二维码下载地址
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("二维码已生成");
		commonResult.setObj(tencentCOSService.getQrcodePresignedUrl(key));
		return commonResult;
	}

	/**
	 * 通过二维码共享 1、验证用户是否已经绑定 2、验证二维码是否有效 3、绑定房屋与住户的信息 4、保存共享历史记录并删除redis
	 * 
	 * @param token
	 * @param timeStemp
	 * @param inviterId
	 * @param roomId
	 * @return
	 * @throws Exception
	 */
	public CommonResult bindRoomByQrcodeResetPerson(String token, long timeStemp, long shareId) throws Exception {
		CommonResult commonResult = new CommonResult();
		// 查询恭喜信息
		Share share = shareMapper.selectByPrimaryKey(shareId);
		long roomId = share.getRoomid();
		long inviterId = share.getShareresidentid();
		Long residentId = redisService.getResidentIdByLoginToken(token);
		Map<String, String> params = new HashMap<>();
		params.put("residentId", String.valueOf(residentId));
		params.put("roomId", String.valueOf(share.getRoomid()));
		// 验证二维码是否有效
		if (share.getAccepted() == 1) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("二维码已失效");
			return commonResult;
		}
		// 不允许邀请自己
		if (share.getShareresidentid() == residentId) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("不允许邀请自己");
			return commonResult;
		}
		// 验证用户是否已经绑定
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		if (null != residentRoom) {
			commonResult.setCode(CommonResult.RESIDENT_AND_ROOM_BINDED);
			commonResult.setMessage("已拥有此房屋权限");
			return commonResult;
		}
		// 验证二维码是否有效
		String timestempTemp = redisService.getShareQrcode(shareId);
		if (StringUtils.isBlank(timestempTemp)) {
			commonResult.setCode(CommonResult.QRCODE_INVALID);
			commonResult.setMessage("二维码已失效");
			return commonResult;
		}
		// 验证房屋是否存在
		Room room = roomMapper.selectByPrimaryKey(roomId);
		if (null == room) {
			commonResult.setCode(CommonResult.ROOM_NOT_FOUND);
			commonResult.setMessage("房屋不存在");
			return commonResult;
		}
		// 绑定房屋与住户的信息
		residentRoom = new ResidentRoom();
		residentRoom.setLeader(0);
		residentRoom.setRoomid(roomId);
		residentRoom.setResidentid(residentId);
		residentRoom.setInviterid(inviterId);
		residentRoom.setDatetime(new Date());
		residentRoom.setSharedauth(1);
		residentRoomMapper.insert(residentRoom);
		// 更新share信息
		share.setAcceptresidentid(residentId);
		share.setAccepted(1);
		shareMapper.updateByPrimaryKey(share);
		// 删除二维码信息
		redisService.removeShareQrcodePrefix(inviterId);
		// 删除COS文件
		tencentCOSService.deleteQrcode(share.getKeyname());
		// 更新人脸信息
		youtuFaceService.resetPerson(residentId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("房屋绑定完成");
		commonResult.setObj(residentId);
		return commonResult;
	}

	/**
	 * 通过账户共享房屋 用户与房屋是否已经绑定 1、根据被邀请手机号码查询住户 2、用户存在，推送消息给住户并保存一份通知
	 * 3、用户不存在发送短信给指定手机号码
	 * 
	 * @return
	 * @throws Exception
	 */
	public CommonResult shareByAccount(String token, String acceptMobilePhone, long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		// 当前登录住户ID
		long residentId = redisService.getResidentIdByLoginToken(token);
		// 当前登录住户的手机号码
		String residentMobilePhone = redisService.getMobilePhoneByLoginToken(token);
		// 查询被邀请者的ID
		Resident resident = residentMapper.selectByMobilePhone(acceptMobilePhone);
		// 被邀请用户不存在发送短信到指定的手机号码
		if (null == resident) {
			logger.debug("mobilePhone not exist :" + acceptMobilePhone);
			smsService.sendSms(acceptMobilePhone, SmsTypeEnum.SHAREROOM.ordinal());
			commonResult.setCode(CommonResult.MOBILEPHONE_NOT_REGISTER);
			commonResult.setMessage("手机号码未注册");
			return commonResult;
		}
		// 不能重复发送邀请
		Map<String, String> params = new HashMap<>();
		Share share = shareMapper.selectByFourElement(residentId, resident.getId(), roomId, 0);
		if (null != share) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("请不要重复发送邀请");
			return commonResult;
		}
		// 被邀请者与房屋的绑定信息
		params = new HashMap<>();
		params.put("roomId", String.valueOf(roomId));
		params.put("residentId", String.valueOf(resident.getId()));
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		// 判断是否已经绑定
		if (null != residentRoom) {
			commonResult.setCode(CommonResult.RESIDENT_AND_ROOM_BINDED);
			commonResult.setMessage("所邀请的用户已经有此房屋的权限");
			return commonResult;
		}
		Room room = roomMapper.selectByPrimaryKey(roomId);
		Unit unit = unitMapper.selectByPrimaryKey(room.getUnitid());
		Community community = communityMapper.selectByPrimaryKey(unit.getCommunityid());
		// 房屋位置
		StringBuffer roomAddress = new StringBuffer();
		roomAddress.append(community.getName()).append(unit.getUnitcode()).append("单元").append(room.getRoomcode())
				.append("室");
		// 保存Share
		share = new Share();
		share.setDatetime(new Date());
		share.setSharetype(ShareTypeEnum.ACCOUNT.ordinal());
		share.setShareresidentid(residentId);
		share.setAccepted(0);
		share.setRoomid(roomId);
		share.setAcceptresidentid(resident.getId());
		shareMapper.insert(share);
		// 保存Notice
		long noticeId = noticeService.addShareRoomNotice(roomAddress.toString(), residentMobilePhone, resident.getId(),
				share.getId());
		// 推送房屋共享邀请消息
		pushService.pushShareRoomMessage(residentMobilePhone, acceptMobilePhone, roomAddress.toString(), noticeId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("邀请已发送");
		commonResult.setObj(share.getId());
		return commonResult;
	}

	/**
	 * 通过APP主动开锁
	 * 
	 * @param token
	 * @param roomId
	 * @return
	 * @throws Exception
	 */
	public CommonResult unlockByApp(String token, long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		Map<String, String> params = new HashMap<>();
		params.put("residentId", String.valueOf(residentId));
		params.put("roomId", String.valueOf(roomId));
		// 查看住户是否有此房屋的权限
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		if (null == residentRoom) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("没有指定房屋的权限");
			return commonResult;
		}
		// 保存开锁记录
		// UnlockHistory unlockHistory = new UnlockHistory();
		// unlockHistory.setDatetime(new Date());
		// unlockHistory.setRoomid(roomId);
		// unlockHistory.setResidentid(residentId);
		// unlockHistory.setType(UnlockTypeEnum.APP.ordinal());
		// unlockHistory.setState(1);
		// unlockHistoryMapper.insert(unlockHistory);
		// 推送开锁消息到房屋所在的设备
		Room room = roomMapper.selectByPrimaryKey(roomId);
		Unit unit = unitMapper.selectByPrimaryKey(room.getUnitid());
		Device device = deviceMapper.selectByPrimaryKey(unit.getDeviceid());
		if (null == device) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("未找到对应的设备");
			return commonResult;
		}
		if (device.getActivate() == 0) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("该设备未激活");
			return commonResult;
		}
		// 推送开锁通知到访客端

		// JSONObject jsonObject = pushService.unlock(device.getMobilephone(),
		// unlockHistory.getId()); 根据手机号码推送
		JSONObject jsonObject = pushService.unlock(device.getDevicecode(), null, 1); // 根据设备号推送
		int code = jsonObject.getInt("ret_code");
		if (code != 0) {
			// unlockHistory.setState(2);
			// unlockHistory.setErrorreason(message);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("开锁失败");
			return commonResult;
		}
		// unlockHistory.setState(1);
		// unlockHistoryMapper.updateByPrimaryKey(unlockHistory);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("开锁成功");
		return commonResult;
	}

	/**
	 * 取消住户与房屋关联
	 * 
	 * @return
	 * @throws Exception
	 */
	public CommonResult cancelShareResetPerson(long residentId, long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		Map<String, String> params = new HashMap<>();
		params.put("residentId", String.valueOf(residentId));
		params.put("roomId", String.valueOf(roomId));
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		if (null == residentRoom) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("未找到此房屋与住户的绑定信息");
			return commonResult;
		}
		ResidentRoomBackup residentRoomBackup = new ResidentRoomBackup();
		// copy属性到指定的对象
		BeanUtils.copyProperties(residentRoom, residentRoomBackup);
		// 保存备份信息
		residentRoomBackupMapper.insert(residentRoomBackup);
		// 删除原始数据
		residentRoomMapper.deleteByResidentIdAndRoomId(params);
		// 重置人脸识别信息
		// youtuFaceService.resetPerson(residentId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("共享权限已取消");
		commonResult.setObj(residentId);
		return commonResult;
	}

	/**
	 * 查询单元下的所有房屋信息
	 * 
	 * @param unitId
	 *            单元ID
	 * @return
	 */
	public CommonResult selectRoomsByUnitId(long unitId) throws Exception {
		CommonResult commonResult = new CommonResult();
		List<Room> rooms = roomMapper.selectRoomsByUnitId(unitId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj(rooms);
		return commonResult;
	}

	/**
	 * 查询单元下无居民的房屋
	 * 
	 * @param unitId
	 *            单元ID
	 * @return
	 */
	public CommonResult selectRoomsNotHaveResident(long unitId) throws Exception {
		CommonResult commonResult = new CommonResult();
		List<Room> rooms = roomMapper.selectRoomsNotHaveResident(unitId);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj(rooms);
		return commonResult;
	}

	/**
	 * 根据房屋ID获取房屋的具体地址(社区+单元+室号)
	 * @param roomId
	 * @return
	 */
	public String getRoomAddress(Long roomId) {
		StringBuffer address = new StringBuffer();
		Room room = roomMapper.selectByPrimaryKey(roomId);
		Unit unit = unitMapper.selectByPrimaryKey(room.getUnitid());
		Community community = communityMapper.selectByPrimaryKey(unit.getCommunityid());
		address.append(community.getName()).append(unit.getUnitcode()).append("单元").append(room.getRoomcode())
				.append("室");
		return address.toString();
	}

}
