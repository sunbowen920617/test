package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.AdvertService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("广告相关API")
@RequestMapping("/advert")
public class AdvertController {

	private static final Logger log = LoggerFactory.getLogger(AdvertController.class);

	@Autowired
	AdvertService advertService;

	/**
	 * 查询首页广告
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/gethomepageadvert")
	@ResponseBody
	@ApiOperation(value = "查询首页广告")
	@ApiImplicitParams({})
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult getHomePageAdvert() {
		CommonResult commonResult = new CommonResult();
		try {
			return advertService.getHomePageAdvert();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("getHomePageAdvert has an error,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 查询首页广告
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/getroomlistadvert")
	@ResponseBody
	@ApiOperation(value = "房屋列表广告")
	@ApiImplicitParams({})
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult getRoomListAdvert() {
		CommonResult commonResult = new CommonResult();
		try {
			return advertService.getRoomListAdvert();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("getHomePageAdvert has an error,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 广告点击事件
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("/advertclicked")
	@ResponseBody
	@ApiOperation(value = "广告点击事件")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "token", dataType = "String", required = true, value = "住户ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "advertId", dataType = "Long", required = true, value = "广告ID", defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "timestemp", dataType = "Long", required = true, value = "点击的时间戳", defaultValue = ""),})
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult advertClicked(String token, long advertId, long timestemp) {
		CommonResult commonResult = new CommonResult();
		try {
			advertService.advertClicked(token, advertId, timestemp);
			commonResult.setCode(CommonResult.SUCCESS);
			commonResult.setObj("ok");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("getHomePageAdvert has an error,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

}
