package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.CallService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("通话记录相关API")
@RequestMapping("/call")
public class CallController {
	Logger logger = LoggerFactory.getLogger(CallController.class);

	@Autowired
	CallService callService;

	/**
	 * 获取住户当前通话标识
	 * 
	 * @param callBack
	 * @return
	 */
	@PostMapping("/selectCall")
	@ResponseBody
	@ApiOperation(value = "获取住户当前通话标识")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "callId", dataType = "Long", required = true, value = "呼叫记录ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectCall(long callId) {
		CommonResult commonResult = new CommonResult();
		try {
			return callService.selectCall(callId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("newlyCall has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("调用接口失败");
		}
		return commonResult;
	}

	/**
	 * 呼叫反馈
	 * 
	 * @param token
	 * @param type
	 * @param timestemp
	 * @return
	 */
	@PostMapping("/callfeedback")
	@ResponseBody
	@ApiOperation(value = "呼叫反馈")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "callId", dataType = "Long", required = true, value = "通话记录ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "Integer", required = true, value = "回调类型(1、语音接听。2、视频接听。3、挂断。4、拒绝开锁。5、开锁)", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "timestemp", dataType = "Long", required = true, value = "呼叫的时间戳", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult callFeedback(long callId, int type, long timestemp) {
		CommonResult commonResult = new CommonResult();
		try {
			switch (type) {
			case 1:
				return callService.voiceAnswer(callId, timestemp);
			case 2:
				return callService.vedioAnswer(callId, timestemp);
			case 3:
				return callService.hangup(callId, timestemp);
			case 4:
				return callService.reject(callId, timestemp);
			case 5:
				return callService.unlock(callId, timestemp);
			default:
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("回调类型错误");
				commonResult.setObj("ok");
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("callFeedback has an error,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("回调失败");
		}
		return commonResult;
	}

	/**
	 * 查询登录用户 的通话记录
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/selectcalls")
	@ResponseBody
	@ApiOperation(value = "查询登录用户 的通话记录")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "登陆之后 的token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "page", dataType = "Integer", required = true, value = "下一页", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "size", dataType = "Integer", required = true, value = "每页大小", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectCalls(String token, int page, int size) {
		CommonResult commonResult = new CommonResult();
		try {
			commonResult = callService.selectCalls(token, page, size);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectCalls has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 查询当前住户的通话状态
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/selectcurrentcall")
	@ResponseBody
	@ApiOperation(value = "查询当前住户的通话状态")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "登陆之后 的token", defaultValue = ""),})
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectCurrentCall(String token) {
		CommonResult commonResult = new CommonResult();
		try {
			return callService.selectCurrentCall(token);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectCurrentCall has an error, e", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

}
