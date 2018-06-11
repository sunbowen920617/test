package com.zheman.lock.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.FaceCOSMapper;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.dao.ResidentRoomBackupMapper;
import com.zheman.lock.dao.ResidentRoomMapper;
import com.zheman.lock.dao.RoomMapper;
import com.zheman.lock.dao.ShareMapper;
import com.zheman.lock.model.Notice;
import com.zheman.lock.model.Resident;
import com.zheman.lock.model.ResidentRoom;
import com.zheman.lock.model.Share;
import com.zheman.lock.model.enumtion.ShareStateEnum;
import com.zheman.lock.model.trans.TransRoomShare;

@Service
public class ShareService {

	@Autowired
	ShareMapper shareMapper;

	@Autowired
	RedisService redisService;

	@Autowired
	ResidentRoomMapper residentRoomMapper;

	@Autowired
	ResidentRoomBackupMapper residentRoomBackupMapper;

	@Autowired
	NoticeService noticeService;

	@Autowired
	PushService pushService;

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	RoomMapper roomMapper;

	@Autowired
	FaceCOSMapper faceCOSMapper;

	@Autowired
	TencentCOSService tencentCOSService;

	@Autowired
	YoutuFaceService youtuFaceService;

	/**
	 * 查询房屋列表 如果是leader可以查看到所有共享信息 如果不是只能查看到自己的共享信息
	 * 
	 * @param roomId
	 *            房屋ID
	 * @return
	 * @throws Exception
	 */
	public CommonResult selectRoomShare(String token, long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		List<TransRoomShare> transRoomShares = new ArrayList<>();
		long residentId = redisService.getResidentIdByLoginToken(token);
		Map<String, String> params = new HashMap<>();
		params.put("roomId", String.valueOf(roomId));
		params.put("residentId", String.valueOf(residentId));
		List<ResidentRoom> residentRoomList = residentRoomMapper.selectByRoomIdAndResidentId2(residentId, roomId);
		if (null == residentRoomList || residentRoomList.size() == 0) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("住户未与此房屋绑定");
			return commonResult;
		}
		ResidentRoom residentRoom = residentRoomList.get(0);
		// 如果是leader，可查询所有住户的绑定信息
		if (residentRoom.getLeader() == 1) {
			TransRoomShare transRoomShare = null;
			// 根据房屋ID查询到所有房屋的信息
			List<ResidentRoom> residentRooms = residentRoomMapper.selectByRoomId(roomId);
			// 设置查询参数
			Resident resident = null;
			for (ResidentRoom residentRoom2 : residentRooms) {
				// 查询住户房屋关联信息
				if (residentRoom2.getResidentid() == residentId)
					continue;
				transRoomShare = new TransRoomShare();
				transRoomShare.setDateTime(DateUtils.formatDate(residentRoom2.getDatetime(), "yyyy-MM-dd"));
				resident = residentMapper.selectByPrimaryKey(residentRoom2.getResidentid());
				transRoomShare.setMobilePhone(resident.getMobilephonenumber());
				transRoomShare.setShareAuth(residentRoom2.getSharedauth());
				transRoomShare.setResidentId(residentRoom2.getResidentid());
				transRoomShare.setRoomId(residentRoom2.getRoomid());
				transRoomShares.add(transRoomShare);
			}
			// 如果不是leader，查询自己所共享的住户
		} else {
			params = new HashMap<>();
			params.put("invitedId", String.valueOf(residentId));
			params.put("roomId", String.valueOf(roomId));
			List<ResidentRoom> residentRooms = residentRoomMapper.selectByInvitedIdAndRoomId(params);
			Resident resident=null;
			for (ResidentRoom residentRoom2 : residentRooms) {
				if (residentRoom2.getResidentid() == residentId)
					continue;
				TransRoomShare transRoomShare = new TransRoomShare();
				transRoomShare.setDateTime(DateUtils.formatDate(residentRoom2.getDatetime(), "yyyy-MM-dd"));
				resident = residentMapper.selectByPrimaryKey(residentRoom2.getResidentid());
				transRoomShare.setMobilePhone(resident.getMobilephonenumber());
				transRoomShare.setShareAuth(residentRoom2.getSharedauth());
				transRoomShare.setResidentId(residentRoom2.getResidentid());
				transRoomShare.setRoomId(residentRoom2.getRoomid());
				transRoomShares.add(transRoomShare);
			}
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询成功");
		if (null == transRoomShares)
			commonResult.setObj("");
		else
			commonResult.setObj(transRoomShares);
		return commonResult;
	}

