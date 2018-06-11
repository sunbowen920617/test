package com.zheman.lock.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.util.JSONUtil;

public class AuthenticationInterceptor implements HandlerInterceptor {

	@Value("${zheman.app.localAppKey}")
	String localAppKey;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		// APP鉴权
		String version = request.getParameter("version");
		String device = request.getParameter("device");
		String timestemp = request.getParameter("timestemp");
		String sign = request.getParameter("sign");
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(version).append(device).append(timestemp).append(localAppKey);
		String md5Sign = DigestUtils.md5DigestAsHex(stringBuffer.toString().getBytes());
		if (!StringUtils.equals(md5Sign, sign)) {
			response.setCharacterEncoding("utf-8");
			CommonResult commonResult = new CommonResult();
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("鉴权失败");
			String json = JSONUtil.parseToString(commonResult);
			response.getWriter().print(json);
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
