package com.zheman.lock.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.model.enumtion.SmsTypeEnum;
import com.zheman.lock.service.ResidentService;
import com.zheman.lock.service.SmsService;
import com.zheman.lock.util.VerifyUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("住戶相关API")
@RequestMapping("/resident")
public class ResidentController {

	Logger logger = LoggerFactory.getLogger(ResidentController.class);

	@Autowired
	SmsService smsService;

	@Autowired
	ResidentService residentService;

	@PostMapping("/checkmobilephone")
	@ResponseBody
	@ApiOperation(value = "校验手机号码")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult checkMobilePhone(String mobilePhone) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		try {
			// 验证手机号码是否正确
			Boolean bool = VerifyUtil.isMobile(mobilePhone);
			if (!bool) {
				commonResult.setMessage("手机号码格式错误");
			} else {
				// 验证手机号码是否被注册
				bool = residentService.checkMobilePhone(mobilePhone);
				if (bool) {
					commonResult.setMessage("手机号码已经被注册");
				} else {
					commonResult.setCode(CommonResult.SUCCESS);
					commonResult.setMessage("手机号码可以使用");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("checkMobilePhone has error ： " + e.getMessage());
			commonResult.setMessage("手机号码校验失败");
		}
		return commonResult;
	}

	@PostMapping("/register")
	@ResponseBody
	@ApiOperation(value = "注册")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "smsCode", dataType = "String", required = true, value = "短信验证码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "realName", dataType = "String", required = true, value = "真实姓名", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "登录密码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "device", dataType = "String", required = true, value = "设备(ios/android)", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "deviceId", dataType = "String", required = true, value = "设备唯一标识", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "timestemp", dataType = "String", required = true, value = "时间戳", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult register(HttpServletRequest request, String mobilePhone, String smsCode, String realName,
			String password, String device, String deviceId, String timestemp) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		try {
			if (StringUtils.isBlank(mobilePhone)) {
				commonResult.setMessage("手机号码不能为空");
			} else if (StringUtils.isBlank(smsCode)) {
				commonResult.setMessage("短信验证码不能为空");
			} else if (StringUtils.isBlank(realName)) {
				commonResult.setMessage("真是姓名不能为空");
			} else if (StringUtils.isBlank(password)) {
				commonResult.setMessage("密码不能为空");
			} else if (!smsService.validSmsCode(mobilePhone, SmsTypeEnum.REGISTER.ordinal(), smsCode)) {
				commonResult.setMessage("验证码无效或者超时");
			} else {
				boolean bool = residentService.register(mobilePhone, password, realName);
				if (!bool) {
					commonResult.setMessage("注册失败");
				} else {
					// 注册完成直接登录
					String ipAddress = getIpAddr(request);
					commonResult = residentService.loginByPassword(mobilePhone, password, device, ipAddress, deviceId,
							timestemp);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("register has error : " + e.getMessage());
			commonResult.setMessage("注册失败");
		}
		return commonResult;
	}

	@PostMapping("/loginbypassword")
	@ResponseBody
	@ApiOperation(value = "通过密码登录")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "登录密码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "device", dataType = "String", required = true, value = "设备(ios/android)", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "deviceId", dataType = "String", required = true, value = "设备唯一标识", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "timestemp", dataType = "String", required = true, value = "时间戳", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult loginByPassword(HttpServletRequest request, String mobilePhone, String password, String device,
			String deviceId, String timestemp) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		try {
			String ipAddress = getIpAddr(request);
			return residentService.loginByPassword(mobilePhone, password, device, ipAddress, deviceId, timestemp);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			commonResult.setMessage("登录失败");
			logger.error("loginByPassword has error : e", e);
		}
		return commonResult;
	}

