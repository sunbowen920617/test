package com.zheman.lock.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.RoomService;
import com.zheman.lock.service.ShareService;
import com.zheman.lock.util.VerifyUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("权限共享相关API")
@RequestMapping("/share")
public class ShareController {
	Logger logger = LoggerFactory.getLogger(ShareController.class);

	@Autowired
	RoomService roomService;

	@Autowired
	ShareService shareService;

	/**
	 * 查询房屋的共享列表
	 * 
	 * @param token
	 * @param mobilePhone
	 * @return
	 */
	@ApiOperation(value = "获取房屋共享列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/findallroomshare")
	@ResponseBody
	public CommonResult findAllRoomShare(String token, Long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			return shareService.selectRoomShare(token, roomId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
			logger.error("findAllRoomShare:", e);
		}
		return commonResult;
	}

	/**
	 * 生成房屋共享二维码
	 * 
	 * @param token
	 * @param roomId
	 * @return
	 */
	@ApiOperation(value = "生成房屋共享二维码")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/genshareqrcode")
	@ResponseBody
	public CommonResult genShareQrcode(HttpServletRequest request, String token, long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			String strBackUrl = "http://" + request.getServerName() // 服务器地址
					+ ":" + request.getServerPort() // 端口号
					+ request.getContextPath();
			return roomService.genShareQrCode(strBackUrl, token, roomId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("生成二维码失败");
			logger.error("genshareqrcode has an error : ", e);
		}
		return commonResult;
	}

	/**
	 * 通过二维码方式共享权限
	 * 
	 * @param token
	 * @param timestemp
	 * @param inviterId
	 * @param roomId
	 */
	@ApiOperation(value = "通过二维码方式共享权限")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "timestemp", dataType = "Long", required = true, value = "时间戳", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "shareId", dataType = "Long", required = true, value = "邀请ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/sharebyqrcode")
	@ResponseBody
	public CommonResult shareByQrcode(String token, long timestemp, long shareId) {
		CommonResult commonResult = new CommonResult();
		try {
			return roomService.bindRoomByQrcodeResetPerson(token, timestemp, shareId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("shareByQrcode has an error ", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("权限共享失败");
			// TODO: handle exception
		}
		return commonResult;
	}

	/**
	 * 通过账户邀请共享权限
	 * 
	 * @param token
	 * @param inviterId
	 * @param roomId
	 * @return
	 */
	@ApiOperation(value = "通过账户邀请共享权限")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/sharebyaccount")
	@ResponseBody
	public CommonResult shareByAccount(String token, String mobilePhone, long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			boolean bool = VerifyUtil.isMobile(mobilePhone);
			if (!bool) {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("手机号码格式不正确");
				return commonResult;
			}
			return roomService.shareByAccount(token, mobilePhone, roomId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("shareByAccout has an error ", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("邀请失败");
			// TODO: handle exception
		}
		return commonResult;
	}

	/**
	 * 通过账户共享房屋权限feedback
	 * 
	 * @param token
	 * @param shareId
	 *            共享信息ID
	 * @param roomId
	 *            房屋ID
	 * @param result
	 *            反馈结果
	 * @return
	 */
	@ApiOperation(value = "通过账户共享房屋权限feedback")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "shareId", dataType = "Long", required = true, value = "共享ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "result", dataType = "Integer", required = true, value = "反馈结果(1、接受。2、拒绝)", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/sharebyaccountfeedback")
	@ResponseBody
	public CommonResult shareByAccountFeedback(String token, long shareId, int result) {
		CommonResult commonResult = new CommonResult();
		try {
			return shareService.shareByAccountFeedbackResetPerson(token, shareId, result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("shareByAccountFeedback has an error:", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("接口调用失败");
		}
		return commonResult;
	}

	/**
	 * 开启住户共享权限
	 * 
	 * @param token
	 * @param inviterId
	 * @param roomId
	 * @return
	 */
	@ApiOperation(value = "开启住户共享权限")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "residentId", dataType = "Long", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/enableshare")
	@ResponseBody
	public CommonResult enableShare(String token, long residentId, long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			return shareService.enableShare(roomId, residentId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("shareByAccout has an error ", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("开始共享权限失败");
			// TODO: handle exception
		}
		return commonResult;
	}

	/**
	 * 禁用住户共享权限
	 * 
	 * @param token
	 * @param inviterId
	 * @param roomId
	 * @return
	 */
	@ApiOperation(value = "禁用住户共享权限")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "用户登录之后的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "residentId", dataType = "Long", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/disableshare")
	@ResponseBody
	public CommonResult disableShare(String token, long residentId, long roomId) {
		CommonResult commonResult = new CommonResult();
		try {
			return shareService.disableShare(roomId, residentId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("shareByAccout has an error ", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("禁用共享权限失败");
			// TODO: handle exception
		}
		return commonResult;
	}

}
