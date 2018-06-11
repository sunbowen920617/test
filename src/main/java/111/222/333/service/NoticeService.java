package com.zheman.lock.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.NoticeMapper;
import com.zheman.lock.dao.ProfileFaceCOSMapper;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.dao.ShareMapper;
import com.zheman.lock.model.Notice;
import com.zheman.lock.model.ProfileFaceCOS;
import com.zheman.lock.model.Share;
import com.zheman.lock.model.enumtion.NoticeTypeEnum;
import com.zheman.lock.model.trans.TransNotice;

@Service
public class NoticeService {

	private static final Logger log = LoggerFactory.getLogger(NoticeService.class);

	@Autowired
	NoticeMapper noticeMapper;

	@Autowired
	RedisService redisService;

	@Autowired
	ShareMapper shareMapper;

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	TencentCOSService tencentCOSService;

	@Autowired
	ProfileFaceCOSMapper profileFaceCOSMapper;

	/**
	 * 根据登录token查询通知列表
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public CommonResult selectNoticesByToken(String token, int page, int size) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		// 查询通知列表
		int index = (page - 1) * size;
		// 根据residentId查询通知 按时间降序排列
		List<Notice> notices = noticeMapper.selectByResidentId(residentId, index, size);
		// 转换 为传输对象
		List<TransNotice> transNotices = convertToTransNotices(notices);
		commonResult.setObj(transNotices);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询完成");
		return commonResult;
	}

	public List<TransNotice> convertToTransNotices(List<Notice> notices) {
		List<TransNotice> transNotices = new ArrayList<>();
		for (Notice notice : notices) {
			transNotices.add(convertToTransNotice(notice));
		}
		return transNotices;
	}

	public TransNotice convertToTransNotice(Notice notice) {
		TransNotice transNotice = new TransNotice();
		transNotice.setDatetime(DateUtils.formatDate(notice.getDatetime(), "yyyy-MM-dd HH:mm:ss"));
		transNotice.setMessage(notice.getMessage());
		transNotice.setTitle(notice.getTitle());
		transNotice.setType(notice.getNoticetype());
		transNotice.setId(notice.getId());
		transNotice.setReaded(notice.getReaded() == 1 ? true : false);
		transNotice.setUrl(notice.getUrl());
		Share share = shareMapper.selectByPrimaryKey(notice.getShareid());
		if (null == share) {
			transNotice.setFaceUrl("");
			transNotice.setFaceDatetime(0);
			transNotice.setResidentId(0);
		} else {
			ProfileFaceCOS profileFaceCOS = profileFaceCOSMapper.selectByResidentId(share.getShareresidentid());
			if (null == profileFaceCOS) {
				transNotice.setFaceDatetime(0);
				transNotice.setFaceUrl("");
				transNotice.setResidentId(0);
			} else {
				String url = tencentCOSService.getProfileFacePresignedUrl(profileFaceCOS.getDownloadurl());
				transNotice.setFaceUrl(url);
				transNotice.setFaceDatetime(profileFaceCOS.getDatetime().getTime());
				transNotice.setResidentId(profileFaceCOS.getResidentid());
			}
			transNotice.setShareResult(share.getAccepted());
		}
		return transNotice;
	}

	/**
	 * 根据ID查询通知
	 * 
	 * @param token
	 * @param noticeId
	 * @return
	 */
	public CommonResult selectNoticeById(long noticeId) throws Exception {
		CommonResult commonResult = new CommonResult();
		Notice notice = noticeMapper.selectByPrimaryKey(noticeId);
		if (null == notice) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("未找到对应的通知");
			return commonResult;
		}
		TransNotice transNotice = new TransNotice();
		transNotice.setDatetime(DateUtils.formatDate(notice.getDatetime(), "yyyy-MM-dd"));
		transNotice.setId(notice.getId());
		transNotice.setMessage(notice.getMessage());
		if (notice.getReaded() == 1)
			transNotice.setReaded(true);
		else
			transNotice.setReaded(false);
		transNotice.setTitle(notice.getTitle());
		transNotice.setType(notice.getNoticetype());
		transNotice.setUrl(notice.getUrl());
		if (notice.getNoticetype() == NoticeTypeEnum.ROOMSHAREMESSAGE.ordinal()) {
			Share share = shareMapper.selectByPrimaryKey(notice.getShareid());
			if (null != share) {
				if (null == share.getAccepted()) {
					transNotice.setShareResult(0);
				} else {
					transNotice.setShareResult(share.getAccepted());
				}
			} else
				log.info("selectNoticeById has an error, shareid is null");
		}
		notice.setReaded(1);
		noticeMapper.updateByPrimaryKey(notice);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询完成");
		commonResult.setObj(transNotice);
		return commonResult;
	}

	/**
	 * 添加房屋共享通知
	 * 
	 * @param roomAddress
	 *            房屋地址
	 * @param mobilePhone
	 *            邀请人的手机号码
	 * @param residentId
	 * @return
	 */
	public long addShareRoomNotice(String roomAddress, String mobilePhone, long residentId, long shareId) {
		StringBuffer title = new StringBuffer();
		title.append("有住户邀请你共享房屋权限");
		StringBuffer message = new StringBuffer();
		message.append("手机号码为:").append(mobilePhone).append("的住户,邀请你共享").append(roomAddress).append("的权限");
		return addNotice(message.toString(), title.toString(), NoticeTypeEnum.ROOMSHAREMESSAGE.ordinal(), residentId,
				shareId);
	}

	/**
	 * 添加拒绝房屋共享通知
	 * 
	 * @param title
	 *            标题
	 * @param residentId
	 *            接收通知的住户ID
	 * @param mobilePhone
	 *            拒绝房屋共享住户的手机号码
	 * @return
	 */
	public long addRejectShareRoomNotice(long residentId, String message) {
		StringBuffer title = new StringBuffer();
		title.append("住户已拒绝你的房屋权限共享邀请");
		return addNotice(message, title.toString(), NoticeTypeEnum.TEXTMESSAGE.ordinal(), residentId);
	}

	/**
	 * 添加接受房屋共享通知
	 *
	 * @param residentId
	 *            接收通知的住户ID
	 * @param mobilePhone
	 *            接受房屋共享住户的手机号码
	 * @return
	 */
	public long addAcceptShareRoomNotice(long residentId, String message) {
		StringBuffer title = new StringBuffer();
		title.append("住户已接受你的房屋权限共享邀请");
		return addNotice(message.toString(), title.toString(), NoticeTypeEnum.TEXTMESSAGE.ordinal(), residentId);
	}

	/**
	 * 添加房屋权限申请
	 *
	 * @param residentId
	 *            接收通知的住户ID
	 * @param mobilePhone
	 *            接受房屋共享住户的手机号码
	 * @return
	 */
	public long addRoomRequestAuthorization(long residentId, String message) {
		StringBuffer title = new StringBuffer();
		title.append("新住户请求共享房屋权限");
		return addNotice(message.toString(), title.toString(), NoticeTypeEnum.ROOMREQUESTAUTHORIZATIONMESSAGE.ordinal(),
				residentId);
	}

	public long addNotice(String message, String title, int type, long residentId) {
		Notice notice = new Notice();
		notice.setMessage(message);
		notice.setNoticetype(type);
		notice.setReaded(0);
		notice.setResidentid(residentId);
		notice.setDatetime(new Date());
		notice.setTitle(title);
		noticeMapper.insert(notice);
		return notice.getId();
	}

	public long addNotice(String message, String title, int type, long residentId, long shareId) {
		Notice notice = new Notice();
		notice.setMessage(message);
		notice.setNoticetype(type);
		notice.setReaded(0);
		notice.setResidentid(residentId);
		notice.setDatetime(new Date());
		notice.setTitle(title);
		notice.setShareid(shareId);
		noticeMapper.insert(notice);
		return notice.getId();
	}

	public Notice selectEntityById(long id) {
		return noticeMapper.selectByPrimaryKey(id);
	}

}
