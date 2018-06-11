package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.NoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("通知列表API")
@RequestMapping("/notice")
public class NoticeController {

	Logger logger = LoggerFactory.getLogger(NoticeController.class);

	@Autowired
	NoticeService noticeService;

	/**
	 * 查询住户通知
	 * 
	 * @param token
	 * @param page
	 * @return
	 */
	@PostMapping("/selectresidentnotices")
	@ResponseBody
	@ApiOperation(value = "查询住户所有通知")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "page", dataType = "Integer", required = true, value = "当前页", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "size", dataType = "Integer", required = true, value = "当前页", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectResidentNotices(String token, int page, int size) {
		CommonResult commonResult = new CommonResult();
		try {
			return noticeService.selectNoticesByToken(token, page, size);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectResidentNotices has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 根据ID查询单个通知
	 * 
	 * @param token
	 * @param page
	 * @return
	 */
	@PostMapping("/selectresidentnotice")
	@ResponseBody
	@ApiOperation(value = "查询住户单个通知")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "noticeId", dataType = "Long", required = true, value = "通知ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectResidentNotice(long noticeId) {
		CommonResult commonResult = new CommonResult();
		try {
			return noticeService.selectNoticeById(noticeId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectResidentNotices has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

}
