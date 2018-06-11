package com.zheman.lock.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.FaceCOSMapper;
import com.zheman.lock.dao.ProfileFaceCOSMapper;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.dao.ResidentRoomMapper;
import com.zheman.lock.dao.RoomMapper;
import com.zheman.lock.dao.UnitMapper;
import com.zheman.lock.model.FaceCOS;
import com.zheman.lock.model.ProfileFaceCOS;
import com.zheman.lock.model.Resident;
import com.zheman.lock.model.ResidentRoom;
import com.zheman.lock.model.Room;
import com.zheman.lock.model.trans.TransResident;
import com.zheman.lock.model.trans.TransResidentFace;
import com.zheman.lock.util.GenerateUtil;

@Service("profile")
public class ProfileService {

	Logger logger = LoggerFactory.getLogger(ProfileService.class);

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	TencentCOSService tencentCOSService;

	@Autowired
	RedisService redisService;

	@Autowired
	ProfileFaceCOSMapper profileFaceCOSMapper;

	@Autowired
	YoutuFaceService youtuFaceService;

	@Autowired
	ResidentRoomMapper residentRoomMapper;

	@Autowired
	UnitMapper unitMapper;

	@Autowired
	RoomMapper roomMapper;

	@Autowired
	FaceCOSMapper faceCOSMapper;

	/**
	 * 查询用户资料
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public CommonResult selectResident(String token) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		TransResident transResident = new TransResident();
		transResident.setId(resident.getId());
		transResident.setNickName(resident.getNickname());
		transResident.setDefaultRoomId(resident.getDefaultroomid());
		// 获取用户头像临时地址
		ProfileFaceCOS profileFaceCOS = profileFaceCOSMapper.selectByResidentId(resident.getId());
		if (null != profileFaceCOS) {
			String profileFaceUrl = tencentCOSService.getProfileFacePresignedUrl(residentId);
			transResident.setProfileFaceUrl(profileFaceUrl);
			transResident.setFaceDatetime(profileFaceCOS.getDatetime().getTime());
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj(transResident);
		return commonResult;
	}

	/**
	 * 修改个人资料头像
	 * 
	 * @param token
	 * @param file
	 * @return
	 * @throws Exception
	 */

