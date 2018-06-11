package com.zheman.lock.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.dao.ResidentMapper;
import com.zheman.lock.model.Resident;
import com.zheman.lock.service.RedisService;
import com.zheman.lock.service.TencentImageService;
import com.zheman.lock.util.JSONUtil;

public class RegisterStateInterceptor implements HandlerInterceptor {

	@Autowired
	RedisService redisService;

	@Autowired
	ResidentMapper residentMapper;

	@Autowired
	TencentImageService tencentImageService;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		CommonResult commonResult = new CommonResult();
		String mobilePhone = request.getParameter("mobilePhone");
		Resident resident = residentMapper.selectByMobilePhone(mobilePhone);
		if (null == resident)
			return true;
		if (resident.getRegisterstep() == -1)
			return true;
		if (request.getRequestURI().indexOf("loginbypassword") > -1) {
			String password = request.getParameter("password");
			password = DigestUtils.md5Hex(password);
			if (!StringUtils.equals(password, resident.getPassword())) {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("密码错误");
				writerJson(commonResult, arg1);
				return false;
			}
		}
		if (request.getRequestURI().indexOf("loginbysmscode") > -1) {
			String smsCode = request.getParameter("smsCode");
			String redisSmsCode = redisService.getLoginSmsCode(mobilePhone);
			if (!StringUtils.equals(smsCode, redisSmsCode)) {
				commonResult.setCode(CommonResult.FAILURE);
				commonResult.setMessage("验证码无效");
				writerJson(commonResult, arg1);
				return false;
			}
			redisService.removeLoginSmsCode(mobilePhone);
		}
		Map<String, String> result = new HashMap<>();
		result.put("residentId", resident.getId().toString());
		result.put("idcardNumber", resident.getIdcardnumber());
		result.put("name", resident.getName());
		result.put("registerStep", String.valueOf(resident.getRegisterstep()));
		String code = "";
		if (resident.getRegisterstep() == 3) {
			code = tencentImageService.liveGetFour();
			result.put("validateData", code);
		}
		commonResult.setCode(CommonResult.SUCCESS);
		commonResult.setMessage("");
		commonResult.setObj(result);
		writerJson(commonResult, arg1);
		return false;
	}

	private void writerJson(CommonResult commonResult,HttpServletResponse response) throws Exception {
		String json = JSONUtil.parseToString(commonResult);
		response.setContentType("text/json;charset=UTF-8");
		response.getWriter().println(json);
		response.getWriter().flush();
		response.getWriter().close();
	}

}
