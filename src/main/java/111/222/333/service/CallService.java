package com.zheman.lock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.CallHistoryMapper;
import com.zheman.lock.dao.DeviceMapper;
import com.zheman.lock.dao.ProfileFaceCOSMapper;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.dao.ResidentRoomMapper;
import com.zheman.lock.dao.RoomMapper;
import com.zheman.lock.dao.UnitMapper;
import com.zheman.lock.dao.UnlockHistoryMapper;
import com.zheman.lock.model.CallHistory;
import com.zheman.lock.model.Device;
import com.zheman.lock.model.ProfileFaceCOS;
import com.zheman.lock.model.Resident;
import com.zheman.lock.model.ResidentRoom;
import com.zheman.lock.model.Room;
import com.zheman.lock.model.Unit;
import com.zheman.lock.model.UnlockHistory;
import com.zheman.lock.model.enumtion.CallStateEnum;
import com.zheman.lock.model.trans.TransCallHistory;

@Service
public class CallService {

	Logger logger = LoggerFactory.getLogger(CallService.class);

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	CallHistoryMapper callHistoryMapper;

	@Autowired
	RedisService redisService;

	@Autowired
	TencentCOSService tencentCOSService;

	@Autowired
	ResidentRoomMapper residentRoomMapper;

	@Autowired
	PushService pushService;

	@Autowired
	RoomMapper roomMapper;

	@Autowired
	UnitMapper unitMapper;

	@Autowired
	DeviceMapper deviceMapper;

	@Autowired
	ProfileFaceCOSMapper profileFaceCOSMapper;

	@Autowired
	UnlockHistoryMapper unlockHistoryMapper;

	/**
	 * 根据 token查询通话记录
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public CommonResult selectCalls(String token, int page, int size) throws Exception {
		CommonResult commonResult = new CommonResult();
		// 查询到所有的房屋
		List<Long> ids = new ArrayList<>();
		long residentId = redisService.getResidentIdByLoginToken(token);
		List<ResidentRoom> residentRooms = residentRoomMapper.selectByResidentId(residentId);
		if (null == residentRooms || residentRooms.size() <= 0) {
			commonResult.setCode(CommonResult.RESIDENT_NOT_BINDED_ROOM);
			commonResult.setMessage("未绑定任何房屋");
			return commonResult;
		}
		for (ResidentRoom residentRoom : residentRooms) {
			ids.add(residentRoom.getRoomid());
		}
		int offset = (page - 1) * size;
		List<CallHistory> callHistories = callHistoryMapper.selectByRoomIds(ids, offset, size);
		if (null == callHistories || callHistories.size() <= 0) {
			commonResult.setCode(CommonResult.NOT_FOUND_CALLHISTORY);
			commonResult.setMessage("未找到访客记录");
		}
		// 转换为trans对象
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj(convertToTransCallHistories(callHistories));
		return commonResult;
	}

	/**
	 * 查询单个通话记录
	 * 
	 * @param callId
	 * @return
	 */
	public CommonResult selectCall(long callId) {
		CommonResult commonResult = new CommonResult();
		CallHistory callHistory = callHistoryMapper.selectByPrimaryKey(callId);
		TransCallHistory transCallHistory = convertToTransCallHistory(callHistory);
		if (null == transCallHistory) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("找不到对应的呼叫记录");
			return commonResult;
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询成功");
		commonResult.setObj(transCallHistory);
		return commonResult;
	}

	public List<TransCallHistory> convertToTransCallHistories(List<CallHistory> callHistories) {
		List<TransCallHistory> transCallHistories = new ArrayList<>();
		for (CallHistory callHistory : callHistories) {
			transCallHistories.add(convertToTransCallHistory(callHistory));
		}
		return transCallHistories;
	}

	// 转换为传输对象
	public TransCallHistory convertToTransCallHistory(CallHistory callHistory) {
		TransCallHistory transCallHistory = new TransCallHistory();
		Resident resident = residentMapper.selectByPrimaryKey(callHistory.getResidentid());
		// 获取指定的文件
		ProfileFaceCOS profileFaceCOS = profileFaceCOSMapper.selectByResidentId(resident.getId());
		// url
		String url = "";
		if (null != profileFaceCOS) {
			url = tencentCOSService.getProfileFacePresignedUrl(profileFaceCOS.getDownloadurl());
			transCallHistory.setFaceDatetime(profileFaceCOS.getDatetime().getTime());
		}
		transCallHistory.setFaceImageUrl(url);
		transCallHistory.setName(resident.getName());
		transCallHistory.setDatetime(callHistory.getCalldatetime());
		transCallHistory.setId(callHistory.getId());

		if (null != callHistory.getUnlockhistoryid()) {
			UnlockHistory unlockHistory = new UnlockHistory();
			unlockHistory = unlockHistoryMapper.selectByPrimaryKey(callHistory.getUnlockhistoryid());
			transCallHistory.setState(unlockHistory.getState());
		}
		transCallHistory.setResidentId(resident.getId());
		return transCallHistory;
	}

