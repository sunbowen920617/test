package com.zheman.lock.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.ProfileService;
import com.zheman.lock.service.ResidentService;
import com.zheman.lock.service.YoutuFaceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("个人资料API")
@RequestMapping("/profile")
public class ProfileController {

	Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	ProfileService profileService;

	@Autowired
	ResidentService residentService;

	@Autowired
	YoutuFaceService youtuFaceService;

	@GetMapping("/upload")
	public String toUpload() {
		return "upload";
	}

	/**
	 * 查询住户资料
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/selectresident")
	@ResponseBody
	@ApiOperation(value = "根据登录token查询用户资料")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectProfile(String token) {
		CommonResult commonResult = new CommonResult();
		try {
			return profileService.selectResident(token);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectProfile has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询 失败");
		}
		return commonResult;
	}

	/**
	 * 修改个人资料的头像信息
	 * 
	 * @param token
	 * @param file
	 * @return
	 */
	@PostMapping("/changeprofileface")
	@ResponseBody
	@ApiOperation(value = "修改头像")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "file", dataType = "File", required = true, value = "上传的文件", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult changeProfileFacePath(@RequestParam("file") MultipartFile file, String token) {
		CommonResult commonResult = new CommonResult();
		try {
			if (file.getSize() > 2097152) {
				commonResult.setCode(CommonResult.PROFILE_FACE_TOOLONG);
				commonResult.setMessage("上传图片太大");
				return commonResult;
			}
			return profileService.changeProfileFace(token, file);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("changeProfileFacePath has an error", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("头像修改失败");
		}
		return commonResult;
	}

	/**
	 * 修改昵称
	 * 
	 * @param token
	 * @param file
	 * @return
	 */
	@PostMapping("/changeprofilenickname")
	@ResponseBody
	@ApiOperation(value = "修改昵称")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "nickname", dataType = "String", required = true, value = "昵称", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult changeProfileNickname(String token, String nickname) {
		CommonResult commonResult = new CommonResult();
		try {
			return profileService.changeProfileNickname(token, nickname);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("changeProfileNickname has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("修改昵称失败");
		}
		return commonResult;
	}

	/**
	 * 设置默认开锁房屋
	 * 
	 * @param token
	 * @param roomId
	 * @return
	 */
	@PostMapping("/defaultroom")
	@ResponseBody
	@ApiOperation(value = "设置默认开锁房屋")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult defaultRoom(String token, long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			return profileService.defaultRoom(token, roomId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("defaultRoom has an  error:", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("默认房屋设置失败");
		}
		return commonResult;
	}

	/**
	 * 添加人脸 信息
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/addface")
	@ResponseBody
	@ApiOperation(value = "添加 人脸信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "file", dataType = "File", required = true, value = "人脸图片", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult addFace(String token, MultipartFile file) {
		CommonResult commonResult = new CommonResult();
		try {
			if (file.getSize() >= 4194304) {
				commonResult.setCode(CommonResult.FACE_TOOLONG);
				commonResult.setMessage("图片过大,请限制在4M以内");
				return commonResult;
			}
			return profileService.addFace(token, file);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("profile addFace has an error ", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("添加人脸信息失败");
		}
		return commonResult;
	}

	/**
	 * 查询住户所有脸部信息
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/selectfaces")
	@ResponseBody
	@ApiOperation(value = "查询住户所有脸部信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectFaces(String token) {
		CommonResult commonResult = new CommonResult();
		try {
			return profileService.selectFaces(token);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectFaces has an error ,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询脸部信息失败");
		}
		return commonResult;
	}

	/**
	 * 删除脸部信息
	 * 
	 * @param token
	 * @param faceIds
	 * @return
	 */
	@PostMapping("/deletefaces")
	@ResponseBody
	@ApiOperation(value = "删除脸部信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "faceIds", dataType = "String", required = true, value = "脸部信息ids(id,id,id)", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult deleteFaces(String token, String faceIds) {
		CommonResult commonResult = new CommonResult();
		try {
			String[] faceIdArray = StringUtils.split(faceIds, ",");
			List<String> faceIdList = new ArrayList<>();
			for (String faceId : faceIdArray) {
				faceIdList.add(faceId);
			}
			return profileService.deleteFaces(token, faceIdList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("deleteFaces has an error ,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("删除脸部信息失败");
		}
		return commonResult;
	}

	/**
	 * 删除优图脸部信息
	 * 
	 * @param token
	 * @param faceIds
	 * @return
	 */
	@PostMapping("/deleteyoutufaces")
	@ResponseBody
	@ApiOperation(value = "删除优图中保存的脸部信息（测试接口APP中不需要调用）")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "faceIds", dataType = "String", required = true, value = "脸部信息ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult deleteYoutuFaces(String token, String faceIds) {
		CommonResult commonResult = new CommonResult();
		try {
			String[] faceIdArray = StringUtils.split(faceIds, ",");
			List<String> faceIdList = new ArrayList<>();
			for (String faceId : faceIdArray) {
				faceIdList.add(faceId);
			}
			return profileService.deleteYoutuFaces(token, faceIdList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("deleteFaces has an error ,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("删除脸部信息失败");
		}
		return commonResult;
	}

	@PostMapping("/selectyoutufaces")
	@ResponseBody
	@ApiOperation(value = "查询优图中保存的人脸信息（测试接口APP中不需要调用）")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户的登录token", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectYoutuFaces(String token) {
		CommonResult commonResult = new CommonResult();
		try {
			commonResult.setObj(profileService.selectYoutuFaces(token).toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return commonResult;
	}

}
