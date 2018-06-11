package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.FeedbackService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("反馈信息API")
@RequestMapping("/feedback")
public class FeedbackController {

	private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);

	@Autowired
	FeedbackService feedbackService;

	/**
	 * 查询住户的所有通知
	 * 
	 * @param token
	 * @param page
	 * @return
	 */
	@PostMapping("/selectfeedbacks")
	@ResponseBody
	@ApiOperation(value = "查询住户的所有通知")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "手机号码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectFeedbacksByResidentId(String token) {
		CommonResult commonResult = new CommonResult();
		try {
			return feedbackService.selectFeedbacksByResidentId(token);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("selectFeedbacksByResidentId has an error,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 添加通知
	 * 
	 * @param token
	 * @param page
	 * @return
	 */
	@PostMapping("/addFeedback")
	@ResponseBody
	@ApiOperation(value = "添加通知")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "message", dataType = "String", required = true, value = "消息", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult addFeedback(String token, String message) {
		CommonResult commonResult = new CommonResult();
		try {
			return feedbackService.addFeedback(token, message);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("selectFeedbacksByResidentId has an error,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("添加失败");
		}
		return commonResult;
	}

	/**
	 * 查询单个反馈信息
	 * 
	 * @param token
	 * @param page
	 * @return
	 */
	@PostMapping("/selectfeedback")
	@ResponseBody
	@ApiOperation(value = "查询单个反馈信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "feedbackId", dataType = "Long", required = true, value = "单个反馈信息的ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectFeedback(long feedbackId) {
		CommonResult commonResult = new CommonResult();
		try {
			return feedbackService.selectFeedback(feedbackId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("selectFeedback has an error,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}
}
