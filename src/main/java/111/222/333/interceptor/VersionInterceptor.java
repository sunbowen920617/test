package com.zheman.lock.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.SystemInfoService;
import com.zheman.lock.util.JSONUtil;

public class VersionInterceptor implements HandlerInterceptor {

	@Autowired
	SystemInfoService systemInfoService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		String version = request.getParameter("version");
		String device = request.getParameter("device");
		String dbVersion = systemInfoService.getVersion(device);
		response.setCharacterEncoding("utf-8");
		if (null == dbVersion) {
			return true;
		}
		int result = version.compareTo(dbVersion);
		if (result < 0) {
			CommonResult commonResult = new CommonResult();
			commonResult.setCode(CommonResult.INVALID_VERSION);
			commonResult.setMessage("无效版本");
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
