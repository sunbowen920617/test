package com.zheman.lock.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.RedisService;

public class LoginTokenInterceptor implements HandlerInterceptor {

	@Autowired
	RedisService redisService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stu
		CommonResult commonResult = new CommonResult();
		commonResult.setCode(CommonResult.INVALID_TOKEN);
		commonResult.setMessage("invalid token");
		String token = request.getParameter("token");
		if (StringUtils.isBlank(token)) {
			Gson gson = new Gson();
			response.getWriter().print(gson.toJson(commonResult));
			return false;
		}
		//查询token是否记录再redis中
		boolean bool = redisService.existsToken(token);
		if (!bool) {
			Gson gson = new Gson();
			response.getWriter().print(gson.toJson(commonResult));
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
