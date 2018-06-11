package com.zheman.lock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.TencentVedioService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping("/tencentvedio")
@Api("腾讯视频API")
public class TencentVedioController {

	@Autowired
	TencentVedioService tencentVedioService;

	/**
	 * 账户导入
	 * 
	 * @return
	 */
	@ApiOperation(value = "腾讯云视频账户导入")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "nick", dataType = "String", required = true, value = "昵称", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/accountimport")
	@ResponseBody
	public CommonResult accountImport(String mobilePhone, String nick) {
		CommonResult commonResult = new CommonResult();
		try {
			tencentVedioService.accountImport(mobilePhone, nick, "");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return commonResult;
	}

	/**
	 * 获取账户的在线状态
	 * 
	 * @return
	 */
	@ApiOperation(value = "获取账户的在线状态")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/querystate")
	@ResponseBody
	public CommonResult queryState(String mobilePhone) {
		CommonResult commonResult = new CommonResult();
		try {
			tencentVedioService.queryState(mobilePhone);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return commonResult;
	}
}
