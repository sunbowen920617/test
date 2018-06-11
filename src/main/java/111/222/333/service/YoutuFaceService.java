package com.zheman.lock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.FaceCOSMapper;
import com.zheman.lock.dao.ResidentRoomMapper;
import com.zheman.lock.model.FaceCOS;
import com.zheman.lock.youtu.Youtu;

@Service
public class YoutuFaceService {

	Logger logger = LoggerFactory.getLogger(YoutuFaceService.class);

	@Autowired
	Youtu youtu;

	@Autowired
	ResidentService residentService;

	@Autowired
	ResidentRoomMapper residentRoomMapper;

	@Autowired
	FaceCOSMapper faceCOSMapper;

	@Autowired
	TencentCOSService tencentCOSService;

	@Value("${tencent.youtu.appId}")
	String m_appid;

	@Value("${tencent.youtu.userId}")
	String m_user_id;

	@Value("${zheman.static.address.face}")
	String staticFacePath;

	/**
	 * 创建个体
	 * 
	 * @param url
	 *            face地址
	 * @param id
	 *            住戶ID
	 * @param groupIds
	 *            单元ID
	 * @throws Exception
	 */
	public CommonResult newPerson(String url, long residentId, List<String> groupIds) throws Exception {
		CommonResult commonResult = new CommonResult();
		JSONObject jsonObject = youtu.NewPerson(url, String.valueOf(residentId), groupIds);
		Object errorCode = jsonObject.get("errorcode");
		int errorCodeI = Integer.valueOf(errorCode.toString());
		Object errormsg = jsonObject.get("errormsg");
		commonResult.setCode(errorCodeI);
		commonResult.setMessage(errormsg.toString());
		logger.info("youtu newPerson has an error code:" + jsonObject.toString());
		return commonResult;
	}

	/**
	 * 删除个体
	 * 
	 * @param residentId
	 *            住户ID
	 * @return
	 * @throws Exception
	 */
	public CommonResult deletePerson(long residentId) throws Exception {
		CommonResult commonResult = new CommonResult();
		JSONObject jsonObject = youtu.DelPerson(String.valueOf(residentId));
		Object errorCode = jsonObject.get("errorcode");
		int errorCodeI = Integer.valueOf(errorCode.toString());
		Object errormsg = jsonObject.get("errormsg");
		commonResult.setCode(errorCodeI);
		commonResult.setMessage(errormsg.toString());
		logger.info("youtu deletePerson has an error code:" + jsonObject.toString());
		return commonResult;
	}

	/**
	 * 添加脸部信息
	 * 
	 * @param residetnId
	 *            住户ID
	 * @param url
	 *            人脸
	 * @return
	 * @throws Exception
	 */
	public CommonResult newFace(long residetnId, String url) throws Exception {
		CommonResult commonResult = new CommonResult();
		List<String> urls = new ArrayList<>();
		urls.add(url);
		JSONObject jsonObject = youtu.AddFaceUrl(String.valueOf(residetnId), urls);
		int errorCode = jsonObject.getInt("errorcode");
		String errorMsg = jsonObject.getString("errormsg");
		JSONArray retJsonArray = jsonObject.getJSONArray("ret_codes");
		int retCode = retJsonArray.getInt(0);
		int addCount = jsonObject.getInt("added");
		JSONArray jsonArray = jsonObject.getJSONArray("face_ids");
		commonResult.setCode(retCode);
		commonResult.setMessage(errorMsg);
		commonResult.setObj(jsonArray.get(0));
		logger.info("youtu newFace finish : " + jsonObject.toString());
		return commonResult;
	}

	/**
	 * 删除优图Face
	 * 
	 * @param residentId
	 * @param faceIds
	 * @return
	 */
	public boolean deleteFace(long residentId, List<String> faceIds) throws Exception {
		JSONObject jsonObject = youtu.DelFace(String.valueOf(residentId), faceIds);
		int errorCode = jsonObject.getInt("errorcode");
		String errorMessage = jsonObject.getString("errormsg");
		logger.info("deleteFace finish :" + jsonObject.toString());
		if (errorCode == 0)
			return true;
		return false;
	}