	/**
	 * 音频接通
	 * 
	 * @param callId
	 * 
	 * 
	 * @param timestemp
	 * @return
	 * @throws Exception
	 */
	public CommonResult voiceAnswer(long callId, long timestemp) throws Exception {
		CommonResult commonResult = new CommonResult();
		CallHistory callHistory = callHistoryMapper.selectByPrimaryKey(callId);
		callHistory.setAnswerdatetime(timestemp);
		callHistory.setAnswerdevice(2);
		callHistory.setAnswertype(1);
		callHistory.setState(CallStateEnum.ANSWER.ordinal());
		callHistoryMapper.updateByPrimaryKey(callHistory);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("");
		commonResult.setObj("ok");
		return commonResult;
	}

	/**
	 * 视频接通
	 * 
	 * @param callId
	 * @param timestemp
	 * @return
	 */
	public CommonResult vedioAnswer(long callId, long timestemp) {
		CommonResult commonResult = new CommonResult();
		CallHistory callHistory = callHistoryMapper.selectByPrimaryKey(callId);
		callHistory.setAnswerdatetime(timestemp);
		callHistory.setAnswerdevice(2);
		callHistory.setAnswertype(2);
		callHistory.setState(CallStateEnum.ANSWER.ordinal());
		callHistoryMapper.updateByPrimaryKeySelective(callHistory);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("");
		commonResult.setObj("ok");
		return commonResult;
	}

	/**
	 * 挂断
	 * 
	 * @param callId
	 * @param timestemp
	 * @return
	 */
	public CommonResult hangup(long callId, long timestemp) {
		CommonResult commonResult = new CommonResult();
		CallHistory callHistory = callHistoryMapper.selectByPrimaryKey(callId);
		callHistory.setEnddatetime(timestemp);
		callHistory.setState(CallStateEnum.HANGUP.ordinal());
		callHistoryMapper.updateByPrimaryKeySelective(callHistory);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("");
		commonResult.setObj("ok");
		return commonResult;
	}

	/**
	 * 拒绝
	 * 
	 * @param callId
	 *            通话ID
	 * @param timestemp
	 * @return
	 */
	CommonResult commonResult = new CommonResult();

	public CommonResult reject(long callId, long timestemp) {
		CallHistory callHistory = callHistoryMapper.selectByPrimaryKey(callId);
		callHistory.setEnddatetime(timestemp);
		callHistory.setState(CallStateEnum.REJECT.ordinal());
		callHistoryMapper.updateByPrimaryKeySelective(callHistory);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("");
		commonResult.setObj("ok");
		return commonResult;
	}

	/**
	 * 开锁
	 * 
	 * @param callId
	 *            通话ID
	 * @param timestemp
	 * @return
	 */
	public CommonResult unlock(long callId, long timestemp) {
		CommonResult commonResult = new CommonResult();
		CallHistory callHistory = callHistoryMapper.selectByPrimaryKey(callId);
		Room room = roomMapper.selectByPrimaryKey(callHistory.getRoomid());
		Unit unit = unitMapper.selectByPrimaryKey(room.getUnitid());
		Device device = deviceMapper.selectByPrimaryKey(unit.getDeviceid());
		callHistory.setEnddatetime(timestemp);
		callHistory.setUnlockdatetime(timestemp);
		callHistory.setState(CallStateEnum.UNLOCK.ordinal());
		callHistoryMapper.updateByPrimaryKeySelective(callHistory);
		// push开锁消息
		pushService.unlock(device.getDevicecode(), callHistory.getId(), 2);
		// 设置commonResult
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("开锁成功");
		commonResult.setObj("ok");
		return commonResult;
	}

	/**
	 * 查询最新 正在呼叫的通话记录
	 * 
	 * @param token
	 *            住户 token
	 * @param roomId
	 *            房屋ID
	 * @param timestemp
	 *            时间戳
	 * @return
	 * @throws Exception
	 */
	public CommonResult selectCurrentCall(String token) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		String mobilePhone = redisService.getMobilePhoneByLoginToken(token);
		CallHistory callHistory = callHistoryMapper.selectCurrentCall(residentId);
		Map map = new HashMap<>();
		if (null != callHistory) {
			map.put("callHistoryId", callHistory.getId());
			map.put("mobilePhone", mobilePhone);
		} else {
			map.put("callHistoryId", "");
			map.put("mobilePhone", "");
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询成功");
		commonResult.setObj(map);
		return commonResult;
	}

	/**
	 * 开锁
	 * 
	 * @param lockState
	 *            是否开锁
	 * @param uniqueCode
	 *            唯一码
	 * @return
	 */
	public CommonResult lockState(long callHistoryId, long timestemp) {
		CommonResult commonResult = new CommonResult();
		CallHistory callHistory = callHistoryMapper.selectByPrimaryKey(callHistoryId);
		callHistory.setUnlockdatetime(timestemp);
		callHistory.setState(5);
		callHistoryMapper.updateByPrimaryKeySelective(callHistory);
		commonResult.setCode(CommonResult.SUCCESS);
		return commonResult;
	}

}
