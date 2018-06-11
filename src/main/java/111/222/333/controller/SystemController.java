package com.zheman.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zheman.lock.common.CommonResult;
import com.zheman.lock.service.CommunityService;
import com.zheman.lock.service.RoomService;
import com.zheman.lock.service.UnitService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping("/system")
@Api("系统相关API")
public class SystemController {

	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

	@Autowired
	CommunityService communityService;

	@Autowired
	UnitService unitService;

	@Autowired
	RoomService roomService;

	/**
	 * 查询所有社区信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "查询所有社区信息")
	@ApiImplicitParams({})
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/selectcommunitys")
	@ResponseBody
	public CommonResult selectCommunitys() {
		CommonResult commonResult = new CommonResult();
		try {
			return communityService.selectCommunitys();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectCommunitys has an error :", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 查询所有单元信息
	 * 
	 * @param communityId
	 * @return
	 */
	@ApiOperation(value = "查询所有单元信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "communityId", dataType = "Long", required = true, value = "社区ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/selectunits")
	@ResponseBody
	public CommonResult selectUnitsByCommunityId(long communityId) {
		CommonResult commonResult = new CommonResult();
		try {
			return unitService.selectByCommunityId(communityId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectUnits has an error;", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 查询单元下的所有房屋
	 * 
	 * @param unitId
	 * @return
	 */
	@ApiOperation(value = "查询单元下的所有房屋")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "unitId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/selectrooms")
	@ResponseBody
	public CommonResult selectRoomsByUnitId(long unitId) {
		CommonResult commonResult = new CommonResult();
		try {
			return roomService.selectRoomsByUnitId(unitId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectRoomsByUnitId has an error ,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

	/**
	 * 查询单元下的所有房屋
	 * 
	 * @param unitId
	 * @return
	 */
	@ApiOperation(value = "查询单元下的所有房屋")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "unitId", dataType = "Long", required = true, value = "房屋ID", defaultValue = ""), })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@PostMapping("/selectroomsnothaveresident")
	@ResponseBody
	public CommonResult selectRoomsNotHaveResident(long unitId) {
		CommonResult commonResult = new CommonResult();
		try {
			return roomService.selectRoomsNotHaveResident(unitId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("selectRoomsByUnitId has an error ,", e);
			commonResult.setCode(CommonResult.FAILURE);
			commonResult.setMessage("查询失败");
		}
		return commonResult;
	}

}
