package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.model.enumtion.SmsTypeEnum;
import com.zheman.lock.service.RedisService;
import com.zheman.lock.service.SmsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping("/sms")
@Api("短信验证码相关API")
public class SmsController {

	Logger logger = LoggerFactory.getLogger(SmsController.class);

	@Autowired
	SmsService smsService;

	@Autowired
	RedisService redisService;

	/**
	 * 
	 * 
	 * @param mobilePhone
	 * @return
	 */

	@ApiOperation(value = "发送短信验证码(注册)")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/register")
	@ResponseBody
	public CommonResult register(String mobilePhone) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("验证码已发送");
		try {
			boolean result = smsService.sendSms(mobilePhone, SmsTypeEnum.REGISTER.ordinal());
			if (!result) {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("验证码发送失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("sms/register has an error :" + e.getMessage());
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("验证码发送失败");
		}
		return commonResult;
	}

	@ApiOperation(value = "发送短信验证码(登录)")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/login")
	@ResponseBody
	public CommonResult login(String mobilePhone) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("验证码已发送");
		try {
			boolean result = smsService.sendSms(mobilePhone, SmsTypeEnum.LOGIN.ordinal());
			if (!result) {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("验证码发送失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("sms/login has an error :" + e.getMessage());
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("验证码发送失败");
		}
		return commonResult;
	}

	@ApiOperation(value = "发送短信验证码(找回密码)")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/updatepassword")
	@ResponseBody
	public CommonResult updatepassword(String mobilePhone) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("验证码已发送");
		try {
			boolean result = smsService.sendSms(mobilePhone, SmsTypeEnum.UPDATEPASSWORD.ordinal());
			if (!result) {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("验证码发送失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("sms/login has an error :" + e.getMessage());
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("验证码发送失败");
		}
		return commonResult;
	}
	
	@ApiOperation(value = "发送短信验证码(添加房屋)")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/addroom")
	@ResponseBody
	public CommonResult addRoom(String mobilePhone) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("验证码已发送");
		try {
			boolean result = smsService.sendSms(mobilePhone, SmsTypeEnum.ADDROOM.ordinal());
			if (!result) {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("验证码发送失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("sms/login has an error :" + e.getMessage());
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("验证码发送失败");
		}
		return commonResult;
	}


}