	@ApiOperation(value = "通过短信验证码登录")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "smsCode", dataType = "String", required = true, value = "短信验证码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "device", dataType = "String", required = true, value = "设备(ios/android)", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "deviceId", dataType = "String", required = true, value = "设备唯一标识", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "timestemp", dataType = "String", required = true, value = "时间戳", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/loginbysmscode")
	@ResponseBody
	public CommonResult loginBySmsCode(HttpServletRequest request, String mobilePhone, String smsCode, String device,
			String deviceId, String timestemp) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		try {
			String ipAddress = getIpAddr(request);
			return residentService.loginBySmsCode(mobilePhone, smsCode, device, ipAddress, deviceId, timestemp);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			commonResult.setMessage("登录失败");
			logger.error("loginByPassword has error : e" + e.getMessage());
		}
		return commonResult;
	}

	/**
	 * 获取IP地址
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String getIpAddr(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Real-IP");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		} else {
			return request.getRemoteAddr();
		}
	}

	@ApiOperation(value = "修改密码")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "smsCode", dataType = "String", required = true, value = "短信验证码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "密码", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/updatepassword")
	@ResponseBody
	public CommonResult updatePassword(String mobilePhone, String password, String smsCode) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.FAILURE);
		try {
			if (StringUtils.isBlank(mobilePhone)) {
				commonResult.setMessage("手机号码不能为空");
			} else if (StringUtils.isBlank(smsCode)) {
				commonResult.setMessage("短信验证码不能为空");
			} else if (StringUtils.isBlank(password)) {
				commonResult.setMessage("新密码不能为空");
			} else {
				boolean bool = smsService.validSmsCode(mobilePhone, SmsTypeEnum.UPDATEPASSWORD.ordinal(), smsCode);
				if (!bool) {
					commonResult.setMessage("验证码无效");
				} else {
					commonResult.setCode(CommonResult.SUCCESS);
					commonResult.setMessage("密码修改完成");
					residentService.updatePassword(mobilePhone, password);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			commonResult.setMessage("修改密码失败");
			logger.error("resident updatepassword has an error :" + e.getMessage());
			e.printStackTrace();
		}
		return commonResult;
	}

	@ApiOperation(value = "退出")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "登录token", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/logout")
	@ResponseBody
	public CommonResult logout(String token) {
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setObj("ok");
		try {
			residentService.logout(token);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("logout has an error,", e);
		}

		return commonResult;
	}

	@ApiOperation(value = "启动发送token")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "登录token", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "pushToken", dataType = "String", required = true, value = "推送token", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/iospushtoken")
	@ResponseBody
	public CommonResult iosPushToken(String token, String pushToken) throws Exception {
		CommonResult commonResult = new CommonResult();
		try {
			return residentService.iosPushToken(token, pushToken);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("iosPushToken has an error,e", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("");
			commonResult.setObj("ok");
		}
		return commonResult;
	}

	@ApiOperation(value = "注册2步骤1")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "mobilePhone", dataType = "String", required = true, value = "手机号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "smsCode", dataType = "String", required = true, value = "验证码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "密码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = true, value = "名字", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "idcard", dataType = "String", required = true, value = "证件号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "idcardType", dataType = "int", required = true, value = "证件类型", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "type", dataType = "int", required = true, value = "住戶类型(1、业主。2、住户。3、租户)", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/register2step1")
	@ResponseBody
	public CommonResult register2step1(String mobilePhone, String smsCode, String password, String name, String idcard,
			Integer idcardType, Integer type) throws Exception {
		CommonResult commonResult = new CommonResult();
		try {
			if (StringUtils.isBlank(mobilePhone)) {
				commonResult.setMessage("手机号码不能为空");
			} else if (StringUtils.isBlank(smsCode)) {
				commonResult.setMessage("短信验证码不能为空");
			} else if (StringUtils.isBlank(password)) {
				commonResult.setMessage("密码不能为空");
			} else if (!smsService.validSmsCode(mobilePhone, SmsTypeEnum.REGISTER.ordinal(), smsCode)) {
				commonResult.setMessage("验证码无效或者超时");
			} else {
				commonResult = residentService.register2Step1(mobilePhone, smsCode, password, name, idcard, idcardType,
						type);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("register2step1 has an error,e", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("");
			commonResult.setObj("ok");
		}
		return commonResult;
	}

	@ApiOperation(value = "注册2步骤2")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "residentId", dataType = "Long", required = true, value = "住户ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "communityId", dataType = "Long", required = true, value = "社区ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "unitId", dataType = "Long", required = true, value = "单元ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "roomId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/register2step2")
	@ResponseBody
	public CommonResult register2step2(Long residentId, Long communityId, Long unitId, Long roomId) throws Exception {
		CommonResult commonResult = new CommonResult();
		try {
			return residentService.register2Step2(residentId, communityId, unitId, roomId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("register2step2 has an error,e", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("");
			commonResult.setObj("ok");
		}
		return commonResult;
	}

	@ApiOperation(value = "注册2步骤3")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "validateCode", dataType = "String", required = true, value = "唇语验证码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "residentId", dataType = "Long", required = true, value = "住户ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "file", dataType = "MultipartFile", required = true, value = "文件", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "idcardNumber", dataType = "String", required = true, value = "身份证号码", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "idcardName", dataType = "String", required = true, value = "名字", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "deviceId", dataType = "String", required = true, value = "设备ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "timestemp", dataType = "String", required = true, value = "时间戳", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "device", dataType = "String", required = true, value = "设备（1、ios。2、android）", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/register2step3two")
	@ResponseBody
	public CommonResult register2step3two(HttpServletRequest request, String validateCode, Long residentId,
			MultipartFile file, String idcardNumber, String idcardName, String deviceId, String timestemp,
			String device) throws Exception {
		CommonResult commonResult = new CommonResult();
		try {
			commonResult = residentService.register2step3(validateCode, residentId, file, idcardNumber, idcardName);
			if (commonResult.getCode() == CommonResult.FAILURE) {
				
			} else {
				// 注册完成直接登录
				String ipAddress = getIpAddr(request);
				Map<String, String> result = residentService.loginByAfterRegister(ipAddress, residentId, device,
						deviceId, timestemp);
				commonResult.setObj(result);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("register2step2 has an error,e", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("");
			commonResult.setObj("ok");
		}
		return commonResult;
	}

}