	/**
	 * 判断用户是否存在优图
	 * 
	 * @param personId
	 *            住户ID
	 * @return true ： 存在。false ： 不存在
	 * @throws Exception
	 */
	public boolean existYoutuPerson(long personId) throws Exception {
		JSONObject jsonObject = youtu.GetInfo(String.valueOf(personId));
		Object errorCode = jsonObject.get("errorcode");
		logger.info("existYoutuPerson finish :" + jsonObject.toString());
		int errorCodeI = Integer.valueOf(errorCode.toString());
		if (errorCodeI == -1303)
			return false;
		return true;
	}

	/**
	 * 查询个体信息
	 * 
	 * @param personId
	 * @return
	 */
	public JSONObject getInfo(String personId) throws Exception {
		JSONObject jsonObject = youtu.GetInfo(personId);
		return jsonObject;
	}

	/**
	 * 重置组信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public CommonResult resetPerson(Long residentId) throws Exception {
		CommonResult commonResult = new CommonResult();
		// 删除个体信息
		commonResult = deletePerson(residentId);
		if (commonResult.getCode() != 0) {
			return commonResult;
		}
		// 获取所有单元信息
		Set<Long> units = residentService.getUnitsByResident(residentId);
		List<String> groupIds = new ArrayList<>();
		for (long unitId : units) {
			groupIds.add(String.valueOf(unitId));
		}
		// 获取所有头像信息
		List<FaceCOS> faceCOSs = faceCOSMapper.selectByResidentId(residentId);
		// 创建个体
		for (FaceCOS faceCOS : faceCOSs) {
			boolean bool = existYoutuPerson(residentId);
			String url = tencentCOSService.getFacePresignedUrl(faceCOS.getKeyname());
			if (!bool) {
				newPerson(url, residentId, groupIds);
			} else {
				newFace(residentId, url);
			}
		}
		commonResult.setCode(CommonResult.SUCCESS);
		return commonResult;
	}

	/**
	 * 图片鉴黄
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public CommonResult imagePorn(String url) throws Exception {
		CommonResult commonResult = new CommonResult();
		JSONObject jsonObject = youtu.ImagePornUrl(url);
		logger.info(jsonObject.toString());
		int errorCode = jsonObject.getInt("errorcode");
		String errorMsg = jsonObject.getString("errormsg");
		//
		if (errorCode == 0 && StringUtils.equals("OK", errorMsg)) {
			JSONArray tags = jsonObject.getJSONArray("tags");
			// 循环出所有数据
			for (int i = 0; i < tags.length(); i++) {
				JSONObject tag = tags.getJSONObject(i);
				String tagName = tag.getString("tag_name");
				int confidence = tag.getInt("tag_confidence");
				if (StringUtils.equals(tagName, "porn")) {
					if (confidence > 80) {
						commonResult.setCode(CommonResult.FAILURE);
						commonResult.setMessage("表示黄色图像");
						return commonResult;
					}
				} else if (StringUtils.equals(tagName, "porn-level")) {
					if (confidence > 80) {
						commonResult.setCode(CommonResult.FAILURE);
						commonResult.setMessage("表示色情级别");
						return commonResult;
					}
				} else if (StringUtils.equals(tagName, "normal_hot_porn")) {
					if (confidence > 80) {
						commonResult.setCode(CommonResult.FAILURE);
						commonResult.setMessage("图像为色情的综合值");
						return commonResult;
					}
				} else {
					commonResult.setCode(CommonResult.SUCCESS);
					commonResult.setMessage("正常");
				}
			}
		}

		return commonResult;
	}

	/**
	 * url人脸比对 值大于90
	 * 
	 * @param urla
	 *            人脸a
	 * @param urlb
	 *            人脸b
	 * @return
	 */
	public CommonResult faceCompareUrl(String urla, String urlb) throws Exception {
		CommonResult commonResult = new CommonResult();
		JSONObject jsonObject = youtu.FaceCompareUrl(urla, urlb);
		logger.info("faceCompare:" + jsonObject.toString());
		int code = jsonObject.getInt("errorcode");
		double similarity = jsonObject.getDouble("similarity");
		String errmsg = jsonObject.getString("errormsg");
		if (code == 0) {
			if (similarity > 90) {
				commonResult.setCode(CommonResult.SUCCESS);
				commonResult.setMessage(errmsg);
				commonResult.setObj(similarity);
				return commonResult;
			}
		}
		commonResult.setCode(CommonResult.FAILURE);
		commonResult.setMessage("不是同一人");
		return commonResult;
	}
}
