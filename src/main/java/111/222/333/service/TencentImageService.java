package com.zheman.lock.service;

import java.io.File;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qcloud.image.ImageClient;
import com.qcloud.image.request.FaceIdCardLiveDetectFourRequest;
import com.qcloud.image.request.FaceLiveGetFourRequest;
import com.zheman.lock.common.CommonResult;

@Service
public class TencentImageService {

	private static final Logger log = LoggerFactory.getLogger(TencentImageService.class);

	@Autowired
	ImageClient imageClient;

	@Autowired
	TencentCOSService tencentCOSService;

	@Value("${tencent.cos.idcardLiveDetectFourName}")
	String bucketName;

	/**
	 * 唇语活体检测
	 * 
	 * @param validate
	 * @param vedio
	 * @param idcardNumber
	 * @param idcardName
	 * @return
	 * @throws Exception
	 */
	public CommonResult idcardLiveDetectFour(String validate, File vedio, String idcardNumber, String idcardName)
			throws Exception {
		CommonResult commonResult = new CommonResult();
		FaceIdCardLiveDetectFourRequest faceIdCardLiveDetectFourRequest = new FaceIdCardLiveDetectFourRequest(
				bucketName, validate, vedio, idcardNumber, idcardName, null);
		String result = imageClient.faceIdCardLiveDetectFour(faceIdCardLiveDetectFourRequest);
		log.info("idcardLiveDetectFour:" + result);
		JSONObject jsonObject = new JSONObject(result);
		int code = jsonObject.getInt("code");
		String message = jsonObject.getString("message");
		if (code == 0) {
			JSONObject data = jsonObject.getJSONObject("data");
			int compareStatus = data.getInt("compare_status");
			String compareMsg = data.getString("compare_msg");

			int sim = data.getInt("sim");
			String videoPhoto = data.getString("video_photo");
			int liveStatus = data.getInt("live_status");
			String liveMsg = data.getString("live_msg");

			if (liveStatus == 0 && sim > 90) {
				commonResult.setCode(CommonResult.SUCCESS);
				commonResult.setMessage("唇语活体检测成功");
				commonResult.setObj(videoPhoto);
				return commonResult;
			}
		}else{
			commonResult.setCode(code);
			commonResult.setMessage(message);
		}
		return commonResult;
	}

	/**
	 * 获取唇语验证信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String liveGetFour() throws Exception {
		FaceLiveGetFourRequest faceLiveGetFourRequest = new FaceLiveGetFourRequest(bucketName, "");
		String result = imageClient.faceLiveGetFour(faceLiveGetFourRequest);
		log.info("liveGetFour :" + result);
		JSONObject jsonObject = new JSONObject(result);
		JSONObject data = jsonObject.getJSONObject("data");
		String validateData = data.getString("validate_data");
		return validateData;
	}

}
