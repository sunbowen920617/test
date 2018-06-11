package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.model.enumtion.DictionaryType;
import com.zheman.lock.service.DictionaryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api("数据字典API")
@RequestMapping("/dictionary")
public class DictionaryController {

	private static final Logger log = LoggerFactory.getLogger(DictionaryController.class);

	@Autowired
	DictionaryService dictionaryService;

	/**
	 * 查询证件类型
	 * 
	 * @param token
	 * @param page
	 * @return
	 */
	@PostMapping("/selectidcardbytype")
	@ResponseBody
	@ApiOperation(value = "查询证件类型")
	@ApiImplicitParams({})
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	public CommonResult selectIdcardByType() {
		CommonResult commonResult = new CommonResult();
		try {
			commonResult = dictionaryService.selectDictionaryByType(DictionaryType.IDCARD_TYPE.toString());
		} catch (Exception e) {
			// TODO: handle exception
			log.error("selectIdcardByType has an error:", e);
			e.printStackTrace();
		}
		return commonResult;
	}

}
