package com.zheman.lock.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.dao.RoomMapper;
import com.zheman.lock.dao.RoomRequestAuthorizationMapper;
import com.zheman.lock.model.Resident;
import com.zheman.lock.model.Room;
import com.zheman.lock.model.RoomRequestAuthorization;

@Service
public class RoomRequestAuthorizationService {

	@Autowired
	RoomRequestAuthorizationMapper roomRequestAuthorizationMapper;

	@Autowired
	DictionaryService dictionaryService;

	@Autowired
	PushService pushService;

	@Autowired
	NoticeService noticeService;

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	RoomService roomService;

	/**
	 * 住戶发送权限请求到业主
	 * 
	 * @param residentId
	 * @param roomId
	 * @return
	 */
	public CommonResult register(Long residentId, Long roomId) {
		CommonResult commonResult = new CommonResult();
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		String roomAddress = roomService.getRoomAddress(roomId);
		RoomRequestAuthorization roomRequestAuthorization = new RoomRequestAuthorization();
		roomRequestAuthorization.setResidentid(residentId);
		roomRequestAuthorization.setRoomid(roomId);
		roomRequestAuthorization.setDatetime(new Date());
		roomRequestAuthorization.setState(0);
		roomRequestAuthorizationMapper.insert(roomRequestAuthorization);
		// 手机号码显示为正常
		StringBuffer message = new StringBuffer();
		message.append("手机号码为:").append(resident.getMobilephonenumber()).append("的住户,").append("请求").append(roomAddress)
				.append("房屋授权。");
		// 添加通知
		long noticeId = noticeService.addRoomRequestAuthorization(residentId, message.toString());
		// 推送结果
		pushService.pushRoomRequestAuthorization(noticeId, resident.getMobilephonenumber(), message.toString());
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("非业主发送房屋授权请求");
		return commonResult;
	}

}