	/**
	 * 开启房屋共享权限
	 * 
	 * @param roomId
	 *            房屋
	 * @param residentId
	 *            住户
	 * @return
	 */
	public CommonResult enableShare(long roomId, long residentId) {
		CommonResult commonResult = new CommonResult();
		Map<String, String> params = new HashMap<>();
		params.put("roomId", String.valueOf(roomId));
		params.put("residentId", String.valueOf(residentId));
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		residentRoom.setSharedauth(1);
		residentRoomMapper.updateByPrimaryKeySelective(residentRoom);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("共享权限已开启");
		return commonResult;
	}

	/**
	 * 禁用房屋共享权限
	 * 
	 * @param roomId
	 *            房屋
	 * @param residentId
	 *            住户
	 * @return
	 */
	public CommonResult disableShare(long roomId, long residentId) {
		CommonResult commonResult = new CommonResult();
		Map<String, String> params = new HashMap<>();
		params.put("roomId", String.valueOf(roomId));
		params.put("residentId", String.valueOf(residentId));
		ResidentRoom residentRoom = residentRoomMapper.selectByRoomIdAndResidentId(params);
		residentRoom.setSharedauth(0);
		residentRoomMapper.updateByPrimaryKeySelective(residentRoom);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("共享权限已禁用");
		return commonResult;
	}

	/**
	 * 账户共享权限反馈
	 * 
	 * @param token
	 * @param residentId
	 *            邀请人
	 * @param roomId
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public CommonResult shareByAccountFeedbackResetPerson(String token, long shareId, int result) throws Exception {
		CommonResult commonResult = new CommonResult();
		long acceptResidentId = redisService.getResidentIdByLoginToken(token);
		String acceptMobilePhone = redisService.getMobilePhoneByLoginToken(token);
		// 查询到通知
		Notice notice = noticeService.selectEntityById(shareId);
		// 根据账户ID查询账户共享信息
		Share share = shareMapper.selectByPrimaryKey(notice.getShareid());
		if (null == share) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("未找到匹配的共享信息");
			return commonResult;
		}
		// 如果已被处理过
		if (share.getAccepted() != ShareStateEnum.NIL.ordinal()) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("房屋邀请已过期");
			return commonResult;
		}
		// 查询邀请人的信息
		Resident inviterResident = residentMapper.selectByPrimaryKey(share.getShareresidentid());
		// 拒绝
		if (result == ShareStateEnum.REJECT.ordinal()) {
			// 保存拒绝通知给邀请人
			StringBuffer message = new StringBuffer();
			message.append("手机号码为").append(acceptMobilePhone).append("的住户，拒绝了你的房屋共享邀请。");
			long noticeId = noticeService.addRejectShareRoomNotice(inviterResident.getId(), message.toString());
			// 推送拒绝反馈消息给邀请人
			pushService.pushRejectShareRoomMessage(noticeId, inviterResident.getMobilephonenumber(),
					message.toString());
		} else {
			// 保存接受通知给邀请人
			StringBuffer message = new StringBuffer();
			message.append("手机号码为").append(acceptMobilePhone).append("的住户，已接受你的房屋共享邀请。");
			long noticeId = noticeService.addAcceptShareRoomNotice(inviterResident.getId(), message.toString());
			// 推送接受通知给邀请人
			pushService.pushAcceptShareRoomMessage(noticeId, inviterResident.getMobilephonenumber(),
					message.toString());
			// 保存住户与房屋关联
			ResidentRoom residentRoom = new ResidentRoom();
			residentRoom.setInviterid(inviterResident.getId());
			residentRoom.setLeader(0);
			residentRoom.setResidentid(acceptResidentId);
			residentRoom.setRoomid(share.getRoomid());
			residentRoom.setSharedauth(0);
			residentRoom.setDatetime(new Date());
			residentRoomMapper.insert(residentRoom);
			// 更新人脸到新的组内
			youtuFaceService.resetPerson(share.getAcceptresidentid());
		}
		share.setAcceptresidentid(acceptResidentId);
		share.setAccepted(result);
		shareMapper.updateByPrimaryKeySelective(share);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("处理完成");
		commonResult.setObj(share.getAcceptresidentid());
		return commonResult;
	}

}