	public CommonResult changeProfileFace(String token, MultipartFile file) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		// 生成key
		String prefix = GenerateUtil.getProfileFaceName(residentId);
		// 获取文件后缀
		String fileName = file.getOriginalFilename();
		int index = StringUtils.lastIndexOf(fileName, ".");
		String suffix = StringUtils.substring(fileName, index + 1);
		String key = prefix + "." + suffix;
		String etag = tencentCOSService.updateProfileFace(residentId, file, key);
		// 直接获取下载地址返回
		String url = tencentCOSService.getProfileFacePresignedUrl(key);
		// 图片鉴黄
		CommonResult imagePornCommonResult = youtuFaceService.imagePorn(url);
		if (imagePornCommonResult.getCode() == CommonResult.FAILURE) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("头像设置失败");
			tencentCOSService.deleteProfileFace(key);
			return commonResult;
		}
		// 删除之前的头像
		ProfileFaceCOS profileFaceCOS = profileFaceCOSMapper.selectByResidentId(residentId);
		if (null != profileFaceCOS)
			profileFaceCOSMapper.deleteByPrimaryKey(profileFaceCOS.getId());
		// 保存头像信息
		profileFaceCOS = new ProfileFaceCOS();
		profileFaceCOS.setDatetime(new Date());
		profileFaceCOS.setEtag(etag);
		profileFaceCOS.setResidentid(residentId);
		profileFaceCOS.setDownloadurl(key);
		profileFaceCOSMapper.insert(profileFaceCOS);
		// 更新主表的关联信息
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		resident.setProfilefaceid(profileFaceCOS.getId());
		residentMapper.updateByPrimaryKeySelective(resident);

		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("修改完成");
		commonResult.setObj(url);
		return commonResult;
	}

	/**
	 * 修改昵称
	 * 
	 * @param token
	 * @param nickname
	 * @return
	 * @throws Exception
	 */

	public CommonResult changeProfileNickname(String token, String nickname) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		resident.setNickname(nickname);
		residentMapper.updateByPrimaryKeySelective(resident);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("昵称修改完成");
		return commonResult;
	}

	/**
	 * 添加人脸识别信息
	 * 
	 * 判断当前住户是否大于5张人脸信息 上传人脸信息图片到腾讯对象存储并且 返回key和etag 获取当前人脸信息的可下载地址
	 * 判断住户是否已经在优图创建个体，未创建：unitid作为groupid，residentId作为personid创建个体。
	 * 创建优图face信息，根据返回值added判断是否重复上传，重复上传：删除对应的cos文件并
	 * 返回消息。未重复上传：把key和etag保存到数据库。
	 * 
	 * @param token
	 * @param file
	 * @return
	 */
	public CommonResult addFace(String token, MultipartFile file) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		// 每人 最多可上传五个face
		List<FaceCOS> faceCOSs = faceCOSMapper.selectByResidentId(residentId);
		if (faceCOSs.size() >= 20) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("最多录入二十张脸部信息");
			return commonResult;
		}
		// 上传人脸信息到cos
		Map<String, String> result = tencentCOSService.uploadFace(residentId, file);
		String key = result.get("key");
		String etag = result.get("etag");
		// 获取当前人脸信息可下载地址
		String url = tencentCOSService.getFacePresignedUrl(key);
		// 人脸比对 新上传的与之前的是否为同一人
		if (faceCOSs.size() > 0) {
			FaceCOS faceCOS = faceCOSs.get(0);
			String preUrl = tencentCOSService.getFacePresignedUrl(faceCOS.getKeyname());
			commonResult = youtuFaceService.faceCompareUrl(preUrl, url);
			// 与之前上传的人脸不是同一人
			if (commonResult.getCode() == CommonResult.FAILURE) {
				// 删除掉之前上传的face
				tencentCOSService.deleteFace(key);
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("暂时只支持同一人脸信息");
				return commonResult;
			}
		}
		// 判断住户是否已经在腾讯优图创建个体
		boolean bool = youtuFaceService.existYoutuPerson(residentId);
		// 先在优图创建个体
		if (!bool) {
			// 获取用户所有的房屋列表
			List<ResidentRoom> residentRooms = residentRoomMapper.selectByResidentId(residentId);
			if (residentRooms.size() <= 0) {
				commonResult.setCode(CommonResult.SUCCESS);
				commonResult.setMessage("请绑定房屋后,再上传人脸信息");
				return commonResult;
			}
			List<String> groups = new ArrayList<>();
			Room room = null;
			for (ResidentRoom residentRoom : residentRooms) {
				room = roomMapper.selectByPrimaryKey(residentRoom.getRoomid());
				groups.add(String.valueOf(room.getUnitid()));
			}
			// 创建新的个体
			CommonResult newPersonResult = youtuFaceService.newPerson(url, residentId, groups);
			if (newPersonResult.getCode() != 0) {
				if (newPersonResult.getCode() == -1100) {
					commonResult.setCode(CommonResult.FAILURE);
					commonResult.setMessage("相似度错误");
					return commonResult;
				} else if (newPersonResult.getCode() == -1101) {
					commonResult.setCode(CommonResult.FAILURE);
					commonResult.setMessage("人脸信息检测失败");
					return commonResult;
				} else {
					commonResult.setCode(CommonResult.FAILURE);
					commonResult.setMessage("优图创建Person失败");
					return commonResult;
				}
			}
		}
		// 创建优图的face
		CommonResult newFaceCommonResult = youtuFaceService.newFace(residentId, url);
		if (newFaceCommonResult.getCode() != 0) {
			if (newFaceCommonResult.getCode() == -1100) {
				newFaceCommonResult.setCode(CommonResult.FAILURE);
				newFaceCommonResult.setMessage("相似度错误");
				return newFaceCommonResult;
			} else if (newFaceCommonResult.getCode() == -1101) {
				newFaceCommonResult.setCode(CommonResult.FAILURE);
				newFaceCommonResult.setMessage("人脸信息检测失败");
				return newFaceCommonResult;
			} else if (newFaceCommonResult.getCode() == -1312) {
				newFaceCommonResult.setCode(CommonResult.FAILURE);
				newFaceCommonResult.setMessage("添加了与之前几乎相同的人脸");
				return newFaceCommonResult;
			} else {
				newFaceCommonResult.setCode(CommonResult.FAILURE);
				newFaceCommonResult.setMessage("优图创建Face失败");
				return newFaceCommonResult;
			}
		}
		// 保存到数据库
		FaceCOS faceCOS = new FaceCOS();
		faceCOS.setDatetime(new Date());
		faceCOS.setEtag(etag);
		faceCOS.setKeyname(key);
		faceCOS.setResidentid(residentId);
		faceCOS.setYoutuid(newFaceCommonResult.getObj().toString());
		faceCOSMapper.insert(faceCOS);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("添加人脸识别信息完成");
		return commonResult;
	}


	/**
	 * 查询所有人脸信息
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public CommonResult selectFaces(String token) throws Exception {
		CommonResult commonResult = new CommonResult();
		List<TransResidentFace> transResidentFaces = new ArrayList<>();
		TransResidentFace transResidentFace = null;
		long residentId = redisService.getResidentIdByLoginToken(token);
		List<FaceCOS> faceCOSs = faceCOSMapper.selectByResidentId(residentId);
		String url = null;
		for (FaceCOS faceCOS : faceCOSs) {
			transResidentFace = new TransResidentFace();
			transResidentFace.setId(faceCOS.getId());
			url = tencentCOSService.getFacePresignedUrl(faceCOS.getKeyname());
			transResidentFace.setUrl(url);
			transResidentFaces.add(transResidentFace);
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("查询完成");
		commonResult.setObj(transResidentFaces);
		return commonResult;
	}

	/**
	 * 删除人脸识别信息
	 * 
	 * @param token
	 * @param faceId
	 * @return
	 * @throws Exception
	 */
	public CommonResult deleteFaces(String token, List<String> faceIds) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		List<String> youtuIds = new ArrayList<>();
		// 删除cos存储
		for (String faceId : faceIds) {
			FaceCOS faceCOS = faceCOSMapper.selectByPrimaryKey(Long.valueOf(faceId));
			tencentCOSService.deleteFace(faceCOS.getKeyname());
			// 删除db数据
			faceCOSMapper.deleteByPrimaryKey(Long.valueOf(faceId));
			youtuIds.add(faceCOS.getYoutuid());
		}
		// 删除youtu
		boolean bool = youtuFaceService.deleteFace(residentId, youtuIds);
		if (!bool) {
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("刪除youtuface失败");
			return commonResult;
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("删除人脸信息完成");
		return commonResult;
	}

	/**
	 * 删除优图人脸识别信息
	 * 
	 * @param token
	 * @param faceId
	 * @return
	 * @throws Exception
	 */
	public CommonResult deleteYoutuFaces(String token, List<String> faceIds) throws Exception {
		long residentId = redisService.getResidentIdByLoginToken(token);
		youtuFaceService.deleteFace(residentId, faceIds);
		return new CommonResult();
	}

	/**
	 * 设置默认开锁房屋
	 * 
	 * @param token
	 * @param roomId
	 * @return
	 * @throws Exception
	 */
	public CommonResult defaultRoom(String token, long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		long residentId = redisService.getResidentIdByLoginToken(token);
		Resident resident = residentMapper.selectByPrimaryKey(residentId);
		resident.setDefaultroomid(roomId);
		residentMapper.updateByPrimaryKeySelective(resident);
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("修改完成");
		return commonResult;
	}

	public JSONObject selectYoutuFaces(String token) throws Exception {
		long residentId = redisService.getResidentIdByLoginToken(token);
		return youtuFaceService.getInfo(String.valueOf(residentId));
	}
}
