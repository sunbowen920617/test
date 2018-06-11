package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.model.trans.TransResidentRoom;
import com.zheman.lock.service.RoomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("房屋相关API")
@RequestMapping("/room")
public class RoomController {
	Logger logger = LoggerFactory.getLogger(RoomController.class);

	@Autowired
	RoomService roomService;

	/**
	 * 根据登录token查询到房屋信息
	 * 
	 * @param token
	 * @param mobilePhone
	 * @return
	 */
	@ApiOperation(value = "获取房屋列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/findallroomsbylogintoken")
	@ResponseBody
	public CommonResult findRoomsByToken(String token) {
		CommonResult commonResult = new CommonResult();
		try {
			return roomService.findAllRoomsByLogin(token);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
			logger.error("findRoomsByToken:", e);
		}
		return commonResult;
	}

	/**
	 * 根据ID查询房屋信息
	 * 
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "查询单个房屋")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "查询房屋的id", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/findbyroomid")
	@ResponseBody
	public CommonResult findByRoomId(String token, long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			TransResidentRoom transResidentRoom = roomService.findByRoomIdAndResidentId(token, roomId);
			commonResult.setObj(transResidentRoom);
			commonResult.setCode(CommonResult.SUCCESS);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("findByRoomId has an error e:", e);
			e.printStackTrace();
			commonResult.setMessage("查询失败");
			commonResult.setCode(CommonResult.FAILURE);
		}
		return commonResult;
	}

	/**
	 * 添加房屋
	 * 
	 * @param token
	 *            登录用户的token
	 * @param communityId
	 *            社区ID
	 * @param unitId
	 *            单元ID
	 * @param roomId
	 *            房屋ID
	 * @param mobilePhone
	 *            手机号码
	 * @param smsCode
	 *            短信码
	 * @return
	 */
	@ApiOperation(value = "添加房屋信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "communityId", dataType = "Long", required = true, value = "社区ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "unitId", dataType = "Long", required = true, value = "社区ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "smsCode", dataType = "String", required = true, value = "短信验证码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/addroom")
	@ResponseBody
	public CommonResult addRoom(String token, long communityId, long unitId, long roomId, String mobilePhone,
			String smsCode) {
		CommonResult commonResult = new CommonResult();
		try {
			return roomService.addRoomResetPerson(token, communityId, unitId, roomId, mobilePhone, smsCode);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("addRoom has an error : ", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("添加房屋失败");
			e.printStackTrace();
		}
		return commonResult;
	}

	/**
	 * app内主动开锁
	 * 
	 * @param token
	 *            登录用户的token
	 * @param roomId
	 *            开锁的房屋ID
	 * @return
	 */
	@ApiOperation(value = "APP内主动开锁")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/unlockbyapp")
	@ResponseBody
	public CommonResult unlockByApp(String token, long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			return roomService.unlockByApp(token, roomId);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("unlock has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("开锁失败");
			e.printStackTrace();
		}
		return commonResult;
	}

	/**
	 * 取消房屋和住户的关联
	 * 
	 * @param token
	 * @param residentRoomId
	 * @return
	 */
	@ApiOperation(value = "取消房屋和住户的关联")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "residentId", dataType = "Long", required = true, value = "住户ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/cancelshare")
	@ResponseBody
	public CommonResult deleteResidentRoom(long roomId, long residentId) {
		CommonResult commonResult = new CommonResult();
		try {
			return roomService.cancelShareResetPerson(residentId, roomId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("shareByAccout has an error ", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("取消共享权限失败");
			// TODO: handle exception
		}
		return commonResult;
	}

}
