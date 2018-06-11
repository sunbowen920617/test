package com.zheman.lock.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.YoutuFaceService;

@Aspect
@Component
public class ResetYoutuAspect {

	@Autowired
	YoutuFaceService youtuFaceService;
	
	@AfterReturning(pointcut="execution(* ResetPerson(*))",returning="returnValue")  
	public void resetPerson(JoinPoint joinPoint,Object returnValue) throws Exception {
		CommonResult commonResult=(CommonResult) returnValue;
		youtuFaceService.resetPerson(Long.valueOf(commonResult.getObj().toString()));
	}
	
	
}
